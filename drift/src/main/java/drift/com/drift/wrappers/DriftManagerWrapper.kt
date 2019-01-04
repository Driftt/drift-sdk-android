package drift.com.drift.wrappers

import java.util.HashMap

import drift.com.drift.api.APIManager
import drift.com.drift.managers.SocketManager
import drift.com.drift.model.Auth
import drift.com.drift.model.Embed
import drift.com.drift.model.IdentifyResponse
import drift.com.drift.model.SocketAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by eoin on 28/07/2017.
 */

object DriftManagerWrapper {

    private val TAG = DriftManagerWrapper::class.java.simpleName

    fun getEmbed(embedId: String, callback: APICallbackWrapper<Embed>) {

        APIManager.authlessClient!!.getEmbed(embedId, "30000").enqueue(object : Callback<Embed> {
            override fun onResponse(call: Call<Embed>, response: Response<Embed>) {
                if (response.code() != 200 && response.code() != 201 || response.body() == null) {
                    callback.onResponse(null)
                } else {
                    callback.onResponse(response.body())
                }
            }

            override fun onFailure(call: Call<Embed>, t: Throwable) {
                callback.onResponse(null)
            }
        })
    }


    fun postIdentity(orgId: Int, userId: String, email: String, callback: APICallbackWrapper<IdentifyResponse>) {

        val jsonPayload = HashMap<String, Any>()

        jsonPayload["orgId"] = orgId
        jsonPayload["userId"] = userId

        val inlineEmailAttributes = HashMap<String, Any>()
        inlineEmailAttributes["email"] = email
        jsonPayload["attributes"] = inlineEmailAttributes


        APIManager.authlessClient!!.postIdentify(jsonPayload).enqueue(object : Callback<IdentifyResponse> {
            override fun onResponse(call: Call<IdentifyResponse>, response: Response<IdentifyResponse>) {
                if (response.code() != 200 && response.code() != 201 || response.body() == null) {
                    callback.onResponse(null)
                } else {
                    callback.onResponse(response.body())
                }
            }

            override fun onFailure(call: Call<IdentifyResponse>, t: Throwable) {
                callback.onResponse(null)
            }
        })
    }

    fun getAuth(orgId: Int, userId: String, email: String, redirectUri: String, clientId: String, callback: APICallbackWrapper<Auth>) {

        val jsonPayload = HashMap<String, Any>()

        jsonPayload["email"] = email
        jsonPayload["org_id"] = orgId
        jsonPayload["user_id"] = userId
        jsonPayload["grant_type"] = "sdk"
        jsonPayload["redirect_uri"] = redirectUri
        jsonPayload["client_id"] = clientId


        APIManager.customerClient!!.getAuth(jsonPayload).enqueue(object : Callback<Auth> {
            override fun onResponse(call: Call<Auth>, response: Response<Auth>) {
                if (response.code() != 200 && response.code() != 201 || response.body() == null) {
                    callback.onResponse(null)
                } else {
                    callback.onResponse(response.body())
                }
            }

            override fun onFailure(call: Call<Auth>, t: Throwable) {
                callback.onResponse(null)
            }
        })
    }

    fun getSocketAuth(orgId: Int, accessToken: String, callback: APICallbackWrapper<SocketAuth>) {

        val jsonPayload = HashMap<String, Any>()

        jsonPayload["access_token"] = accessToken

        APIManager.authlessClient!!.postSocketAuth(orgId, computeShardId(orgId), jsonPayload).enqueue(object : Callback<SocketAuth> {
            override fun onResponse(call: Call<SocketAuth>, response: Response<SocketAuth>) {
                if (response.code() != 200 && response.code() != 201 || response.body() == null) {
                    callback.onResponse(null)
                } else {
                    callback.onResponse(response.body())
                }
            }

            override fun onFailure(call: Call<SocketAuth>, t: Throwable) {
                callback.onResponse(null)
            }
        })
    }

    private fun computeShardId(orgId: Int): Int {
        return orgId % 50 //WS_NUM_SHARDS
    }


}
