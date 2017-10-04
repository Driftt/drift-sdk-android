package drift.com.drift.helpers;


import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.util.Linkify;

public class TextHelper {

    public static String cleanString(String body){
        body = body.replaceFirst("\\s++$", ""); //Trim trailing white spaces
        body = body.replaceAll("<p dir=\"....?\">", "");
        body = body.replaceAll("<p>", "");
        body = body.replaceAll("</p>", "<br />");

        body = body.replaceFirst("<p dir=\"....?\">", "");
        body = body.replaceFirst("<p>", "");

        if (body.endsWith("</p>")) {
            body = body.substring(0, body.lastIndexOf("</p>"));
        }

        return body;
    }

    public static String wrapTextForSending(String body) {

        Editable editableText = new SpannableStringBuilder( body );
        Linkify.addLinks(editableText, Linkify.WEB_URLS);
        body = Html.toHtml(editableText).trim();

        body = body.replaceAll("\n", "<br />");
        return body;
    }

}
