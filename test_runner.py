import requests

url = "http://localhost:5000/chat"

with open("test_inputs.txt", "r") as file:
    lines = file.readlines()

for i, line in enumerate(lines):
    message = line.strip()

    response = requests.post(url, json={"message": message})

    print(f"\nTest {i+1}")
    print("Input:", message)
    print("Response:", response.json())