package drift.com.drift.helpers

import android.graphics.Color

import drift.com.drift.model.Embed


object ColorHelper {

    val foregroundColor: Int
        get() {

            val embed = Embed.instance
            if (embed?.configuration?.theme != null) {
                var colorString = embed.configuration?.theme?.foregroundColor ?: ""
                if (colorString[0] != '#') {
                    colorString = "#$colorString"
                }

                return Color.parseColor(colorString)
            }

            return Color.rgb(0, 0, 0)
        }

    val backgroundColor: Int
        get() {

            val embed = Embed.instance
            if (embed?.configuration?.theme != null) {
                var colorString = embed.configuration?.theme?.backgroundColor ?: ""
                if (colorString[0] != '#') {
                    colorString = "#$colorString"
                }

                return Color.parseColor(colorString)
            }

            return Color.rgb(36, 122, 246)
        }

    val backgroundColorLightened: Int
        get() {

            val mainColor = backgroundColor

            return mainColor and 0x00FFFFFF or -0x4d000000
        }
}
