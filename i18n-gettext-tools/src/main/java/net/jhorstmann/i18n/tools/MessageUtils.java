package net.jhorstmann.i18n.tools;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.Message;

class MessageUtils {

    public static String getPluralForms(Message header) {
        HeaderFields fields = HeaderFields.wrap(header);
        return fields.getValue("Plural-Forms");
    }

    public static String serializeMsgid(String msgctx, String msgid) {
        if (msgid == null) {
            throw new IllegalArgumentException("msgid must not be null");
        }
        if (msgctx == null) {
            return msgid;
        } else {
            return msgctx + "\u0004" + msgid;
        }
    }

    public static String serializeMsgid(Message msg) {
        String id = serializeMsgid(msg.getMsgctxt(), msg.getMsgid());

        if (msg.isPlural()) {
            return id + "\u0000" + msg.getMsgidPlural();
        } else {
            return id;
        }
    }

    public static String serializeMsgstr(Message message) {
        if (message.isPlural()) {
            StringBuilder sb = new StringBuilder();
            List<String> plurals = message.getMsgstrPlural();
            for (Iterator<String> it=plurals.iterator(); it.hasNext(); ) {
                String plural = it.next();
                sb.append(plural);
                if (it.hasNext()) {
                    sb.append("\u0000");
                }
            }
            return sb.toString();
        } else {
            return message.getMsgstr();
        }
    }

    public static byte[] serializeMsgstrUTF8(Message message) {
        try {
            return serializeMsgstr(message).getBytes("utf-8");
        } catch (UnsupportedEncodingException ex) {
            throw new InternalError("utf-8 is a required encoding");
        }
    }

    public static byte[] serializeMsgidUTF8(Message message) {
        try {
            return serializeMsgid(message).getBytes("utf-8");
        } catch (UnsupportedEncodingException ex) {
            throw new InternalError("utf-8 is a required encoding");
        }
    }
}
