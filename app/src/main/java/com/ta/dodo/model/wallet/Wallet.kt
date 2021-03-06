package com.ta.dodo.model.wallet

import android.content.Context
import com.ta.dodo.model.user.KeyPairUtil
import com.ta.dodo.repository.WalletRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Server
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.security.PrivateKey
import java.security.PublicKey

private val logger = KotlinLogging.logger {}

class Wallet(val username: String) {
    private lateinit var keyPair: KeyPair
    private val walletRepositories = WalletRepositories()

    companion object {
        private val wallets = HashMap<String, Wallet>()
        private const val privateKeyFilename = "uangsaku.pk"
        private lateinit var instance: Wallet

        fun load(context: Context): Wallet {
            val reader = context.openFileInput(privateKeyFilename).bufferedReader()
            val username = reader.readLine()
            val secretSeed = reader.readLine()

            val keyPair = KeyPair.fromSecretSeed(secretSeed)

            logger.info { "Seed: ${String(keyPair.secretSeed)}" }
            logger.info { "AccountID: ${keyPair.accountId}" }

            return Wallet(username, keyPair)
        }

        fun setInstance(wallet: Wallet) {
            instance = wallet
        }

        fun getInstance(): Wallet {
            return instance
        }
    }

    constructor(username: String, keyPair: KeyPair): this(username) {
        this.keyPair = keyPair
        wallets[username] = this
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

    suspend fun getBalance(): String = withContext(Dispatchers.IO) {
        val server = Server("http://34.87.91.78:8000")
        try {
            val account = server.accounts().account(keyPair.accountId)

            logger.info { "Balance ${account.balances[0].balance}" }

            return@withContext account.balances[0].balance
        } catch (ex: Exception) {
            logger.error { ex.message }
            return@withContext "0"
        }
    }

    suspend fun sendMoney(receiver: String, amount: String) = withContext(Dispatchers.IO) {
        walletRepositories.sendMoney(getSeed(), receiver, amount)
    }

    suspend fun generateKeyPair() {
        val alias = getAccountId()
        val seed = getSeed().toByteArray(StandardCharsets.UTF_8)

        KeyPairUtil.build(alias, seed)
    }

    suspend fun getEncryptionKeyPair(): Pair<PrivateKey, PublicKey> {
        val alias = getAccountId()
        val seed = getSeed().toByteArray(StandardCharsets.UTF_8)

        val keyGenerator = KeyPairUtil.build(alias, seed)
        return keyGenerator.getEncryptionKey()
    }

    suspend fun getSigningKeyPair(): Pair<PrivateKey, PublicKey> {
        val alias = getAccountId()
        val seed = getSeed().toByteArray(StandardCharsets.UTF_8)

        val keyGenerator = KeyPairUtil.build(alias, seed)
        return keyGenerator.getSigningKey()
    }

    fun getAccountId(): String {
        return keyPair.accountId
    }

    fun getSeed(): String {
        return String(keyPair.secretSeed)
    }
}

class WalletNotRegisteredException(username: String?) :
    Exception("Wallet from $username not registered")
