package com.ta.dodo.model.user

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher

private const val provider = "AndroidKeyStoreBCWorkaround"
private const val transformation = "RSA/ECB/PKCS1Padding"

class CipherUtil {
    companion object {
        fun decrypt(encryptedBase64: String, privateKey: PrivateKey): String {
            val encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT)
            val cipher = Cipher.getInstance(transformation, provider)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)

            return String(cipher.doFinal(encryptedBytes), StandardCharsets.UTF_8)
        }

        fun encrypt(data: String, publicKey: PublicKey): String? {
            val cipher = Cipher.getInstance(transformation, provider)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)

            val encryptedBytes = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        }
    }
}
