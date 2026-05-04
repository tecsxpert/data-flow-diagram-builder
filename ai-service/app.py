def validate_input(text):
    blocked = [
        "or 1=1",
        "drop table",
        "--",
        ";",
        "ignore previous instructions",
        "reveal system prompt"
    ]

    text = text.lower()

    for item in blocked:
        if item in text:
            return False

    return True