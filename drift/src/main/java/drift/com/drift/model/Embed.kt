package drift.com.drift.model

import android.content.Context

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import drift.com.drift.Drift
import drift.com.drift.helpers.Preferences

/**
 * Created by eoin on 28/07/2017.
 */

internal class Embed {

    @Expose
    @SerializedName("id")
    var id: String? = null

    @Expose
    @SerializedName("orgId")
    var orgId: Int? = null

    @Expose
    @SerializedName("configuration")
    var configuration: Configuration? = null

    fun saveEmbed() {
        val gson = Gson()
        val stringAuth = gson.toJson(this)
        val context = Drift.getContext()
        if (context != null) {
            val prefs = context.getSharedPreferences(Preferences.EMBED_STORE, Context.MODE_PRIVATE)
            prefs.edit().putString(Preferences.EMBED_CACHE, stringAuth).apply()
        }
        _embed = this
    }

    companion object {


        private var _embed: Embed? = null

        val instance: Embed?
            get() {
                if (_embed == null) {
                    _embed = loadEmbed()
                }

                return _embed
            }

        private fun loadEmbed(): Embed? {

            val context = Drift.getContext() ?: return null

            val prefs = context.getSharedPreferences(Preferences.EMBED_STORE, Context.MODE_PRIVATE)
            val authJSON = prefs.getString(Preferences.EMBED_CACHE, null) ?: return null

            val gson = GsonBuilder().create()
            return gson.fromJson(authJSON, Embed::class.java)
        }
    }
}