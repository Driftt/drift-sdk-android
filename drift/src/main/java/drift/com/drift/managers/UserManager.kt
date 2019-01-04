package drift.com.drift.managers

import java.util.ArrayList
import java.util.HashMap

import drift.com.drift.model.User
import drift.com.drift.wrappers.APICallbackWrapper
import drift.com.drift.wrappers.UserManagerCallback
import drift.com.drift.wrappers.UserManagerWrapper

/**
 * Created by eoin on 04/08/2017.
 */

class UserManager {

    private var userMap = HashMap<Int, User>()

    fun clearCache() {
        userMap = HashMap()
    }

    fun getUserForId(userId: Int): User? {
        return userMap[userId]
    }

    fun getUsers(orgId: Int, userManagerCallback: UserManagerCallback) {

        UserManagerWrapper.getUsers(orgId) { response ->
            if (response != null) {
                userMap.clear()
                for (user in response) {
                    userMap[user.id] = user
                }
                userManagerCallback.didLoadUsers(true)
            } else {
                userManagerCallback.didLoadUsers(false)
            }
        }
    }

    fun getUsersIfWeNeedTo(orgId: Int, userManagerCallback: UserManagerCallback) {

        if (!userMap.isEmpty()) {
            userManagerCallback.didLoadUsers(true)
            return
        }

        getUsers(orgId, userManagerCallback)
    }

    companion object {

        private val TAG = UserManager::class.java.simpleName

        val instance = UserManager()
    }
}
