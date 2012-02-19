package net.jhorstmann.i18n.tools;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class MoWriter {

    public static void writeInt(OutputStream os, int i) throws IOException {
        os.write((i) & 0xFF);
        os.write((i >>> 8) & 0xFF);
        os.write((i >>> 16) & 0xFF);
        os.write((i >>> 24) & 0xFF);
    }

    public static void writeFile_old(String filename, Map<String, String> polines) throws FileNotFoundException, IOException {

        DataOutputStream os = new DataOutputStream(new FileOutputStream(filename));
        HashMap<String, String> bvc = new HashMap<String, String>();
        TreeMap<String, String> hash = new TreeMap(bvc);
        hash.putAll(polines);


        StringBuilder ids = new StringBuilder();
        StringBuilder strings = new StringBuilder();
        ArrayList<ArrayList> offsets = new ArrayList<ArrayList>();
        ArrayList<Integer> key_offsets = new ArrayList<Integer>();
        ArrayList<Integer> value_offsets = new ArrayList<Integer>();
        ArrayList<Integer> temp_offsets = new ArrayList<Integer>();

        for (Map.Entry<String, String> entry : hash.entrySet()) {
            String id = entry.getKey();
            String str = entry.getValue();

            ArrayList<Integer> offsetsItems = new ArrayList<Integer>();
            offsetsItems.add(ids.toString().getBytes("utf-8").length);
            offsetsItems.add(id.getBytes("utf-8").length);
            offsetsItems.add(strings.toString().getBytes("utf-8").length);
            offsetsItems.add(str.getBytes("utf-8").length);
            offsets.add((ArrayList) offsetsItems.clone());

            ids.append(id).append('\0');
            strings.append(str).append('\0');
        }
        Integer key_start = 7 * 4 + hash.size() * 4 * 4;
        Integer value_start = key_start + ids.toString().getBytes("utf-8").length;

        Iterator e = offsets.iterator();
        while (e.hasNext()) {
            ArrayList<Integer> offEl = (ArrayList<Integer>) e.next();
            key_offsets.add(offEl.get(1));
            key_offsets.add(offEl.get(0) + key_start);
            value_offsets.add(offEl.get(3));
            value_offsets.add(offEl.get(2) + value_start);
        }

        temp_offsets.addAll(key_offsets);
        temp_offsets.addAll(value_offsets);

        //writeInt(os, 0xDE120495);
        os.writeByte(0xDE);
        os.writeByte(0x12);
        os.writeByte(0x04);
        os.writeByte(0x95);
        writeInt(os, 0x00);

        writeInt(os, hash.size());
        writeInt(os, (7 * 4));
        writeInt(os, (7 * 4 + hash.size() * 8));
        writeInt(os, 0x00);
        writeInt(os, key_start);

        Iterator offi = temp_offsets.iterator();
        while (offi.hasNext()) {
            Integer off = (Integer) offi.next();
            writeInt(os, off);
        }
        os.write(ids.toString().getBytes("utf-8"));
        os.write(strings.toString().getBytes("utf-8"));
        //os.writeUTF(ids.toString());
        //os.writeUTF(strings.toString());

        os.close();
    }

    public static void writeFile(String filename, TreeMap<String, String> polines) throws IOException {
        OutputStream os = new BufferedOutputStream(new FileOutputStream(filename));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int size = polines.size();
        int[] indices = new int[size*2];
        int[] lengths = new int[size*2];
        int idx = 0;
        // write the strings and translations to a byte array and remember offsets and length in bytes
        for (String key : polines.keySet()) {
            byte[] utf = key.getBytes("utf-8");
            indices[idx] = bos.size();
            lengths[idx] = utf.length;
            bos.write(utf);
            bos.write(0);
            idx++;
        }
        for (String val : polines.values()) {
            byte[] utf = val.getBytes("utf-8");
            indices[idx] = bos.size();
            lengths[idx] = utf.length;
            bos.write(utf);
            bos.write(0);
            idx++;
        }

        try {
            int headerLength = 7*4;
            int tableLength = size*2*2*4;
            writeInt(os, 0x950412DE);                   // magic
            writeInt(os, 0);                            // file format revision
            writeInt(os, size);                         //number of strings
            writeInt(os, headerLength);                 // offset of table with original strings
            writeInt(os, headerLength + tableLength/2); // offset of table with translation strings
            writeInt(os, 0);                            // size of hashing table
            writeInt(os, headerLength + tableLength);   // offset of hashing table

            for (int i=0; i<size*2; i++) {
                writeInt(os, lengths[i]);
                writeInt(os, headerLength + tableLength + indices[i]);
            }

            // copy keys and translations
            bos.writeTo(os);

        } finally {
            os.close();
        }
    }

    public static void main(String[] args) throws IOException {
        TreeMap<String, String> polines = new TreeMap<String, String>();
        //polines.put("Hello World From Managed Bean", "Hallo Welt von einer gemanagten Bohne");
        polines.put("Access denied.", "Hozz  f  r  s megtagadva.");
        polines.put("Embeds a Vimeo Player in your web page.", "Youtube vide   beilleszt  se a weboldalra.");
        writeFile("test.mo", polines);
    }
}
