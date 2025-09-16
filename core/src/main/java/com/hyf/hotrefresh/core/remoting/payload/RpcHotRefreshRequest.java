package com.hyf.hotrefresh.core.remoting.payload;

import com.hyf.hotrefresh.common.util.StringUtils;
import com.hyf.hotrefresh.remoting.exception.RpcException;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcRequest;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class RpcHotRefreshRequest extends RpcRequest {

    // super content
    // file name length(4byte)
    // file name
    // file location length (4byte)
    // file location
    // inst(1byte)

    public static final int FIXED_LENGTH = 4 + 4 + 1;

    private String                   fileName;
    private String                   fileLocation;
    private RpcHotRefreshRequestInst inst;

    @Override
    public ByteBuffer encode(RpcMessageEncoding encoding, RpcMessageCodec codec) {

        ByteBuffer superBuf = super.encode(encoding, codec);

        int messageLength = superBuf.limit() + FIXED_LENGTH;

        byte[] fileNameBytes = null;
        if (StringUtils.isNotBlank(fileName)) {
            fileNameBytes = fileName.getBytes(encoding.getCharset());
            messageLength += fileNameBytes.length;
        }

        byte[] fileLocationBytes = null;
        if (StringUtils.isNotBlank(fileLocation)) {
            fileLocationBytes = fileLocation.getBytes(encoding.getCharset());
            messageLength += fileLocationBytes.length;
        }

        ByteBuffer buf = ByteBuffer.allocate(messageLength);

        superBuf.flip(); // 可写
        buf.put(superBuf);

        buf.putInt(fileNameBytes == null ? 0 : fileNameBytes.length);
        if (fileNameBytes != null) {
            buf.put(fileNameBytes);
        }

        buf.putInt(fileLocationBytes == null ? 0 : fileLocationBytes.length);
        if (fileLocationBytes != null) {
            buf.put(fileLocationBytes);
        }

        if (inst == null) {
            throw new RpcException("Request inst must not be null");
        }
        buf.put(inst.getCode());

        return buf;
    }

    @Override
    public void decode(ByteBuffer buf, RpcMessageEncoding encoding, RpcMessageCodec codec) {

        super.decode(buf, encoding, codec);

        int fileNameLength = buf.getInt();
        if (fileNameLength != 0) {
            byte[] fileNameBytes = new byte[fileNameLength];
            buf.get(fileNameBytes);
            this.setFileName(new String(fileNameBytes, encoding.getCharset()));
        }

        int fileLocationLength = buf.getInt();
        if (fileLocationLength != 0) {
            byte[] fileLocationBytes = new byte[fileLocationLength];
            buf.get(fileLocationBytes);
            this.setFileLocation(new String(fileLocationBytes, encoding.getCharset()));
        }

        byte instCode = buf.get();
        this.setInst(RpcHotRefreshRequestInst.getInst(instCode));
    }

    @Override
    public byte getMessageCode() {
        return RpcMessageType.REQUEST_HOT_REFRESH;
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

    public RpcHotRefreshRequestInst getInst() {
        return inst;
    }

    public void setInst(RpcHotRefreshRequestInst inst) {
        this.inst = inst;
    }

    @Override
    public boolean equals(Object o) {
        RpcHotRefreshRequest that = (RpcHotRefreshRequest) o;
        return super.equals(o) && Objects.equals(fileName, that.fileName) && Objects.equals(fileLocation,
                that.fileLocation) && inst == that.inst;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileName, fileLocation, inst);
    }

    @Override
    public String toString() {
        return "[" + inst.name() + "] " + fileLocation;
    }
}
