package drift.com.drift.managers;


import androidx.annotation.Nullable;
import drift.com.drift.helpers.LoggerHelper;
import drift.com.drift.helpers.LogoutHelper;
import drift.com.drift.model.Auth;
import drift.com.drift.model.Embed;
import drift.com.drift.model.IdentifyResponse;
import drift.com.drift.model.SocketAuth;
import drift.com.drift.wrappers.APICallbackWrapper;
import drift.com.drift.wrappers.DriftManagerWrapper;

/**
 * Created by eoin on 28/07/2017.
 */

public class DriftManager {

    private static String TAG = DriftManager.class.getSimpleName();

    private static DriftManager _driftManager = new DriftManager();

    @Nullable
    private RegisterInformation registerInformation = null;

    public Boolean loadingUser = false;

    public static DriftManager getInstance() {
        return _driftManager;
    }

    public void getDataFromEmbeds(String embedId) {
        Embed embed = Embed.getInstance();
        if (embed != null && !embed.id.equals(embedId)) {
            LogoutHelper.logout();
        }

        DriftManagerWrapper.getEmbed(embedId, new APICallbackWrapper<Embed>() {
            @Override
            public void onResponse(Embed response) {
                if (response != null) {
                    response.saveEmbed();
                    LoggerHelper.logMessage(TAG, "Get Embed Success");

                    if (registerInformation != null) {
                        registerUser(registerInformation.userId, registerInformation.email);
                    }

                }
            }
        });
    }

    public void registerUser(String userId, final String email) {

        final Embed embed = Embed.getInstance();

        if (embed == null) {
            LoggerHelper.logMessage(TAG, "No Embed, not registering yet");
            registerInformation = new RegisterInformation(userId, email);
            return;
        }

        if (loadingUser) {
            return;
        }

        registerInformation = null;

        ///Post Identify
        loadingUser = true;
        DriftManagerWrapper.postIdentity(embed.orgId, userId, email, new APICallbackWrapper<IdentifyResponse>() {
            @Override
            public void onResponse(IdentifyResponse response) {
                if ( response != null ) {
                    LoggerHelper.logMessage(TAG, "Identify Complete");
                    getAuth(embed, response, email);
                } else {
                    LoggerHelper.logMessage(TAG, "Identify Failed");
                    loadingUser = false;
                }
            }
        });
    }

    private void getAuth(final Embed embed, IdentifyResponse identifyResponse, String email) {

        DriftManagerWrapper.getAuth(identifyResponse.orgId, identifyResponse.userId, email, embed.configuration.redirectUri, embed.configuration.authClientId, new APICallbackWrapper<Auth>() {
            @Override
            public void onResponse(Auth response) {
                if (response != null) {
                    response.saveAuth();
                    LoggerHelper.logMessage(TAG, "Auth Complete");
                    getSocketAuth(embed.orgId, response.getAccessToken());
                } else {
                    LoggerHelper.logMessage(TAG, "Auth Failed");
                    loadingUser = false;
                }
            }
        });
    }

    private void getSocketAuth(int orgId, String accessToken) {
        DriftManagerWrapper.getSocketAuth(orgId, accessToken, new APICallbackWrapper<SocketAuth>() {
            @Override
            public void onResponse(SocketAuth response) {
                if (response != null) {
                    LoggerHelper.logMessage(TAG, "Socket Auth Complete");
                    SocketManager.getInstance().connect(response);
                } else {
                    LoggerHelper.logMessage(TAG, "Socket Auth Failed");
                }
                loadingUser = false;
            }
        });
    }

    private class RegisterInformation {

        private String userId;
        private String email;

        RegisterInformation(String userId, String email){
            this.email = email;
            this.userId = userId;
        }

    }
}