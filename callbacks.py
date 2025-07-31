from dash import Input, Output, State, html
# from pages.main_page import rule_src_selection,show_rule_uploder,show_Minerful_params, IMr_params_show, rule_related_statistics_show,conformance_related_statistics_show,show_petri_net,IMr_no_rules_params_show
from pages.LLM import show_petri_net_LLM
import os
import shutil
from prolysis.calls.minerful_calls import discover_declare
from pathlib import Path
from prolysis.discovery.discovery import run_IMr
import json
from prolysis.analysis.evaluation import conformance_checking,extract_significant_dev
from prolysis.util.redis_connection import redis_client
from prolysis.rules_handling.utils import rules_from_json
import time
import google.generativeai as genai
from functions.LLM.prompting import extract_json_from_llm_response,extract_json_from_llm_response2, com_message, task_description
from functions.LLM.gemini_chat_manager import get_chat, append_to_history
from functions.utils import read_json_file
import pandas as pd
import pm4py
from dash_chat import ChatComponent
from enum import Enum
from functions.encoding_functions import parse_constraints
import dash
from functions.subprocess_calls import measurement_extraction
from openai import OpenAI


UPLOAD_FOLDER = "event_logs"
OUTPUT_FOLDER = "output_files"
WINDOWS = []


class AIProviders(Enum):
    GOOGLE = "Google"
    OPENAI = "OpenAI"
    DEEPSEEK = "DeepSeek"
    ANTHROPIC = "Anthropic"
    DEEPINFRA = "Deepinfra"
    MISTRAL_AI = "Mistral AI"
    OPENROUTER = "OpenRouter"
    COHERE = "Cohere"
    GROK = "Grok"

AI_MODEL_DEFAULTS = {
    AIProviders.GOOGLE.value: "gemini-2.5-flash",
    AIProviders.OPENAI.value: "gpt-4.1",
    AIProviders.DEEPSEEK.value: "deepseek-reasoner",
    AIProviders.ANTHROPIC.value: "claude-3-7-sonnet-latest",
    AIProviders.DEEPINFRA.value: "meta-llama/Llama-3.2-90B-Vision-Instruct",
    AIProviders.MISTRAL_AI.value: "mistral-large-latest",
    AIProviders.OPENROUTER.value: "mistralai/devstral-small:free",
    AIProviders.COHERE.value: "command-r-plus",
    AIProviders.GROK.value: "grok-3",
}


def clear_upload_folder(folder_path):
    shutil.rmtree(folder_path)
    os.makedirs(folder_path)

