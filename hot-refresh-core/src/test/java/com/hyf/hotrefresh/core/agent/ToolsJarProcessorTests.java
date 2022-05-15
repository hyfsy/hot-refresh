package com.hyf.hotrefresh.core.agent;

import com.hyf.hotrefresh.common.Constants;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class ToolsJarProcessorTests {

    public static final String NOT_EXIST_TOOLS_JAR_PATH = Constants.REFRESH_HOME + File.separator + "test";

    @Test
    public void testGetToolsJarPath() {
        ToolsJarProcessor processor = new ToolsJarProcessor();
        assertNotNull(processor.getToolsJarPath());
    }

    @Test
    public void testSetDefaultPathToGetToolsJarPath() {
        ToolsJarProcessor processor = new ToolsJarProcessor();
        assertNull(processor.getToolsJarPath(NOT_EXIST_TOOLS_JAR_PATH));
    }

    @Test
    public void testSetSystemPropertyToGetToolsJarPath() {
        System.setProperty(ToolsJarProcessor.TOOLS_JAR_PATH_PROPERTY, NOT_EXIST_TOOLS_JAR_PATH);
        ToolsJarProcessor processor = new ToolsJarProcessor();
        assertEquals(processor.getToolsJarPath(), NOT_EXIST_TOOLS_JAR_PATH);
    }
}
