| Component           | Standard                                        |
| ------------------- | ----------------------------------------------- |
| Signature Algorithm | Ed25519                                         |
| Private Key         | Raw 32-byte seed (hex encoded = 64 chars)       |
| Public Key          | Raw 32-byte public key (hex encoded = 64 chars) |
| Signature           | Raw 64 bytes (hex encoded = 128 chars)          |
| Message Encoding    | UTF-8                                           |
| Serialization       | Canonical format (we'll define this later)      |


The cleanup roadmap

Phase A — Crypto Foundation

Goal:

Android

↓

Generate Keypair

↓

Sign "Hello Sentinel"

↓

Backend verifies

↓

PASS
