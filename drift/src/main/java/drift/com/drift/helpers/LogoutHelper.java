package drift.com.drift.helpers;

import drift.com.drift.managers.AttachmentManager;
import drift.com.drift.managers.ConversationManager;
import drift.com.drift.managers.MessageManager;
import drift.com.drift.managers.PresentationManager;
import drift.com.drift.managers.SocketManager;
import drift.com.drift.managers.UserManager;
import drift.com.drift.model.Auth;
import drift.com.drift.model.Embed;

public class LogoutHelper {
    public static void logout(){
        PresentationManager.getInstance().closePopupView();
        UserManager.getInstance().clearCache();
        ConversationManager.getInstance().clearCache();
        MessageManager.getInstance().clearCache();
        AttachmentManager.getInstance().clearCache();
        Auth.deleteAuth();
        SocketManager.getInstance().disconnect();
    }
}
