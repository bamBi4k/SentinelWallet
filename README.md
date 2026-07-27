# 🛡️ Project Sentinel v2

## Privacy-Preserving Identity Verification Prototype

Project Sentinel is a privacy-focused identity verification system designed to demonstrate how users can prove attributes about themselves without revealing unnecessary personal information.

The goal is to build a future-ready identity layer where a user can prove statements such as:

> "I am over 18"

without exposing:

- Full name
- Date of birth
- Address
- Government ID data
- Other unnecessary personal information

Instead, the user controls a secure wallet containing cryptographic credentials.

---

# Current Prototype

This version demonstrates the complete verification handshake between:

```
Browser / Verifier
        |
        |
        ↓
 QR Challenge
        |
        |
        ↓
 Sentinel Wallet
        |
        |
        ↓
 Cryptographic Proof
        |
        |
        ↓
 Backend Verification
```

The current implementation uses:

- Python Flask backend
- Android Kotlin wallet
- Ed25519 digital signatures
- QR-based verification handshake
- Challenge-response authentication

---

# Features

## ✅ Authority / Issuer

The backend can:

- Generate identity credentials
- Sign credentials
- Provide authority public keys

---

## ✅ Wallet

The Android wallet can:

- Generate Ed25519 key pairs
- Securely store wallet keys
- Receive credentials
- Generate verification proofs
- Scan QR verification requests

---

## ✅ QR Verification Flow

The complete QR handshake works:

### Browser

Creates a verification request:

```
sentinel://verify?session_id=XXXX&challenge=YYYY
```

### Wallet

Scans QR code and creates:

```
{
    "claim_type": "AGE_OVER_18",
    "value": true,
    "challenge": "...",
    "timestamp": "...",
    "public_key": "...",
    "signature": "..."
}
```

### Backend

Verifies:

- Signature validity
- Challenge validity
- Verification policy

Result:

```
✅ Verification successful
```

---

# Architecture

```
Project Sentinel

                 +----------------+
                 |   Authority    |
                 |  Credential   |
                 |    Issuer      |
                 +-------+--------+
                         |
                         |
                         ↓

+-------------+     Credential     +--------------+
|             | ------------------> |              |
|   Wallet    |                     |   Browser    |
|  Android    |                     |  Verifier    |
|             |                     |              |
+------+------+\                    +------+-------+
       |        \                          |
       |         \                         |
       |          \ QR Challenge           |
       |           ------------------------>
       |
       |
       ↓

 Generate Proof

       |
       ↓

 Flask Verification API

       |
       ↓

 Signature Verification

```

---

# Technology Stack

## Backend

- Python 3
- Flask
- Ed25519 cryptography
- QRCode generation

## Android Wallet

- Kotlin
- Jetpack Compose
- Retrofit
- Coroutines
- libsodium / Ed25519

---

# Running the Backend

## Requirements

Install:

```
Python 3.11+
pip
```

Install dependencies:

```
pip install -r requirements.txt
```

---

## Start Sentinel Backend

Navigate to:

```
Sentinel_v2/
```

Run:

```
python app.py
```

Expected output:

```
PROJECT SENTINEL v2 - Identity Handshake System

Authority: Ready
Wallet: Ready
Verifier: Ready

Starting server:
http://127.0.0.1:5000
```

---

# Testing the QR Verification

## Step 1

Open:

```
http://127.0.0.1:5000/qr/generate
```

A QR code appears:

```
Waiting for verification...
```

---

## Step 2

Open the Android Sentinel Wallet.

Press:

```
Scan QR Code
```

---

## Step 3

Scan the QR code from the browser.

The wallet will:

1. Read session ID
2. Read challenge
3. Generate proof
4. Sign proof using Ed25519
5. Send proof to backend

---

## Step 4

Backend verifies:

Example log:

```
🔍 Full proof:

{
 "claim_type": "AGE_OVER_18",
 "value": true,
 "challenge": "...",
 "signature": "...",
 "public_key": "..."
}

Verification result: True
```

---

## Step 5

Browser updates:

```
✅ Verifizierung erfolgreich!
Zugriff gewährt.
```

---

# API Endpoints

## Create Challenge

```
POST /verifier/challenge
```

Returns:

```json
{
 "session_id":"...",
 "challenge":"..."
}
```

---

## Verify Proof

```
POST /qr/verify
```

Example:

```json
{
 "session_id":"...",
 "proof":{
    "claim_type":"AGE_OVER_18",
    "value":true,
    "challenge":"...",
    "signature":"...",
    "public_key":"..."
 }
}
```

---

## Check Verification Status

```
GET /qr/check/<session_id>
```

Returns:

```json
{
 "status":"verified"
}
```

---

# Security Model (Current Prototype)

Implemented:

✅ Challenge-response authentication

✅ Ed25519 signatures

✅ Replay protection

✅ Public key verification

✅ No identity data transferred during proof presentation


---

# Future Development

Planned improvements:

## Privacy Layer

- Zero-Knowledge Proof integration
- Selective disclosure credentials
- Anonymous credentials

## Wallet Improvements

- Hardware-backed key storage
- Biometric unlock
- Credential backup and recovery

## Backend Improvements

- Database-backed sessions
- Distributed verification nodes
- Production authentication policies

---

# Project Status

Current milestone:

```
Milestone 2/3

✅ Credential issuing
✅ Android wallet
✅ QR handshake
✅ Cryptographic verification

Next:
Zero-Knowledge identity proofs
```

---

# License

Prototype project for research and development purposes.