def register_callbacks(app):
    clear_upload_folder("event_logs")
    clear_upload_folder("output_files")

    @app.callback(
        # Output("output-data-upload2", "children"),
        # Output("chat-box-wrapper", "children"),
        Output("chat-placeholder", "children"),
        [Input("upload-data", "isCompleted")],
        [State("upload-data", "upload_id")],
    )
    def parameters_IMr(isCompleted,log_file):
        if isCompleted==True:
            # dim = ""
            # abs_thr = ""
            input_log_path = os.path.join(UPLOAD_FOLDER, log_file)
            files = os.listdir(input_log_path) if os.path.exists(input_log_path) else []
            log_path = Path(UPLOAD_FOLDER) / f"{log_file}" / files[0]
            # rules = json.loads(redis_client.get('rules'))
            event_log_xes = pm4py.read_xes(str(log_path), variant="rustxes")
            activities = event_log_xes["concept:name"].unique()
            redis_client.set('activities', json.dumps(list(activities)))
            return "Initialize an LLM to start interacting with the assistant."
        else:
            return dash.no_update




    @app.callback(
        Output("petri-net-graph-container", "children"),
        Output("petri-net-placeholder", "style"),
        Input('run_IMr_selector_LLM', "n_clicks"),
        State("upload-data", "upload_id"),
        State("upload-data", "isCompleted"),
        State("sup_IMr_val", "value"),
        State("rule-table", "selectedRows")
    )
    def IMr_call_no_rules(n1, log_file, isCompleted, sup,sel_rules):

        if isCompleted:
            dim = ""
            abs_thr=""
            input_log_path = os.path.join(UPLOAD_FOLDER, log_file)
            files = os.listdir(input_log_path) if os.path.exists(input_log_path) else []
            log_path = Path(UPLOAD_FOLDER) / f"{log_file}" / files[0]
            if sel_rules:
                dim = "support"
                abs_thr = 0
                rules = []
                from functions.encoding_functions import encode, parse_constraints
                for sel_rule in sel_rules:
                    r = parse_constraints(sel_rule['rule'])
                    if 'support' not in r.keys():
                        r['support']=1
                    if 'confidence' not in r.keys():
                        r['confidence']=1
                    rules.append(r)
                redis_client.set('rules', json.dumps(rules))
            else:
                rules = json.loads(redis_client.get('rules'))
            # event_log_xes = pm4py.read_xes(str(log_path), variant="rustxes")
            # activities = event_log_xes["concept:name"].unique()
            # redis_client.set('activities', json.dumps(list(activities)))
            activities = json.loads(redis_client.get('activities'))

            if n1>0:
                #todo event log is once imported here and once in prolysis code
                # import pm4py
                # event_log_xes = pm4py.read_xes(str(log_path), variant="rustxes")
                # activities = event_log_xes["concept:name"].unique()
                # redis_client.set('activities', json.dumps(list(activities)))
                gviz = run_IMr(log_path, sup, rules, activities, dim, abs_thr)
                redis_client.set('graph_viz', gviz.source)
                return show_petri_net_LLM(gviz), {"display": "none"}
            return dash.no_update, dash.no_update

    @app.callback(
        Output("gv", "style"),  # Update the style property of the graph
        Input("zoom-slider", "value"),  # Listen to the slider value
    )
    def update_zoom(zoom_value):
        # Dynamically update the CSS `transform: scale()` property
        return {"transform": f"scale({zoom_value})", "transformOrigin": "0 0"}

    # @app.callback(
    #     Output("graph-wrapper", "style"),
    #     Input("zoom-slider", "value"),
    # )
    # def update_zoom(zoom_value):
    #     return {
    #         "transform": f"scale({zoom_value})",
    #         "transformOrigin": "0 0",
    #         "width": "max-content",
    #     }



    @app.callback(
        Input('remove_inputs', "n_clicks"),
        prevent_initial_call=True
    )
    def remove_inputs(n):
        if n>0:
            if not os.path.exists(r"event_logs"):
                os.makedirs(r"event_logs")
            else:
                clear_upload_folder(r"event_logs")





