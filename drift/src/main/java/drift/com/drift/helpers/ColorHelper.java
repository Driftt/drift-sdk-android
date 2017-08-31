package drift.com.drift.helpers;

import android.graphics.Color;

import drift.com.drift.model.Embed;


public class ColorHelper {

    public static int getForegroundColor() {

        Embed embed = Embed.getInstance();
        if (embed != null && embed.configuration != null && embed.configuration.theme != null) {
            String colorString = embed.configuration.theme.foregroundColor;
            if (colorString.charAt(0) != '#') {
                colorString = "#" + colorString;
            }

            return Color.parseColor(colorString);
        }

        return Color.rgb(0, 0, 0);
    }

    public static int getBackgroundColor() {

        Embed embed = Embed.getInstance();
        if (embed != null && embed.configuration != null && embed.configuration.theme != null) {
            String colorString = embed.configuration.theme.backgroundColor;
            if (colorString.charAt(0) != '#') {
                colorString = "#" + colorString;
            }

            return Color.parseColor(colorString);
        }

        return Color.rgb(36, 122, 246);
    }

    public static int getBackgroundColorLightened() {

        int mainColor = getBackgroundColor();

        return (mainColor & 0x00FFFFFF) | 0xB3000000;
    }
}
