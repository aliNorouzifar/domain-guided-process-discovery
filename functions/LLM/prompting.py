import google.generativeai as genai

import re
import json
import requests

single_par_temps = {"AtLeast1", "AtMost1", }
two_par_temps = {"NotCoExistence","Precedence", "NotSuccession", "Response", "RespondedExistence","CoExistence"}
allowed_templates = single_par_temps.union(two_par_temps)

def configure_Gemini(api_key):
    genai.configure(api_key=api_key)
    model = genai.GenerativeModel("gemini-2.5-flash")
    return model

def task_description():
    with open(r"functions\LLM\task_description_fewshot3_q.txt", "r", encoding="utf-8") as f:
        task_prompt = f.read()
    # task_prompt += (
    #         "\n\nThe list of activities used in this process are the following. "
    #         "Please only use these activities to generate constraints:\n"
    #         + ", ".join(activities)
    # )
    # chat = model.start_chat(history=[
    #     {"role": "user", "parts": [task_prompt]}])
    return task_prompt


def extract_json_from_llm_response(text):
    # Remove triple backticks and optional `json` language tag
    cleaned = re.sub(r"^```json\s*|\s*```$", "", text.strip(), flags=re.IGNORECASE)
    try:
        return json.loads(cleaned)
    except json.JSONDecodeError as e:
        print("⚠️ Failed to parse JSON:", e)
        return None



def extract_json_from_llm_response2(text, act_list):
    """
    Classify and validate potential JSON content in text.

    Returns:
        json_flag, error_message, parsed
    """

    cleaned = re.sub(r"^```json\s*|\s*```$", "", text.strip(), flags=re.IGNORECASE)

    # Check 1: Is the output json formatted?
    json_like = cleaned.strip().startswith("{") and cleaned.strip().endswith("}")
    if not json_like:
        return False, False, False

    # Check 2: Is the json format correct?
    try:
        parsed = json.loads(cleaned)
    except json.JSONDecodeError as e:
        error_message = "The provided JSON output contains syntax errors. Please check the formatting and ensure it is valid JSON."
        return True, error_message, False

    # Check 3: Is the json file a dictionary?
    if not isinstance(parsed, dict):
        error_message = "The JSON output must be a dictionary. Please refer to the instructions for the expected JSON structure."
        return True, error_message, False

    # Check 4: Does the json file dictionary include all required key and value formats?
    if set(parsed.keys()) != {"constraints"}:
        error_message = "The top-level dictionary in the JSON output should contain only one key: constraints. Please refer to the instructions for the expected JSON structure."
        return True, error_message, False
    elif not isinstance(parsed["constraints"],list):
        error_message = "The constraints field in the JSON output must be a list of extracted constraints. In your output, it is not formatted as a list. Please refer to the instructions for the correct JSON structure."
        return True, error_message, False
    else:
        error_message = ""
        for r in parsed["constraints"]:
            if not isinstance(r, dict):
                error_message += f"Each extracted constraint should be represented as a dictionary detailing its characteristics. For rule {r}, the constraint is not provided in the correct dictionary format. Please refer to the instructions for the correct JSON structure."
            else:
                if not set(r.keys()) == {'template','parameters'}:
                    error_message += f"Each extracted constraint must be represented as a dictionary containing two main keys: template and parameters. For constraint {r}, one or both of these keys are missing."
                else:
                    if r['template'] not in allowed_templates:
                        error_message += f"The extracted template for rule {r} is not among the templates supported by our framework: {allowed_templates}."
                    else:
                        if r['template'] in single_par_temps:
                            if len(r['parameters'])!= 1:
                                error_message += f"For the extracted rule {r}, the number of parameters should be one, as the templates in {single_par_temps} support only a single activity as a parameter."
                            else:
                                for p in r['parameters']:
                                    if not isinstance(p,list):
                                        error_message += f"Each parameter in rule {r} should be represented as a list. However, parameter {p} is not formatted as a list."
                                    elif len(p)!=1:
                                        error_message += f"Each parameter in rule {r} should contain exactly one activity. However, this is not the case for parameter {p}."
                                    elif p[0] not in act_list:
                                        error_message += f"Activity {p} in rule {r} is not included in the list of activities extracted from the event log, i.e., {act_list}. Only activities observed in the log are allowed."
                        else:
                            if len(r['parameters'])!= 2:
                                error_message += f"For the extracted rule {r}, the number of parameters should be two, as the templates in {two_par_temps} support two activities as parameters."
                            else:
                                for p in r['parameters']:
                                    if not isinstance(p, list):
                                        error_message += f"Each parameter in rule {r} should be represented as a list. However, parameter {p} is not formatted as a list."
                                    elif len(p) != 1:
                                        error_message += f"Each parameter in rule {r} should contain exactly one activity. However, this is not the case for parameter {p}."
                                    elif p[0] not in act_list:
                                        error_message += f"Activity {p} in rule {r} is not included in the list of activities extracted from the event log, i.e., {act_list}. Only activities observed in the log are allowed."

        if error_message != "":
            return True, error_message, False
    return True, False, parsed


def com_message(user_text, system_text, history, model, provider,MODEL_NAME):
    new_message = []
    if provider=="OpenAI":
        if system_text!='':
            new_message.append({"role": "system", "content": system_text})
        if user_text !='':
            new_message.append({"role": "user", "content": user_text})

        messages = history + new_message
        response = model.chat.completions.create(
            model=MODEL_NAME,
            messages=messages,
            # temperature=0,
        )
        response_txt = response.choices[0].message.content

        history += new_message
        history.append({"role": "assistant", "content": response_txt})

    elif provider=="Google":
        new_message.append({'role': 'user', 'parts': [f"{system_text}\n\n{user_text}"]})
        messages = history + new_message
        response = model.generate_content(messages)
        response_txt = response.text

        history += new_message
        history.append({"role": "model", "parts": [response_txt]})

    elif provider=="deepseek":
        if system_text!='':
            new_message.append({"role": "system", "content": system_text})
        if user_text !='':
            new_message.append({"role": "user", "content": user_text})

        messages = history + new_message
        payload = {
            "model": MODEL_NAME,
            "messages": messages
        }

        response = requests.post(model["url"]+"/chat/completions", headers=model["headers"], json=payload)
        result = response.json()
        response_txt = result["choices"][0]["message"]["content"]
        history += new_message
        history.append({"role": "assistant", "content": response_txt})

    else:
        return "error", history

    return response_txt, history


