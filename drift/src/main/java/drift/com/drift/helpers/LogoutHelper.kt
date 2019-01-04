package drift.com.drift.helpers

import drift.com.drift.managers.AttachmentManager
import drift.com.drift.managers.ConversationManager
import drift.com.drift.managers.MessageManager
import drift.com.drift.managers.PresentationManager
import drift.com.drift.managers.SocketManager
import drift.com.drift.managers.UserManager
import drift.com.drift.model.Auth
import drift.com.drift.model.Embed

object LogoutHelper {
    fun logout() {
        PresentationManager.instance.closePopupView()
        UserManager.instance.clearCache()
        ConversationManager.instance.clearCache()
        MessageManager.instance.clearCache()
        AttachmentManager.instance.clearCache()
        Auth.deleteAuth()
        SocketManager.instance.disconnect()
    }
}
