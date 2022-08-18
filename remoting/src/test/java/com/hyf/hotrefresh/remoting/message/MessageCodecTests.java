package com.hyf.hotrefresh.remoting.message;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * @author baB_hyf
 * @date 2022/05/28
 */
public class MessageCodecTests {

    @Test
    public void testDecode() {
        Message emptyMessage = MessageFactory.createEmptyMessage();
        emptyMessage.setMessageType(RpcMessageType.REQUEST_HEARTBEAT);
        byte[] encode = MessageCodec.encode(emptyMessage);
        ByteBuffer buf = ByteBuffer.allocateDirect(encode.length);
        buf.put(encode);
        buf.flip();

        Message decode = MessageCodec.decode(buf);

        System.out.println(decode);
    }
}
