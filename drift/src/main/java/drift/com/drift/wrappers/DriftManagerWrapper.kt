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

internal object DriftManagerWrapper {

    fun getEmbed(embedId: String, callback: (response: Embed?) -> Unit) {

        APIManager.authlessClient.getEmbed(embedId, "30000").enqueue(object : Callback<Embed> {
            override fun onResponse(call: Call<Embed>, response: Response<Embed>) {
                if (response.code() != 200 && response.code() != 201 || response.body() == null) {
                    callback(null)
                } else {
                    callback(response.body())
                }
            }

            override fun onFailure(call: Call<Embed>, t: Throwable) {
                callback(null)
            }
        })
    }


    fun postIdentity(orgId: Int, userId: String, email: String, callback: (response: IdentifyResponse?) -> Unit) {

        val jsonPayload = HashMap<String, Any>()

        jsonPayload["orgId"] = orgId
        jsonPayload["userId"] = userId

        val inlineEmailAttributes = HashMap<String, Any>()
        inlineEmailAttributes["email"] = email
        jsonPayload["attributes"] = inlineEmailAttributes


        APIManager.authlessClient.postIdentify(jsonPayload).enqueue(object : Callback<IdentifyResponse> {
            override fun onResponse(call: Call<IdentifyResponse>, response: Response<IdentifyResponse>) {
                if (response.code() != 200 && response.code() != 201 || response.body() == null) {
                    callback(null)
                } else {
                    callback(response.body())
                }
            }

            override fun onFailure(call: Call<IdentifyResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun getAuth(orgId: Int, userId: String, email: String, redirectUri: String, clientId: String, callback: (response: Auth?) -> Unit) {

        val jsonPayload = HashMap<String, Any>()

        jsonPayload["email"] = email
        jsonPayload["org_id"] = orgId
        jsonPayload["user_id"] = userId
        jsonPayload["grant_type"] = "sdk"
        jsonPayload["redirect_uri"] = redirectUri
        jsonPayload["client_id"] = clientId


        APIManager.customerClient.getAuth(jsonPayload).enqueue(object : Callback<Auth> {
            override fun onResponse(call: Call<Auth>, response: Response<Auth>) {
                if (response.code() != 200 && response.code() != 201 || response.body() == null) {
                    callback(null)
                } else {
                    callback(response.body())
                }
            }

            override fun onFailure(call: Call<Auth>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun getSocketAuth(orgId: Int, accessToken: String, callback: (response: SocketAuth?) -> Unit) {

        val jsonPayload = HashMap<String, Any>()

        jsonPayload["access_token"] = accessToken

        APIManager.authlessClient.postSocketAuth(orgId, computeShardId(orgId), jsonPayload).enqueue(object : Callback<SocketAuth> {
            override fun onResponse(call: Call<SocketAuth>, response: Response<SocketAuth>) {
                if (response.code() != 200 && response.code() != 201 || response.body() == null) {
                    callback(null)
                } else {
                    callback(response.body())
                }
            }

            override fun onFailure(call: Call<SocketAuth>, t: Throwable) {
                callback(null)
            }
        })
    }

    private fun computeShardId(orgId: Int): Int {
        return orgId % 50 //WS_NUM_SHARDS
    }


}
