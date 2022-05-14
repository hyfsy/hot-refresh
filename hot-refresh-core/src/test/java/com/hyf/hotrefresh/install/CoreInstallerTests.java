package com.hyf.hotrefresh.install;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class CoreInstallerTests {

    private MockInstaller mockInstaller;

    @Before
    public void before() {
        mockInstaller = new MockInstaller();
    }

    @After
    public void after() {
        MockInstaller.executed = false;
    }

    @Test
    public void testInstallerInstall() {
        assertFalse(MockInstaller.isExecuted());
        mockInstaller.install();
        assertTrue(MockInstaller.isExecuted());
    }

    @Test
    public void testCoreInstallerInstall() {
        assertFalse(MockInstaller.isExecuted());
        CoreInstaller.install();
        assertTrue(MockInstaller.isExecuted());
    }

    @Test
    public void testCoreInstallerEnabled() {
        assertFalse(MockInstaller.isExecuted());
        assertTrue(CoreInstaller.enable());
        assertTrue(MockInstaller.isExecuted());
    }

    public static class MockInstaller implements Installer {

        private static boolean executed;

        public static boolean isExecuted() {
            return executed;
        }

        @Override
        public void install() {
            executed = true;
        }
    }
}
