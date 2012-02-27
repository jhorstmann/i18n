package net.jhorstmann.i18n.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.fedorahosted.tennera.jgettext.Message;

public class MoWriter {

    private static void writeInt(OutputStream os, int i) throws IOException {
        os.write((i) & 0xFF);
        os.write((i >>> 8) & 0xFF);
        os.write((i >>> 16) & 0xFF);
        os.write((i >>> 24) & 0xFF);
    }

    public static void writeMessages(File file, MessageBundle bundle) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        try {
            writeMessages(out, bundle);
        } finally {
            out.close();
        }
    }

    public static void writeMessages(OutputStream os, MessageBundle bundle) throws IOException {
        int size = bundle.size();
        byte[][] strings = new byte[size*2][];
        int[] indices = new int[size*2];
        int[] lengths = new int[size*2];
        int idx = 0;
        int off = 0;
        for (Message msg : bundle) {
            byte[] utf = MessageUtils.serializeMsgidUTF8(msg);
            strings[idx] = utf;
            indices[idx] = off;
            lengths[idx] = utf.length;
            off+= utf.length+1;
            idx++;
        }
        for (Message msg : bundle) {
            byte[] str = MessageUtils.serializeMsgstrUTF8(msg);
            strings[idx] = str;
            indices[idx] = off;
            lengths[idx] = str.length;
            off+=str.length+1;
            idx++;
        }

        try {
            int headerLength = 7*4;
            int tableLength = size*2*2*4;
            writeInt(os, 0x950412DE);                   // magic
            writeInt(os, 0);                            // file format revision
            writeInt(os, size);                         // number of strings
            writeInt(os, headerLength);                 // offset of table with original strings
            writeInt(os, headerLength + tableLength/2); // offset of table with translation strings
            writeInt(os, 0);                            // size of hashing table
            writeInt(os, headerLength + tableLength);   // offset of hashing table

            for (int i=0; i<size*2; i++) {
                writeInt(os, lengths[i]);
                writeInt(os, headerLength + tableLength + indices[i]);
            }

            for (int i=0; i<size*2; i++) {
                os.write(strings[i]);
                os.write(0);
            }

        } finally {
            os.close();
        }
    }
}
