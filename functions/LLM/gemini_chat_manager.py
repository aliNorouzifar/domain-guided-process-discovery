import json
import redis
import google.generativeai as genai
from functions.LLM import prompting
from prolysis.util.redis_connection import redis_client


# genai.configure(api_key="YOUR_API_KEY")
# model = genai.GenerativeModel("gemini-1.5-flash")
# api_key = redis_client.get('api_key')
# model = prompting.configure_Gemini(api_key)
# r = redis.Redis(decode_responses=True)  # decode strings

# DEFAULT_PROMPT = "You are an assistant that..."

def get_chat(session_id,api_key):
    model = prompting.configure_Gemini(api_key)
    raw_history = redis_client.get(f"chat:{session_id}")
    if raw_history:
        history = json.loads(raw_history)
    else:
        activities = redis_client.get('activities')
        DEFAULT_PROMPT = prompting.task_description(activities)
        history = [{"role": "user", "parts": [DEFAULT_PROMPT]}]
        redis_client.set(f"chat:{session_id}", json.dumps(history))
        # r.set(f"chat:{session_id}", json.dumps(history))
    return model.start_chat(history=history)

def append_to_history(session_id, role, content):
    history = json.loads(redis_client.get(f"chat:{session_id}"))
    history.append({"role": role, "parts": [content]})
    redis_client.set(f"chat:{session_id}", json.dumps(history))

def get_chat_history(session_id):
    return json.loads(redis_client.get(f"chat:{session_id}"))
