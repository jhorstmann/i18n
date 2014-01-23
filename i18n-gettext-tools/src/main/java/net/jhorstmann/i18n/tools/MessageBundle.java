package net.jhorstmann.i18n.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import org.fedorahosted.openprops.Properties;
import org.fedorahosted.tennera.jgettext.Catalog;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.PoParser;
import org.fedorahosted.tennera.jgettext.PoWriter;

public class MessageBundle implements Iterable<Message> {

    public final static class MessageBundleKey {

        private final String msgctx;
        private final String msgid;
        private final int hash;

        public MessageBundleKey(String msgid) {
            this(null, msgid);
        }

        public MessageBundleKey(String msgctx, String msgid) {
            this.msgctx = msgctx;
            this.msgid = msgid;
            this.hash = createHash(msgctx, msgid);
        }

        private static int createHash(String msgctx, String msgid) {
            int hash = 7;
            if (msgctx != null) {
                hash = hash * 31 + msgctx.hashCode();
            }
            if (msgid != null) {
                hash = hash * 31 + msgid.hashCode();
            }
            return hash;
        }

        public String getMsgctx() {
            return msgctx;
        }

        public String getMsgid() {
            return msgid;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MessageBundleKey)) {
                return false;
            }
            MessageBundleKey other = (MessageBundleKey) obj;
            if ((msgctx == null) ? (other.msgctx != null) : !this.msgctx.equals(other.msgctx)) {
                return false;
            }
            if ((this.msgid == null) ? (other.msgid != null) : !this.msgid.equals(other.msgid)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
    private final LinkedHashMap<MessageBundleKey, Message> messages = new LinkedHashMap<MessageBundleKey, Message>();
    private String basename;
    private String locale;
    private String pkgName;
    private String pkgVersion;
    private boolean template;
    private String footerComment;

    public static MessageBundleKey createKey(Message msg) {
        return new MessageBundleKey(msg.getMsgctxt(), msg.getMsgid());
    }

    public static MessageBundleKey createKey(String msgctx, String msgid) {
        return new MessageBundleKey(msgctx, msgid);
    }

    public static MessageBundleKey createKey(String msgid) {
        return new MessageBundleKey(msgid);
    }

    public static MessageBundle loadProperties(File file, String basename, String locale) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(file));
        MessageBundle bundle = new MessageBundle(file.getName(), props);
        bundle.setBasename(basename);
        bundle.setLocale(locale);
        return bundle;
    }

    public static MessageBundle loadProperties(InputStream in) throws IOException {
        Properties props = new Properties();
        props.load(in);
        return new MessageBundle(props);
    }

    public static MessageBundle loadProperties(Reader reader) throws IOException {
        Properties props = new Properties();
        props.load(reader);
        return new MessageBundle(props);
    }

    public static MessageBundle loadCatalog(File file) throws IOException {
        Catalog catalog = new PoParser().parseCatalog(file);
        return new MessageBundle(catalog);
    }

    public MessageBundle(Catalog catalog) {
        for (Message msg : catalog) {
            addMessage(msg);
        }
    }

    public MessageBundle(Properties props) {
        this(null, props);
    }

    public MessageBundle(String locale, Properties props) {
        for (String key : props.keySet()) {
            String property = props.getProperty(key);
            String comment = props.getComment(key);

            Message msg = new Message();
            msg.setMsgid(key);
            msg.setMsgstr(property);
            msg.addComment(comment);
            addMessage(msg);
        }
    }

    public MessageBundle() {
    }

    public String getBasename() {
        return basename;
    }

    public void setBasename(String basename) {
        this.basename = basename;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public boolean isTemplate() {
        return template;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    public int size() {
        return messages.size();
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }

    public final void addMessage(Message msg) {
        messages.put(createKey(msg), msg);
    }

    public Message getMessage(String msgctx, String msgid) {
        return messages.get(createKey(msgctx, msgid));
    }

    public Message getMessage(String msgid) {
        return messages.get(createKey(msgid));
    }

    public Set<MessageBundleKey> getKeys() {
        return messages.keySet();
    }

    public Set<String> getMessageIds() {
        Set<String> ids = new LinkedHashSet<String>();
        for (MessageBundleKey key : messages.keySet()) {
            String id = MessageUtils.serializeMsgid(key.getMsgctx(), key.getMsgid());
            ids.add(id);
        }
        return ids;
    }

    public Message getHeaderMessage() {
        return getMessage("");
    }

    public String getPluralForms() {
        Message header = getHeaderMessage();
        if (header == null) {
            return null;
        } else {
            return MessageUtils.getPluralForms(header);
        }
    }

    @Override
    public Iterator<Message> iterator() {
        return messages.values().iterator();
    }

    public Catalog toCatalog(boolean template) {
        Catalog catalog = new Catalog(template);
        for (Message message : messages.values()) {
            catalog.addMessage(message);
        }
        return catalog;
    }

    public java.util.Properties toProperties() {
        java.util.Properties props = new java.util.Properties();
        for (Message message : messages.values()) {
            String id = MessageUtils.serializeMsgid(message);
            String str = MessageUtils.serializeMsgstr(message);
            props.setProperty(id, str);
        }
        return props;
    }

    public Properties toOpenProps() {
        Properties props = new Properties();
        for (Message message : messages.values()) {
            String id = MessageUtils.serializeMsgid(message);
            String str = MessageUtils.serializeMsgstr(message);
            props.setProperty(id, str);
        }
        return props;
    }

    public void storeCatalog(File file) throws IOException {
        Catalog catalog = toCatalog(template);
        PoWriter pw = new PoWriter();
        pw.setGenerateHeader(!template);
        FileOutputStream fos = new FileOutputStream(file);
        try {
            pw.write(catalog, fos);
        } finally {
            fos.close();
        }
    }

    public void storeProperties(File file) throws IOException {
        Properties props = toOpenProps();
        FileOutputStream fos = new FileOutputStream(file);
        try {
            props.store(fos, null);
        } finally {
            fos.close();
        }
    }
}
