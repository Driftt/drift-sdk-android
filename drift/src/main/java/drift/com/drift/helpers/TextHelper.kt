package drift.com.drift.helpers


import android.text.Html
import android.text.SpannableStringBuilder
import android.text.util.Linkify

object TextHelper {

    fun cleanString(body: String): String {
        var body = body
        body = body.replaceFirst("\\s++$".toRegex(), "") //Trim trailing white spaces
        body = body.replace("<p dir=\"....?\">".toRegex(), "")
        body = body.replace("<p>".toRegex(), "")
        body = body.replace("</p>".toRegex(), "<br />")

        body = body.replaceFirst("<p dir=\"....?\">".toRegex(), "")
        body = body.replaceFirst("<p>".toRegex(), "")

        if (body.endsWith("</p>")) {
            body = body.substring(0, body.lastIndexOf("</p>"))
        }

        return body
    }

    fun wrapTextForSending(body: String): String {
        var body = body

        val editableText = SpannableStringBuilder(body)
        Linkify.addLinks(editableText, Linkify.WEB_URLS)
        body = Html.toHtml(editableText).trim { it <= ' ' }

        body = body.replace("\n".toRegex(), "<br />")
        return body
    }

}
