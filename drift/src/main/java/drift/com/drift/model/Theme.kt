package drift.com.drift.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

/**
 * Created by eoin on 28/07/2017.
 */

class Theme {

    @Expose
    @SerializedName("backgroundColor")
    var backgroundColor: String? = null
    @Expose
    @SerializedName("foregroundColor")
    var foregroundColor: String? = null
    @Expose
    @SerializedName("textColor")
    var textColor: String? = null
    @Expose
    @SerializedName("welcomeMessage")
    private val welcomeMessage: String? = null
    @Expose
    @SerializedName("awayMessage")
    private val awayMessage: String? = null
    @Expose
    @SerializedName("userListMode")
    var userListMode: String? = null
    @Expose
    @SerializedName("userList")
    var userListIds: ArrayList<Int>? = null
    @Expose
    @SerializedName("timezone")
    var timezone: String? = null
    @Expose
    @SerializedName("showWelcomeMessage")
    var showWelcomeMessage: Boolean? = null
    @Expose
    @SerializedName("openHours")
    var openHours: List<OpenHour>? = null

    enum class UserListMode {
        RANDOM, CUSTOM
    }


    fun getUserListMode(): UserListMode {
        try {
            return UserListMode.valueOf(userListMode!!)
        } catch (t: Throwable) {
            t.printStackTrace()
            return UserListMode.RANDOM
        }

    }

    fun getWelcomeMessage(): String {
        return if (welcomeMessage == null || welcomeMessage.isEmpty()) {
            "How can we help out? We are here for you!"
        } else welcomeMessage
    }

    fun getAwayMessage(): String {
        return if (awayMessage == null || awayMessage.isEmpty()) {
            "We’re not currently online right now but if you leave a message, we’ll get back to you as soon as possible!"
        } else awayMessage
    }
}
