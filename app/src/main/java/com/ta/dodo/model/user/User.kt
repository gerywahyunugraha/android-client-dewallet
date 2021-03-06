package com.ta.dodo.model.user

import android.content.Context
import shadow.com.google.gson.annotations.SerializedName
import java.security.PublicKey
import javax.crypto.SecretKey

class User(val username: String, val publicKey: String, val ePublicKey: PublicKey, val sPublicKey: PublicKey? = null) {

    var data: Data? = null

    data class Data(
        @SerializedName("phoneNumber")
        val phoneNumber: String?,
        @SerializedName("identityNumber")
        val identityNumber: String?,
        @SerializedName("fullName")
        val fullName: String?,
        @SerializedName("email")
        val email: String?
    )

    suspend fun generateSecretKey(context: Context) {
        val keyUtil = KeyUtil.build(username, context)
        keyUtil.generateSecretKey(context)
        KeyUtil.instance = keyUtil
    }

    fun getSecretKey(): SecretKey {
        val keyGenerator = KeyUtil.instance
        return keyGenerator.secretKey
    }

    class DataBuilder {
        private var phoneNumber: String? = null
        private var identityNumber: String? = null
        private var fullName: String? = null
        private var email: String? = null

        fun addPhoneNumber(phoneNumber: String): DataBuilder {
            this.phoneNumber = phoneNumber
            return this
        }

        fun addIdentityNumber(identityNumber: String): DataBuilder {
            this.identityNumber = identityNumber
            return this
        }

        fun addFullName(fullName: String): DataBuilder {
            this.fullName = fullName
            return this
        }

        fun addEmail(email: String): DataBuilder {
            this.email = email
            return this
        }

        fun build(): Data {
            return Data(
                phoneNumber = phoneNumber,
                identityNumber = identityNumber,
                fullName = fullName,
                email = email
            )
        }
    }
}
