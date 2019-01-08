package drift.com.drift.wrappers

import java.util.ArrayList

import drift.com.drift.api.APIManager
import drift.com.drift.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by eoin on 04/08/2017.
 */

object UserManagerWrapper {

    fun getUsers(orgId: Int, callback: (response: ArrayList<User>?) -> Unit) {


        APIManager.customerClient.getUsers(orgId).enqueue(object : Callback<ArrayList<User>> {
            override fun onResponse(call: Call<ArrayList<User>>, response: Response<ArrayList<User>>) {
                if (response.code() != 200 && response.code() != 201 || response.body() == null) {
                    callback(null)
                } else {
                    callback(response.body())
                }
            }

            override fun onFailure(call: Call<ArrayList<User>>, throwable: Throwable) {
                callback(null)
            }
        })

    }

}
