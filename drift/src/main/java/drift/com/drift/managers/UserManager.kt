package drift.com.drift.managers

import java.util.HashMap

import drift.com.drift.model.User

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

    fun setUsers(team: List<User>) {

        userMap.clear()
        for (user in team) {
            userMap[user.id] = user
        }
    }

}
