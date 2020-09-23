package org.ar.audioganme.model;

public class MessageListBean {

    public static final int MSG_SYSYTEM = 0;
    public static final int MSG_NORMAL = 1;
    public static final int MSG_MEMBER_CHANGE =2;
    public static final int MSG_GIFT =3;

    public int type;
    public String name;
    public String content;
    public String id;
    public String toId;
    public String gift;

    public MessageListBean(int type) {
        this.type = type;
    }

    public MessageListBean(int type,String id) {
        this.type = type;
        this.id = id;
    }

    public MessageListBean(int type, String id,String content) {
        this.type = type;
        this.id =id;
        this.content = content;
    }

    public MessageListBean(int type, String fromId,String content,String toId,String gift) {
        this.type = type;
        this.content = content;
        this.id = fromId;
        this.toId = toId;
        this.gift = gift;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    @Override
    public String toString() {
        return "MessageListBean{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", toId='" + toId + '\'' +
                ", gift='" + gift + '\'' +
                '}';
    }
}
