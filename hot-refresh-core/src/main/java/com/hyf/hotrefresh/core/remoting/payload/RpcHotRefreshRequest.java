package com.hyf.hotrefresh.core.remoting.payload;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcRequestInst;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcRequest;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class RpcHotRefreshRequest extends RpcRequest {

    // file name length(4byte)
    // file name
    // file location length (4byte)
    // file location
    // inst(1byte)

    public static final int FIXED_LENGTH = 4 + 4 + 1;

    private String         fileName;
    // @Nullable
    private String         fileLocation;
    private RpcRequestInst inst;

    @Override
    public ByteBuffer encode(RpcMessageEncoding encoding, RpcMessageCodec codec) {

        ByteBuffer superBuf = super.encode(encoding, codec);

        byte[] fileNameBytes = fileName.getBytes(encoding.getCharset());

        byte[] fileLocationBytes = new byte[0];
        if (fileLocation != null) {
            fileLocationBytes = fileLocation.getBytes(encoding.getCharset());
        }

        int messageLength = superBuf.limit() + FIXED_LENGTH + fileNameBytes.length + fileLocationBytes.length;

        ByteBuffer buf = ByteBuffer.allocate(messageLength);
        buf.put(superBuf);
        buf.putInt(fileNameBytes.length);
        buf.put(fileNameBytes);
        buf.putInt(fileLocationBytes.length);
        buf.put(fileLocationBytes);
        buf.put(inst.getCode());

        return buf;
    }

    @Override
    public void decode(ByteBuffer buf, RpcMessageEncoding encoding, RpcMessageCodec codec) {

        super.decode(buf, encoding, codec);

        int fileNameLength = buf.getInt();
        byte[] fileNameBytes = new byte[fileNameLength];
        buf.get(fileNameBytes);

        int fileLocationLength = buf.getInt();
        byte[] fileLocationBytes = new byte[fileLocationLength];
        buf.get(fileLocationBytes);

        byte instCode = buf.get();

        this.setFileName(new String(fileNameBytes, encoding.getCharset()));
        this.setFileLocation(new String(fileLocationBytes, encoding.getCharset()));
        this.setInst(RpcRequestInst.getInst(instCode));
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

    public RpcRequestInst getInst() {
        return inst;
    }

    public void setInst(RpcRequestInst inst) {
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
}
