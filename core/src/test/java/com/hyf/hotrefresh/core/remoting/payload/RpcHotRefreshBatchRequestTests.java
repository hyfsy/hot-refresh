package com.hyf.hotrefresh.core.remoting.payload;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RpcHotRefreshBatchRequestTests {

    @Test
    public void testEncodeAndDecode() throws IOException {
        RpcHotRefreshBatchRequest request = new RpcHotRefreshBatchRequest();

        ByteBuffer buffer = request.encode(RpcMessageEncoding.UTF_8, RpcMessageCodec.JDK);
        RpcHotRefreshBatchRequest decodedRequest = new RpcHotRefreshBatchRequest();
        buffer.flip();
        decodedRequest.decode(buffer, RpcMessageEncoding.UTF_8, RpcMessageCodec.JDK);
        assertEquals(decodedRequest.getRequests().size(), 0);

        RpcHotRefreshRequest req = new RpcHotRefreshRequest();
        req.setInst(RpcHotRefreshRequestInst.CREATE);
        req.setFileLocation("E:\\xxx1.java");
        req.setFileName("xxx1.java");
        req.setBody(new ByteArrayInputStream(new byte[]{1, 2, 3}));
        request.addRequest(req);

        buffer = request.encode(RpcMessageEncoding.UTF_8, RpcMessageCodec.JDK);
        decodedRequest = new RpcHotRefreshBatchRequest();
        buffer.flip();
        decodedRequest.decode(buffer, RpcMessageEncoding.UTF_8, RpcMessageCodec.JDK);
        assertRequest(decodedRequest, 1);

        req = new RpcHotRefreshRequest();
        req.setInst(RpcHotRefreshRequestInst.MODIFY);
        req.setFileLocation("E:\\xxx2.java");
        req.setFileName("xxx2.java");
        req.setBody(new ByteArrayInputStream(new byte[]{1, 2, 3, 4}));
        for (RpcHotRefreshRequest requestRequest : request.getRequests()) {
            requestRequest.getBody().reset();
        }
        request.addRequest(req);

        buffer = request.encode(RpcMessageEncoding.UTF_8, RpcMessageCodec.JDK);
        decodedRequest = new RpcHotRefreshBatchRequest();
        buffer.flip();
        decodedRequest.decode(buffer, RpcMessageEncoding.UTF_8, RpcMessageCodec.JDK);
        assertRequest(decodedRequest, 2);
    }

    private void assertRequest(RpcHotRefreshBatchRequest decodedRequest, int size) {
        assertEquals(decodedRequest.getRequests().size(), size);
        for (RpcHotRefreshRequest request : decodedRequest.getRequests()) {
            assertNotNull(request.getFileName());
            assertNotNull(request.getFileLocation());
            assertNotNull(request.getInst());
            assertNotNull(request.getBody());
        }
    }
}
