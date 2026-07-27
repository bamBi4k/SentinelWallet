                 User
                  |
                  |
          Android Sentinel Wallet
                  |
                  |
        1. Scan QR code
                  |
                  ↓

        Verifier Website

        "I need proof of AGE_OVER_18"

                  |
                  |
        Generates challenge
                  |
                  ↓

             QR Code

                  |
                  |
        Wallet scans QR

                  |
                  ↓

        Wallet creates proof

        {
          challenge,
          claim,
          public_key,
          signature
        }

                  |
                  ↓

        Wallet sends proof back

                  |
                  ↓

        Backend verifies

                  |
                  ↓

        Website receives:
        "Verified ✅"
