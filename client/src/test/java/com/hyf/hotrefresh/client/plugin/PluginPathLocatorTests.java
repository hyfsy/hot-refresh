package com.hyf.hotrefresh.client.plugin;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * @author baB_hyf
 * @date 2023/04/05
 */
public class PluginPathLocatorTests {

    @Test
    public void testGetPath() {
        File path = PluginPathLocator.getPath();
        String homePath = getClass().getResource("").getPath();
        String target = homePath.substring(0, homePath.lastIndexOf("/target") + "/target".length());
        assertEquals(path.getAbsolutePath(), new File(target, "/classes/" + PluginPathLocator.PLUGIN_DIR_NAME).getAbsolutePath());
    }
}
