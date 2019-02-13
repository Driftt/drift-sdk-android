package drift.com.drift.managers


import drift.com.drift.helpers.LoggerHelper
import drift.com.drift.helpers.LogoutHelper
import drift.com.drift.model.Embed
import drift.com.drift.model.IdentifyResponse
import drift.com.drift.wrappers.DriftManagerWrapper

/**
 * Created by eoin on 28/07/2017.
 */

internal object DriftManager {

    private val TAG = DriftManager::class.java.simpleName

    private var registerInformation: RegisterInformation? = null

    var loadingUser: Boolean? = false

    fun getDataFromEmbeds(embedId: String) {
        val embed = Embed.instance
        if (embed?.id != embedId) {
            LogoutHelper.logout()
        }

        DriftManagerWrapper.getEmbed(embedId) { response ->
            if (response != null) {
                response.saveEmbed()
                LoggerHelper.logMessage(TAG, "Get Embed Success")

                if (registerInformation != null) {
                    registerUser(registerInformation!!.userId, registerInformation?.email, registerInformation?.userJwt)
                }

            }
        }
    }

    fun registerUser(userId: String, email: String? = null, userJwt: String? = null) {

        val embed = Embed.instance

        if (embed == null) {
            LoggerHelper.logMessage(TAG, "No Embed, not registering yet")
            registerInformation = RegisterInformation(userId, email, userJwt)
            return
        }

        if (loadingUser!!) {
            return
        }

        registerInformation = null

        ///Post Identify
        loadingUser = true
        DriftManagerWrapper.postIdentity(embed.orgId!!, userId, email, userJwt) { response ->
            if (response != null) {
                LoggerHelper.logMessage(TAG, "Identify Complete")
                getAuth(embed, response, email, userJwt)
            } else {
                LoggerHelper.logMessage(TAG, "Identify Failed")
                loadingUser = false
            }
        }
    }

    private fun getAuth(embed: Embed, identifyResponse: IdentifyResponse, email: String?, userJwt: String?) {

        val userId = identifyResponse.userId ?: return
        val authClientId = embed.configuration!!.authClientId ?: return
        val redirectUri = embed.configuration!!.redirectUri ?: return

        DriftManagerWrapper.getAuth(identifyResponse.orgId!!, userId, email, userJwt, redirectUri, authClientId) { response ->
            if (response?.accessToken != null) {
                response.saveAuth()
                LoggerHelper.logMessage(TAG, "Auth Complete")
                getSocketAuth(embed.orgId!!, response.accessToken!!)
            } else {
                LoggerHelper.logMessage(TAG, "Auth Failed")
                loadingUser = false
            }
        }
    }

    private fun getSocketAuth(orgId: Int, accessToken: String) {
        DriftManagerWrapper.getSocketAuth(orgId, accessToken) { response ->
            if (response != null) {
                LoggerHelper.logMessage(TAG, "Socket Auth Complete")
                SocketManager.connect(response)
            } else {
                LoggerHelper.logMessage(TAG, "Socket Auth Failed")
            }
            loadingUser = false
        }
    }

    private class RegisterInformation internal constructor(internal val userId: String, internal val email: String?, internal val userJwt: String?)

}