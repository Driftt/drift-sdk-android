package drift.com.drift.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import drift.com.drift.Drift;
import drift.com.drift.helpers.Preferences;

/**
 * Created by eoin on 28/07/2017.
 */

public class Embed {


    private static Embed _embed= null;

    @Nullable
    public static Embed getInstance()
    {
        if (_embed == null) {
            _embed = Embed.loadEmbed();
        }

        return _embed;
    }

    @Expose
    @SerializedName("id")
    public String id;

    @Expose
    @SerializedName("orgId")
    public Integer orgId;

    @Expose
    @SerializedName("configuration")
    public Configuration configuration;

    private static Embed loadEmbed(){

        SharedPreferences prefs = Drift.getContext().getSharedPreferences(Preferences.EMBED_STORE, Context.MODE_PRIVATE);
        String authJSON = prefs.getString(Preferences.EMBED_CACHE, null);

        if (authJSON == null) {
            return null;
        }

        Gson gson = new GsonBuilder().create();
        return gson.fromJson(authJSON, Embed.class);
    }

    public static void deleteEmbed(){
        SharedPreferences prefs = Drift.getContext().getSharedPreferences(Preferences.EMBED_STORE, Context.MODE_PRIVATE);
        prefs.edit().remove(Preferences.EMBED_CACHE).apply();
        _embed = null;
    }

    public void saveEmbed() {
        Gson gson = new Gson();
        String stringAuth = gson.toJson(this);
        SharedPreferences prefs = Drift.getContext().getSharedPreferences(Preferences.EMBED_STORE, Context.MODE_PRIVATE);
        prefs.edit().putString(Preferences.EMBED_CACHE, stringAuth).apply();
        _embed = this;
    }

}
