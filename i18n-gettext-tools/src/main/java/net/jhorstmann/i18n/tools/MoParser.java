package net.jhorstmann.i18n.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.fedorahosted.tennera.jgettext.Message;

public class MoParser {

    public static MessageBundle parseMessages(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        try {
            MessageBundle bundle = parseMessages(in);

            return bundle;
        } finally {
            in.close();
        }
    }

    public static MessageBundle parseMessages(InputStream in) throws IOException {
        int magic = readInt(in, false);
        boolean big;
        if (magic == 0x950412DE) {
            big = false;
        } else if (magic == 0xDE120495) {
            big = true;
        } else {
            throw new IOException("Invalid magic number");
        }
        int rev = readInt(in, big);
        int size = readInt(in, big);
        int msgidTableOff = readInt(in, big);
        int msgstrTableOff = readInt(in, big);
        int hashSize = readInt(in, big);
        int hashOff = readInt(in, big);
        int off = 7*4;

        int[] msgidLength = new int[size];
        int[] msgidOffset = new int[size];
        int[] msgstrLength = new int[size];
        int[] msgstrOffset = new int[size];
        String[] msgid = new String[size];
        String[] msgstr = new String[size];

        while (off < msgidTableOff) {
            readNoEOF(in);
            off++;
        }

        for (int i=0; i<size; i++) {
            msgidLength[i] = readInt(in, big);
            msgidOffset[i] = readInt(in, big);
            off+=8;
        }

        while (off < msgstrTableOff) {
            readNoEOF(in);
            off++;
        }

        for (int i=0; i<size; i++) {
            msgstrLength[i] = readInt(in, big);
            msgstrOffset[i] = readInt(in, big);
            off += 8;
        }

        while (off < hashOff+hashSize*4) {
            readNoEOF(in);
            off++;
        }

        while (off < msgidOffset[0]) {
            readNoEOF(in);
            off++;
        }

        for (int i=0; i<size; i++) {
            if (off != msgidOffset[i]) {
                throw new IOException("Offset for msgid " + i + " does not match, expected 0x" + Integer.toHexString(msgidOffset[i]) + " but was 0x" + Integer.toHexString(off));
            }

            msgid[i] = readString(in, msgidLength[i]);
            off += msgidLength[i] + 1;
        }

        while (off < msgstrOffset[0]) {
            readNoEOF(in);
            off++;
        }

        for (int i=0; i<size; i++) {
            if (off != msgstrOffset[i]) {
                throw new IOException("Offset for msgstr " + i + " does not match, expected 0x" + Integer.toHexString(msgstrOffset[i]) + " but was 0x" + Integer.toHexString(off));
            }

            msgstr[i] = readString(in, msgstrLength[i]);
            off += msgstrLength[i] + 1;
        }

        MessageBundle bundle = new MessageBundle();
        for (int i=0; i<size; i++) {
            Message message = new Message();
            String ctx;
            String id;
            String plid;
            String str;
            int idx = msgid[i].indexOf('\u0004');
            if (idx >= 0) {
                ctx = msgid[i].substring(0, idx);
                id = msgid[i].substring(idx+1);
            } else {
                ctx = null;
                id = msgid[i];
            }

            idx = id.indexOf('\u0000');
            if (idx >= 0) {
                plid = id.substring(idx+1);
                id = id.substring(0, idx);
            } else {
                plid = null;
            }

            message.setMsgid(id);
            if (ctx != null) {
                message.setMsgctxt(ctx);
            }
            if (plid != null) {
                message.setMsgidPlural(plid);
            }

            str = msgstr[i];
            idx = str.indexOf('\u0000');
            if (idx >= 0 && plid != null) {
                int lastidx = 0;
                int pos = 0;
                String plural;
                do {
                    plural = str.substring(lastidx, idx);
                    message.addMsgstrPlural(plural, pos);
                    pos++;
                    lastidx = idx+1;
                    idx = str.indexOf('\u0000', lastidx);
                }
                while (idx >= 0);
                plural = str.substring(lastidx);
                message.addMsgstrPlural(plural, pos);
            } else {
                message.setMsgstr(str);
            }
            bundle.addMessage(message);
        }

        return bundle;
    }

    private static int readNoEOF(InputStream in) throws IOException {
        int i = in.read();
        if (i < 0) {
            throw new IOException("EOF");
        }
        return i;
    }

    private static int readInt(InputStream in, boolean bigEndian) throws IOException {
        int b0 = readNoEOF(in);
        int b1 = readNoEOF(in);
        int b2 = readNoEOF(in);
        int b3 = readNoEOF(in);
        if (bigEndian) {
            return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
        } else {
            return b0 | (b1 << 8) | (b2 << 16) | (b3 << 24);
        }
    }

    private static String readString(InputStream in, int len) throws IOException {
        byte[] buf = new byte[len];
        for (int i=0; i<len; i++) {
            buf[i] = (byte) (readNoEOF(in) & 0xFF);
        }
        int end = readNoEOF(in);
        if (end != 0) {
            throw new IOException("Expected NUL terminator for string but got 0x" + Integer.toHexString(end));
        }
        return new String(buf, 0, len, "utf-8");
    }
}
