package com.hyf.hotrefresh.remoting.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class Message {

    private int                 id;
    private byte                encoding;
    private byte                codec;
    private byte                compress;
    private byte                messageType;
    private Map<String, String> metadata = new HashMap<>();
    private Object              body;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getEncoding() {
        return encoding;
    }

    public void setEncoding(byte encoding) {
        this.encoding = encoding;
    }

    public byte getCodec() {
        return codec;
    }

    public void setCodec(byte codec) {
        this.codec = codec;
    }

    public byte getCompress() {
        return compress;
    }

    public void setCompress(byte compress) {
        this.compress = compress;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return id == message.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", encoding=" + encoding +
                ", codec=" + codec +
                ", compress=" + compress +
                ", messageType=" + messageType +
                ", headerMap=" + metadata +
                ", body=" + body +
                '}';
    }
}
