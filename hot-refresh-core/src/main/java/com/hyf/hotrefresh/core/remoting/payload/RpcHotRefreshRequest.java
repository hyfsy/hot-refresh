package com.hyf.hotrefresh.core.remoting.payload;

import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcRequestInst;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class RpcHotRefreshRequest extends RpcRequest {

    // header length(4byte)
    // header
    // file name length(4byte)
    // file name
    // file location length (4byte)
    // file location
    // inst(1byte)
    // content length(4byte)
    // content

    public static final int FIXED_LENGTH = 4 + 4 + 4 + 1 + 4;

    private Map<String, Object> headers = new HashMap<>();
    private String              fileName;
    // @Nullable
    private String              fileLocation;
    private RpcRequestInst      inst;
    private InputStream         content;

    @Override
    public byte[] encode(RpcMessageEncoding encoding, RpcMessageCodec codec) {

        byte[] fileNameBytes = fileName.getBytes(encoding.getCharset());

        byte[] fileLocationBytes = new byte[0];
        if (fileLocation != null) {
            fileLocationBytes = fileLocation.getBytes(encoding.getCharset());
        }

        byte[] headerBytes = MessageCodec.encodeObject(headers, encoding, codec);

        byte[] contentBytes;
        try {
            contentBytes = IOUtils.readAsByteArray(content);
        } catch (IOException e) {
            throw new RuntimeException("Read input stream failed", e);
        } finally {
            try {
                content.close();
            } catch (IOException e) {
                throw new RuntimeException("Close input stream failed", e);
            }
        }

        int messageLength = FIXED_LENGTH + headerBytes.length + fileNameBytes.length + fileLocationBytes.length + contentBytes.length;

        ByteBuffer buf = ByteBuffer.allocate(messageLength);

        buf.putInt(headerBytes.length);
        buf.put(headerBytes);
        buf.putInt(fileNameBytes.length);
        buf.put(fileNameBytes);
        buf.putInt(fileLocationBytes.length);
        buf.put(fileLocationBytes);
        buf.put(inst.getCode());
        buf.putInt(contentBytes.length);
        buf.put(contentBytes);

        return buf.array();
    }

    @Override
    public void decode(byte[] bytes, RpcMessageEncoding encoding, RpcMessageCodec codec) {

        ByteBuffer buf = ByteBuffer.wrap(bytes);

        int headerLength = buf.getInt();
        byte[] headerBytes = new byte[headerLength];
        buf.get(headerBytes);

        int fileNameLength = buf.getInt();
        byte[] fileNameBytes = new byte[fileNameLength];
        buf.get(fileNameBytes);

        int fileLocationLength = buf.getInt();
        byte[] fileLocationBytes = new byte[fileLocationLength];
        buf.get(fileLocationBytes);

        byte instCode = buf.get();

        int contentLength = buf.getInt();
        byte[] contentBytes = new byte[contentLength];
        buf.get(contentBytes);

        this.setHeaders(MessageCodec.decodeObject(headerBytes, encoding, codec));
        this.setFileName(new String(fileNameBytes, encoding.getCharset()));
        this.setFileLocation(new String(fileLocationBytes, encoding.getCharset()));
        this.setInst(RpcRequestInst.getInst(instCode));
        this.setContent(new ByteArrayInputStream(contentBytes));
    }

    @Override
    public byte getMessageCode() {
        return RpcMessageType.REQUEST_HOT_REFRESH;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public RpcRequestInst getInst() {
        return inst;
    }

    public void setInst(RpcRequestInst inst) {
        this.inst = inst;
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RpcHotRefreshRequest that = (RpcHotRefreshRequest) o;
        return Objects.equals(headers, that.headers) && Objects.equals(fileName, that.fileName) && Objects.equals(fileLocation, that.fileLocation) && inst == that.inst;
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers, fileName, fileLocation, inst);
    }
}
