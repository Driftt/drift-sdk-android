package drift.com.drift.helpers

import drift.com.drift.managers.AttachmentManager
import drift.com.drift.managers.ConversationManager
import drift.com.drift.managers.MessageManager
import drift.com.drift.managers.PresentationManager
import drift.com.drift.managers.SocketManager
import drift.com.drift.managers.UserManager
import drift.com.drift.model.Auth

object LogoutHelper {
    fun logout() {
        PresentationManager.closePopupView()
        UserManager.clearCache()
        ConversationManager.clearCache()
        MessageManager.clearCache()
        AttachmentManager.clearCache()
        Auth.deleteAuth()
        SocketManager.disconnect()
    }
}
