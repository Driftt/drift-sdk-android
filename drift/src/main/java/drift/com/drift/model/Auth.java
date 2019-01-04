package drift.com.drift.model;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import drift.com.drift.Drift;
import drift.com.drift.helpers.Preferences;

/**
 * Created by eoin on 28/07/2017.
 */

public class Auth {

    private static Auth _auth = null;

    public Auth(String accessToken) {
        super();
        this.accessToken = accessToken;
    }

    @Expose
    @SerializedName("accessToken")
    private String accessToken;

    @Expose
    @SerializedName("endUser")
    public EndUser endUser;

    @Nullable
    public static Auth getInstance()
    {
        if (_auth == null) {
            _auth = Auth.loadAuth();
        }

        return _auth;
    }

    private static Auth loadAuth(){

        SharedPreferences prefs = Drift.getContext().getSharedPreferences(Preferences.AUTH_STORE, Context.MODE_PRIVATE);
        String authJSON = prefs.getString(Preferences.AUTH_CACHE, null);

        if (authJSON == null) {
            return null;
        }

        Gson gson = new GsonBuilder().create();
        return gson.fromJson(authJSON, Auth.class);
    }

    public static void deleteAuth(){
        SharedPreferences prefs = Drift.getContext().getSharedPreferences(Preferences.AUTH_STORE, Context.MODE_PRIVATE);
        prefs.edit().remove(Preferences.AUTH_CACHE).apply();
        _auth = null;
    }

    public void saveAuth() {
        Gson gson = new Gson();
        String stringAuth = gson.toJson(this);
        SharedPreferences prefs = Drift.getContext().getSharedPreferences(Preferences.AUTH_STORE, Context.MODE_PRIVATE);
        prefs.edit().putString(Preferences.AUTH_CACHE, stringAuth).apply();
        _auth = this;
    }

    public String getAccessToken() {
        return accessToken;
    }
}