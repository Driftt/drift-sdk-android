package drift.com.drift.model

import android.content.Context
import android.content.SharedPreferences

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import drift.com.drift.Drift
import drift.com.drift.helpers.Preferences

/**
 * Created by eoin on 28/07/2017.
 */

class Auth() {

    @Expose
    @SerializedName("accessToken")
    var accessToken: String? = null

    @Expose
    @SerializedName("endUser")
    var endUser: EndUser? = null

    fun saveAuth() {
        val gson = Gson()
        val stringAuth = gson.toJson(this)
        val prefs = Drift.getContext().getSharedPreferences(Preferences.AUTH_STORE, Context.MODE_PRIVATE)
        prefs.edit().putString(Preferences.AUTH_CACHE, stringAuth).apply()
        _auth = this
    }

    companion object {

        private var _auth: Auth? = null

        val instance: Auth?
            get() {
                if (_auth == null) {
                    _auth = Auth.loadAuth()
                }

                return _auth
            }

        private fun loadAuth(): Auth? {

            val prefs = Drift.getContext().getSharedPreferences(Preferences.AUTH_STORE, Context.MODE_PRIVATE)
            val authJSON = prefs.getString(Preferences.AUTH_CACHE, null) ?: return null

            val gson = GsonBuilder().create()
            return gson.fromJson(authJSON, Auth::class.java)
        }

        fun deleteAuth() {
            val prefs = Drift.getContext().getSharedPreferences(Preferences.AUTH_STORE, Context.MODE_PRIVATE)
            prefs.edit().remove(Preferences.AUTH_CACHE).apply()
            _auth = null
        }
    }
}