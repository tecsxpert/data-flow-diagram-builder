from flask import Flask, request, jsonify
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
import bleach
import re

app = Flask(__name__)

# -----------------------------
# Rate Limiter (30 req/min)
# -----------------------------
limiter = Limiter(
    key_func=get_remote_address,
    app=app,
    default_limits=["30 per minute"]
)

# -----------------------------
# Prompt Injection Detection
# -----------------------------
INJECTION_PATTERNS = [
    r"ignore previous instructions",
    r"forget all previous instructions",
    r"act as",
    r"system prompt",
    r"reveal hidden",
    r"bypass",
    r"developer mode",
    r"jailbreak"
]


def detect_prompt_injection(text):
    text = text.lower()
    for pattern in INJECTION_PATTERNS:
        if re.search(pattern, text):
            return True
    return False


# -----------------------------
# Input Sanitisation Middleware
# -----------------------------
@app.before_request
def sanitize_input():

    if request.method == "POST":

        data = request.get_json(silent=True)

        if not data:
            return jsonify({"error": "Invalid JSON"}), 400

        for key, value in data.items():

            if isinstance(value, str):

                # Strip HTML
                cleaned = bleach.clean(value, tags=[], strip=True)

                # Detect prompt injection
                if detect_prompt_injection(cleaned):
                    return jsonify({
                        "error": "Prompt injection detected"
                    }), 400

                data[key] = cleaned


# -----------------------------
# Health Route
# -----------------------------
@app.route("/health")
def health():
    return jsonify({"status": "ok"})


# -----------------------------
# Describe Route
# -----------------------------
@app.route("/describe", methods=["POST"])
def describe():

    data = request.get_json()

    user_input = data.get("input", "")

    return jsonify({
        "description": f"Processed: {user_input}"
    })


# -----------------------------
# Run
# -----------------------------
if __name__ == "__main__":
    app.run(debug=True, port=5000)