################ NEW ##################
    # @app.callback(
    #     Output("input-text", "disabled"),
    #     Output("output-text", "disabled"),
    #     Output("submit-button", "disabled"),
    #     Input("domain-knowledge-radio", "value"),
    # )
    # def toggle_text_fields(has_knowledge):
    #     if has_knowledge == "yes":
    #         return False, False, False
    #     return True, True, True


    # @app.callback(
    #     Output("output-text", "value"),
    #     Input("submit-button", "n_clicks"),
    #     State("input-text", "value"),
    #     State("upload-data", "upload_id"),
    #     prevent_initial_call=True
    # )
    # def handle_user_input(n_clicks, user_input,log_file):
    #     if not user_input:
    #         return "Please enter some process description."
    #
    #     try:
    #         print(n_clicks)
    #         if n_clicks==1:
    #             input_log_path = os.path.join(UPLOAD_FOLDER, log_file)
    #             files = os.listdir(input_log_path) if os.path.exists(input_log_path) else []
    #             log_path = Path(UPLOAD_FOLDER) / f"{log_file}" / files[0]
    #             # todo event log is once imported here and once in prolysis code
    #             import pm4py
    #             event_log_xes = pm4py.read_xes(str(log_path), variant="rustxes")
    #             activities = event_log_xes["concept:name"].unique()
    #             redis_client.set('activities', json.dumps(list(activities)))
    #             chat = prompting.task_description(model, activities)
    #         response = chat.send_message(user_input)
    #         return response.text
    #     except Exception as e:
    #         return f"Error: {str(e)}"

    # @app.callback(
    #     Output("output-text", "value"),
    #     Output("rule-table", "rowData"),
    #     Input("submit-button", "n_clicks"),
    #     State("input-text", "value"),
    #     State("upload-data", "upload_id"),
    #     prevent_initial_call=True
    # )
    # def submit_message(n_clicks, user_input,log_file):
    #     print(n_clicks)
    #
    #     input_log_path = os.path.join(UPLOAD_FOLDER, log_file)
    #     files = os.listdir(input_log_path) if os.path.exists(input_log_path) else []
    #     log_path = Path(UPLOAD_FOLDER) / f"{log_file}" / files[0]
    #     # if n_clicks == 1:
    #     # todo event log is once imported here and once in prolysis code
    #     import pm4py
    #     event_log_xes = pm4py.read_xes(str(log_path), variant="rustxes")
    #     activities = event_log_xes["concept:name"].unique()
    #     redis_client.set('activities', json.dumps(list(activities)))
    #     activities = json.loads(redis_client.get('activities'))
    #     session_id = "user3"  # Can come from cookies or URL
    #     chat = get_chat(session_id)
    #     response = chat.send_message(user_input)
    #     append_to_history(session_id, "user", user_input)
    #     append_to_history(session_id, "model", response.text)
    #     parsed = extract_json_from_llm_response(response.text)
    #     if parsed is not None:
    #         rules = parsed
    #         rules['tasks'] = list(activities)
    #     else:
    #         rules = {}
    #
    #     rule_path = r"E:\PADS\Projects\IMr-LLM-extension\output_files\rules_llm.json"
    #     with open(rule_path, "w", encoding="utf-8") as f:
    #         json.dump(rules, f, indent=2)
    #
    #     from functions.subprocess_calls import measurement_extraction
    #     meas_path = r"E:\PADS\Projects\IMr-LLM-extension\output_files\meas_rules_llm.json"
    #     measurement_extraction(str(log_path),rule_path, meas_path)
    #
    #     data = read_json_file(meas_path.removesuffix(".json") + "[eventsEvaluation].json")
    #
    #     from functions.encoding_functions import encode,parse_constraints
    #     df_encoded, rules_meas = encode(event_log_xes, data)
    #
    #     df_meas = pd.DataFrame.from_dict(rules_meas, orient='index')
    #     df_meas.reset_index(inplace=True)
    #     df_meas.columns = ['rule', 'support', 'confidence']
    #     print(rules_meas)
    #
    #     # for i,r in enumerate(rules['constraints']):
    #     #     if len('parameters')>1:
    #     #         rules['constraints'][i]['description']=generate_natural_language(r['template'],r['parameters'][0][0],r['parameters'][1][0])
    #     #     else:
    #     #         rules['constraints'][i]['description']=generate_natural_language(r['template'], r['parameters'][0][0], "")
    #
    #
    #
    #     redis_client.set('rules', json.dumps(rules))
    #     print(rules)
    #     return response.text, df_meas.to_dict("records")

    # @app.callback(
    #     Output("petri_net2_LLM", "children"),
    #     Input("rediscover-button", "n_clicks"),
    #     State("upload-data", "upload_id"),
    #     State("sup_IMr_val", "value"),
    # )
    # def IMr_call_no_rules2(n1, log_file, sup):
    #     dim = "support"
    #     abs_thr = ""
    #     input_log_path = os.path.join(UPLOAD_FOLDER, log_file)
    #     files = os.listdir(input_log_path) if os.path.exists(input_log_path) else []
    #     log_path = Path(UPLOAD_FOLDER) / f"{log_file}" / files[0]
    #     rules = json.loads(redis_client.get('rules'))
    #     rules_for_imr = []
    #     for r in rules['constraints']:
    #         if 'support' not in r.keys():
    #             r['support']=1
    #         if 'confidence' not in r.keys():
    #             r['confidence'] = 1
    #         rules_for_imr.append(r)
    #     activities = json.loads(redis_client.get('activities'))
    #     if n1 > 0:
    #         # todo event log is once imported here and once in prolysis code
    #         # import pm4py
    #         # event_log_xes = pm4py.read_xes(str(log_path), variant="rustxes")
    #         # activities = event_log_xes["concept:name"].unique()
    #         # redis_client.set('activities', json.dumps(list(activities)))
    #         gviz = run_IMr(log_path, sup, rules_for_imr, activities, dim, abs_thr)
    #         return show_petri_net_LLM(gviz)
    #     return ""




    # @app.callback(
    #     Output("chat-component", "messages"),
    #     Output("rule-table", "rowData"),
    #     Input("chat-component", "new_message"),
    #     State("chat-component", "messages"),
    #     State("upload-data", "upload_id"),
    #     prevent_initial_call=True,
    # )
    # def handle_chat(new_message, messages, log_file):
    #     if not new_message:
    #         return messages
    #
    #     updated_messages = messages + [new_message]
    #
    #
    #     if new_message["role"] == "user":
    #         session_id = "user3"  # Can come from cookies or URL
    #         api_key = redis_client.get('api_key')
    #         chat = get_chat(session_id,api_key)
    #         rules = json.loads(redis_client.get('rules'))
    #         new_message['content'] = new_message['content'] + f" So far the user has selected the following rules {rules}."
    #         print(new_message['content'])
    #         response = chat.send_message(new_message['content'])
    #         append_to_history(session_id, "user", new_message['content'])
    #         append_to_history(session_id, "model", response.text)
    #         bot_response = {"role": "assistant", "content": response.text}
    #
    #         # parsed = extract_json_from_llm_response(response.text)
    #         activities = json.loads(redis_client.get('activities'))
    #         json_flag, error_message, parsed = extract_json_from_llm_response2(response.text,activities)
    #         resolution_counter = 0
    #
    #
    #         if json_flag:
    #             while error_message:
    #                 if resolution_counter<10:
    #                     chat = get_chat(session_id, api_key)
    #                     response = chat.send_message(error_message)
    #                     append_to_history(session_id, "user", error_message)
    #                     append_to_history(session_id, "model", response.text)
    #                     json_flag, error_message, parsed = extract_json_from_llm_response2(response.text,activities)
    #                     resolution_counter += 1
    #                 else:
    #                     return "Some errors persist, and unfortunately, we were unable to resolve them despite our error handling efforts. We apologize for the inconvenience.", ""
    #             # else:
    #             bot_response = {"role": "assistant", "content": "please check the extracted rules in the table and select the one you want to be included in the discovery, then click on re-discover!"}
    #             # activities = json.loads(redis_client.get('activities'))
    #             input_log_path = os.path.join(UPLOAD_FOLDER, log_file)
    #             files = os.listdir(input_log_path) if os.path.exists(input_log_path) else []
    #             log_path = Path(UPLOAD_FOLDER) / f"{log_file}" / files[0]
    #             # if n_clicks == 1:
    #             # todo event log is once imported here and once in prolysis code
    #
    #             event_log_xes = pm4py.read_xes(str(log_path), variant="rustxes")
    #
    #             if parsed is not None:
    #                 rules = parsed
    #                 rules['tasks'] = list(activities)
    #             else:
    #                 rules = {}
    #
    #             rule_path = r"E:\PADS\Projects\IMr-LLM-extension\output_files\rules_llm.json"
    #             with open(rule_path, "w", encoding="utf-8") as f:
    #                 json.dump(rules, f, indent=2)
    #
    #
    #             meas_path = r"E:\PADS\Projects\IMr-LLM-extension\output_files\meas_rules_llm.json"
    #             measurement_extraction(str(log_path), rule_path, meas_path)
    #
    #             data = read_json_file(meas_path.removesuffix(".json") + "[eventsEvaluation].json")
    #
    #             from functions.encoding_functions import encode, parse_constraints
    #             df_encoded, rules_meas = encode(event_log_xes, data)
    #
    #             df_meas = pd.DataFrame.from_dict(rules_meas, orient='index')
    #             df_meas.reset_index(inplace=True)
    #             df_meas.columns = ['rule', 'support', 'confidence']
    #             print(rules_meas)
    #             return updated_messages + [bot_response], df_meas.to_dict("records")
    #         else:
    #             return updated_messages + [bot_response], ""
    #
    #
    #     return updated_messages, ""

    @app.callback(
        Output("chat-component", "messages"),
        Output("rule-table", "rowData"),
        Input("chat-component", "new_message"),
        State("chat-component", "messages"),
        State("upload-data", "upload_id"),
        State("llm-provider", "value"),
        State("model-name", "value"),
        State("api-key", "value"),
        prevent_initial_call=True,
    )
    def handle_chat(new_message, messages, log_file,provider,MODEL_NAME,api_key):
        if not new_message:
            return messages

        updated_messages = messages + [new_message]

        if new_message["role"] == "user":
            # session_id = "user3"
            # api_key = redis_client.get('api_key')

            if provider == "OpenAI":
                model = OpenAI(api_key=api_key)
            elif provider == "Google":
                genai.configure(api_key=api_key)
                model = genai.GenerativeModel(MODEL_NAME)
            elif provider == "deepseek":
                url = "https://api.deepseek.com/"
                headers = {"Content-Type": "application/json", "Authorization": f"Bearer {api_key}"}
                model = {"url": url, "headers": headers}


            raw_history = redis_client.get("chat_history")
            # if raw_history:
            history = json.loads(raw_history)
            # history = [{"role": "user", "parts": [DEFAULT_PROMPT]}]

            # else:

            ### task description ###
            activities = json.loads(redis_client.get('activities'))
            DEFAULT_PROMPT = task_description()
            task_prompt = DEFAULT_PROMPT + "\n\nThe list of activities used in this process are the following. Please only use these activities to generate constraints:\n" + str(activities)
            rules = json.loads(redis_client.get('rules'))
            task_prompt += f". So far the user has selected the following rules {rules}."

            # new_message['content'] = new_message[
            #                              'content'] + f" So far the user has selected the following rules {rules}."
            # print(new_message['content'])

            # chat = get_chat(session_id, api_key)
            # response = chat.send_message(new_message['content'])

            response_txt, history = com_message(new_message['content'], task_prompt, history, model, provider,MODEL_NAME)
            bot_response = {"role": "assistant", "content": response_txt}
            redis_client.set("chat_history", json.dumps(history))

            # append_to_history(session_id, "user", new_message['content'])
            # append_to_history(session_id, "model", response.text)
            # bot_response = {"role": "assistant", "content": response.text}

            # parsed = extract_json_from_llm_response(response.text)
            # activities = json.loads(redis_client.get('activities'))
            json_flag, error_message, parsed = extract_json_from_llm_response2(response_txt, activities)
            resolution_counter = 0

            if json_flag:
                while error_message:
                    if resolution_counter < 10:
                        response_txt, history = com_message('', task_prompt + [error_message], history, model, provider,
                                                            MODEL_NAME)
                        redis_client.set("chat_history", json.dumps(history))
                        # chat = get_chat(session_id, api_key)
                        # response = chat.send_message(error_message)
                        # append_to_history(session_id, "user", error_message)
                        # append_to_history(session_id, "model", response.text)
                        json_flag, error_message, parsed = extract_json_from_llm_response2(response_txt, activities)
                        resolution_counter += 1
                    else:
                        return "Some errors persist, and unfortunately, we were unable to resolve them despite our error handling efforts. We apologize for the inconvenience.", ""
                # else:

                bot_response = {"role": "assistant",
                                "content": "please check the extracted rules in the table and select the one you want to be included in the discovery, then click on re-discover!"}
                # activities = json.loads(redis_client.get('activities'))
                input_log_path = os.path.join(UPLOAD_FOLDER, log_file)
                files = os.listdir(input_log_path) if os.path.exists(input_log_path) else []
                log_path = Path(UPLOAD_FOLDER) / f"{log_file}" / files[0]
                # if n_clicks == 1:
                # todo event log is once imported here and once in prolysis code

                event_log_xes = pm4py.read_xes(str(log_path), variant="rustxes")


                if parsed is not None:
                    rules = parsed
                    rules['tasks'] = list(activities)
                else:
                    rules = {}

                rule_path = r"E:\PADS\Projects\IMr-LLM-extension\output_files\rules_llm.json"
                rules_no_atmost1 = {'tasks':rules['tasks'],'constraints':[r for r in rules['constraints'] if (r['template']!="AtMost1" and r['template']!="AtLeast1")]}
                rules_atmost1 = [r for r in rules['constraints'] if r['template'] == "AtMost1"]
                rules_least1 = [r for r in rules['constraints'] if r['template'] == "AtLeast1"]
                if len(rules_no_atmost1['constraints'])>=1:
                    with open(rule_path, "w", encoding="utf-8") as f:
                        json.dump(rules_no_atmost1, f, indent=2)

                    meas_path = r"E:\PADS\Projects\IMr-LLM-extension\output_files\meas_rules_llm.json"
                    measurement_extraction(str(log_path), rule_path, meas_path)

                    data = read_json_file(meas_path.removesuffix(".json") + "[eventsEvaluation].json")

                    from functions.encoding_functions import encode, parse_constraints
                    df_encoded, rules_meas = encode(event_log_xes, data)

                    df_meas = pd.DataFrame.from_dict(rules_meas, orient='index')
                    df_meas.reset_index(inplace=True)
                    df_meas.columns = ['rule', 'support', 'confidence']

                else:
                    df_meas = pd.DataFrame(columns=['rule', 'support', 'confidence'])

                for r in rules_atmost1:
                    activator = r['parameters'][0][0]
                    case_count = event_log_xes['case:concept:name'].nunique()
                    focuse_df = event_log_xes[event_log_xes['concept:name']==activator]
                    activated_count = focuse_df['case:concept:name'].nunique()

                    activator_counts = focuse_df.groupby('case:concept:name')['concept:name'].apply(
                        lambda x: (x == activator).sum())
                    satisfied_count = (activator_counts <= 1).sum()
                    new_row = pd.DataFrame([{'rule': f"AtMost1({activator})", 'support': satisfied_count/case_count, 'confidence': satisfied_count/activated_count}])
                    df_meas = pd.concat([df_meas, new_row], ignore_index=True)
                # print(rules_meas)
                for r in rules_least1:
                    activator = r['parameters'][0][0]
                    case_count = event_log_xes['case:concept:name'].nunique()
                    focuse_df = event_log_xes[event_log_xes['concept:name']==activator]
                    activated_count = focuse_df['case:concept:name'].nunique()

                    new_row = pd.DataFrame([{'rule': f"AtLeast1({activator})", 'support': activated_count/case_count, 'confidence': activated_count/activated_count}])
                    df_meas = pd.concat([df_meas, new_row], ignore_index=True)

                return updated_messages + [bot_response], df_meas.to_dict("records")
            else:
                return updated_messages + [bot_response], ""

        return updated_messages, ""


    @app.callback(
        Output("model-name", "value"),
        Input("llm-provider", "value"),
        prevent_initial_call=True
    )
    def update_model_name(provider):
        if provider and provider in AI_MODEL_DEFAULTS:
            return AI_MODEL_DEFAULTS[provider]
        return ""

    @app.callback(
        Output("llm-init-output", "children"),
        Output("chat-placeholder", "style"),
        Output("chat-conversation","children"),
        Input("init-llm-btn", "n_clicks"),
        State("llm-provider", "value"),
        State("model-name", "value"),
        State("api-key", "value"),
        prevent_initial_call=True
    )
    def initialize_llm(n, provider, model, key):
        redis_client.set(f"chat_history", json.dumps([]))
        if key:
            redis_client.set('api_key',key)
        # else:
        #     redis_client.set('api_key', "AIzaSyDgM6Kfb9oxdBgncuUbrfhgwN8t5CCyG2M")

        if not provider or not model or not key:
            return "⚠️ Please fill all fields.", dash.no_update, dash.no_update
        # return f"✅ Initialized {provider} with model `{model}`."
        return f"✅ Initialized {provider} with model `{model}`.",{"display": "none"},html.Div([
            ChatComponent(
                id="chat-component",
                messages=[],
                container_style={
                    "height": "260px",  # fixed height
                    "overflowY": "auto",  # enable vertical scrolling
                    "border": "1px solid #ccc",
                    "borderRadius": "8px",
                    "padding": "10px",
                    "backgroundColor": "#fff",
                    "display": "flex",
                    "flexDirection": "column"
                },
                input_container_style={
                    "padding": "8px",
                    "borderTop": "1px solid #ddd",
                    "backgroundColor": "#f9f9f9",
                    "display": "flex",
                    "alignItems": "center",
                    "justifyContent": "space-between"
                },
                input_text_style={
                    "flexGrow": 1,
                    "marginRight": "10px",
                    "height": "30px"
                },
                input_placeholder="Explain your knowledge in your own words...",
                fill_height=False,  # You control height yourself
                theme="light",
                typing_indicator="dots",
            )
                    ])

    @app.callback(
        Output('download', "data"),
        Input('download-button', "n_clicks"),
        prevent_initial_call=True
    )
    def download_model(n):
        if n > 0:
            from pm4py.visualization.petri_net import visualizer as pn_visualizer
            # from graphviz import Source
            # gviz = redis_client.get("graph_viz")
            # gv = Source(gviz)
            # svg_string = pn_visualizer.view(gv)

            import pm4py
            from pm4py.visualization.bpmn import visualizer as bpmn_visualizer
            import os

            path = r"E:\PADS\Projects\IMr-LLM-extension\output_files"

            net, initial_marking, final_marking = pm4py.read_pnml(os.path.join(path, "model.pnml"))
            bpmn = pm4py.objects.conversion.wf_net.variants.to_bpmn.apply(net, initial_marking, final_marking)
            parameters = {"font_size": 40, "format": "pdf"}
            gviz = bpmn_visualizer.apply(bpmn, parameters=parameters)
            bpmn_visualizer.save(gviz, os.path.join(path, "model_bpmn.pdf"))
            svg_string = pn_visualizer.view(gviz)


            return svg_string

