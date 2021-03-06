package com.ta.dodo.service.user.request

import com.google.gson.Gson

class GetPublicKeyRequest(username: String) : BaseRequest("GetPublicKey") {
    init {
        val arg = getArg(username)
        args = listOf(arg)
    }

    private fun getArg(username: String): String {
        val request =
            Request(username)
        val gson = Gson()

        return gson.toJson(request)
    }

    data class Request(val username: String)
}
