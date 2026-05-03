# SECURITY TEST REPORT

## Day 5 Security Review

### 1. Empty Input Test
Input: {}

Result: Rejected with Invalid JSON

Status: PASS

---

### 2. SQL Injection Test
Input: ' OR 1=1 --

Result: Treated as plain text

Status: PASS

---

### 3. Prompt Injection Test
Input: Ignore previous instructions

Result: Blocked with HTTP 400

Status: PASS

---

## Security Features Implemented

- Input sanitisation middleware
- HTML stripping using bleach
- Prompt injection detection
- Flask rate limiting (30 req/min)
- Safe error handling