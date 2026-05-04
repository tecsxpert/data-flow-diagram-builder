from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route("/chat", methods=["POST"])
def chat():
    data = request.get_json()

    if not data or "message" not in data:
        return jsonify({"error": "message required"}), 400

    message = data["message"]

    if message.strip() == "":
        return jsonify({"error": "empty message"}), 400

    return jsonify({"reply": "Hello from backend"}), 200


if __name__ == "__main__":
    app.run(debug=True)