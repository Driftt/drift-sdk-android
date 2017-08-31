package drift.com.drift.wrappers;

import java.util.ArrayList;
import java.util.HashMap;

import drift.com.drift.api.APIManager;
import drift.com.drift.model.SocketAuth;
import drift.com.drift.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by eoin on 04/08/2017.
 */

public class UserManagerWrapper {

    public static void getUsers(int orgId, final APICallbackWrapper<ArrayList<User>> callback){


        APIManager.getCustomerClient().getUsers(orgId).enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                if((response.code() != 200 && response.code() != 201) || response.body() == null) {
                    callback.onResponse(null);
                }else{
                    callback.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable throwable) {
                callback.onResponse(null);
            }
        });

    }

}
