package com.ta.dodo.model.wallet

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.stellar.sdk.KeyPair
import java.lang.Exception

private val logger = KotlinLogging.logger {}

class Wallet(private val username: String) {
    private lateinit var keyPair: KeyPair

    companion object {
        private const val privateKeyFilename = "uangsaku.pk"

        fun load(context: Context): Wallet {
            val reader = context.openFileInput(privateKeyFilename).bufferedReader()
            val username = reader.readLine()
            val secretSeed = reader.readLine()

            val keyPair = KeyPair.fromSecretSeed(secretSeed)

            logger.info { "Seed: ${String(keyPair.secretSeed)}" }
            logger.info { "AccountID: ${keyPair.accountId}" }

            return Wallet(username, keyPair)
        }
    }

    constructor(username: String, keyPair: KeyPair): this(username) {
        this.keyPair = keyPair
    }

    suspend fun register() = withContext(Dispatchers.Default) {
        keyPair = KeyPair.random()

        logger.info { "Seed: ${String(keyPair.secretSeed)}" }
        logger.info { "AccountID: ${keyPair.accountId}" }
        // TODO: Register the public key to Hyperledger
    }

    suspend fun save(context: Context) = withContext(Dispatchers.IO) {
        if (!::keyPair.isInitialized) {
            throw WalletNotRegisteredException(username)
        }
        context.openFileOutput(privateKeyFilename, Context.MODE_PRIVATE).use {
            it.write(username.toByteArray())
            it.write("\n".toByteArray())
            it.write(String(keyPair.secretSeed).toByteArray())
        }
    }

    fun getAccountId(): String {
        if (!::keyPair.isInitialized) {
            return ""
        }
        return keyPair.accountId
    }
}

class WalletNotRegisteredException(username: String?) :
    Exception("Wallet from $username not registered")
