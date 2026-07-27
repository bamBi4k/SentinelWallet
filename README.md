Browser
   |
   |  GET /qr/generate
   |
   ↓
QR generated
(session_id + challenge)

   |
   |  Scan QR
   ↓

Android Wallet
   |
   | Parse:
   | sentinel://verify?
   | session_id=...
   | challenge=...
   |
   ↓

WalletViewModel
   |
   | generateProofWithChallenge()
   |
   ↓

Ed25519 Signature created
   |
   ↓

POST /qr/verify

   |
   ↓

Backend
   |
   | Verify signature
   | Verify public key
   | Verify challenge
   | Verify policy AGE_OVER_18
   |
   ↓

qr_generator.mark_used()

   |
   ↓

Browser polling:

GET /qr/check/<session_id>

   |
   ↓

✅ Verifizierung erfolgreich! Zugriff gewährt.
