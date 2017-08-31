package drift.com.drift.helpers;


public class TextHelper {

    public static String cleanString(String body){
        body = body.replaceFirst("\\s++$", ""); //Trim trailing white spaces

        body = body.replaceFirst("<p dir=\".+\">", "");
        body = body.replaceFirst("<p>", "");

        if (body.endsWith("</p>")) {
            body = body.substring(0, body.lastIndexOf("</p>"));
        }

//        body = body.replace("\n","<br />");
        return body;
    }

    public static String cleanStringIncludingParagraphBreaks(String body){
        body = cleanString(body);
        body = body.replace("<p><br></p>", "");
        return body;
    }

    public static String flattenStringToOneLine(String body){
        body = cleanStringIncludingParagraphBreaks(body);
        body = body.replace("\n", "");
        body = body.replace("<p>", " ");
        body = body.replace("</p>", " ");
        body = body.replace("<br>", " ");
        return body;
    }

}
