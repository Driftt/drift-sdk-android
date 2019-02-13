package drift.com.drift.managers

import java.util.HashMap

import drift.com.drift.model.User
import drift.com.drift.wrappers.UserManagerWrapper

/**
 * Created by eoin on 04/08/2017.
 */

internal object UserManager {

    private var userMap = HashMap<Long, User>()

    fun clearCache() {
        userMap = HashMap()
    }

    fun getUserForId(userId: Long): User? {
        return userMap[userId]
    }

    fun getUsers(orgId: Int, userManagerCallback: (success: Boolean) -> Unit) {

        UserManagerWrapper.getUsers(orgId) { response ->
            if (response != null) {
                userMap.clear()
                for (user in response) {
                    userMap[user.id] = user
                }
                userManagerCallback(true)
            } else {
                userManagerCallback(false)
            }
        }
    }

    fun getUsersIfWeNeedTo(orgId: Int, userManagerCallback: (success: Boolean) -> Unit) {

        if (!userMap.isEmpty()) {
            userManagerCallback(true)
            return
        }

        getUsers(orgId, userManagerCallback)
    }

}
