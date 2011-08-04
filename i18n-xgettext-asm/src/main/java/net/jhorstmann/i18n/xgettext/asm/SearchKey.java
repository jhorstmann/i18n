package net.jhorstmann.i18n.xgettext.asm;

public class SearchKey {
    private String owner;
    private String name;
    private String desc;
    private int messageIndex;
    private int contextIndex;
    private int pluralIndex;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getMessageIndex() {
        return messageIndex;
    }

    public void setMessageIndex(int messageIndex) {
        this.messageIndex = messageIndex;
    }

    public int getContextIndex() {
        return contextIndex;
    }

    public void setContextIndex(int contextIndex) {
        this.contextIndex = contextIndex;
    }

    public int getPluralIndex() {
        return pluralIndex;
    }

    public void setPluralIndex(int pluralIndex) {
        this.pluralIndex = pluralIndex;
    }
}
