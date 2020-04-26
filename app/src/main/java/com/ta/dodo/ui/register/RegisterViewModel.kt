package com.ta.dodo.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ta.dodo.model.wallet.Wallet
import kotlinx.coroutines.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    var wallet: MutableLiveData<Wallet> = MutableLiveData()
    var username: MutableLiveData<String> = MutableLiveData("")
    var isGeneratingKey: MutableLiveData<Boolean> = MutableLiveData(false)

    fun loadSavedPrivateKey() {
        val wallet = Wallet.load(context)
        Wallet.setInstance(wallet)
    }

    fun register() = viewModelScope.launch(Dispatchers.Main) {
        isGeneratingKey.value = true
        val username = this@RegisterViewModel.username.value!!
        val wallet = Wallet(username)

        wallet.register()
        wallet.save(context)

        logger.info { "Finished generating key" }

        wallet.generateKeyPair()

//        val user = User(username, wallet)
//        user.data = User.DataBuilder()
//            .addAddress("Jalan Toya Anakan 4")
//            .addIdentityNumber("5108062406950007")
//            .addDateOfBirth(Date())
//            .build()
//
//        val gson = Gson()
//        val dataJson = gson.toJson(user.data)
//

//
//        val encrypted = CipherUtil.encrypt(dataJson, publicKey)
//        logger.info { "Encrypted data $encrypted" }
//
//        val decrypted = CipherUtil.decrypt(encrypted ?: "", privateKey)
//        logger.info { "Decrypted data $decrypted" }

        this@RegisterViewModel.wallet.value = wallet
        isGeneratingKey.value = false
    }
}
