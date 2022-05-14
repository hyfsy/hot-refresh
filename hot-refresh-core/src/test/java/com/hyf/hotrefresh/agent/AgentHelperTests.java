package com.hyf.hotrefresh.agent;

import com.hyf.hotrefresh.util.Util;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class AgentHelperTests {

    @Test
    public void testGetAttachmentProvider() throws ClassNotFoundException {
        Object attachmentProvider = AgentHelper.getAttachmentProvider();
        assertNotNull(attachmentProvider);

        Class<?> clazz = Util.getInfrastructureJarClassLoader().loadClass("net.bytebuddy.agent.ByteBuddyAgent$AttachmentProvider");
        assertThat(attachmentProvider, instanceOf(clazz));
    }
}
