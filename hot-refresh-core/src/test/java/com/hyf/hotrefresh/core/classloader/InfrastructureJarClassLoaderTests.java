package com.hyf.hotrefresh.core.classloader;

import com.hyf.hotrefresh.core.TestJavaFileUtils;
import com.hyf.hotrefresh.core.util.InfraUtils;
import com.hyf.hotrefresh.core.util.Util;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class InfrastructureJarClassLoaderTests {

    @Test(expected = ClassNotFoundException.class)
    public void testNotRegisterInfrastructureJar() throws ClassNotFoundException {
        Util.getOriginContextClassLoader().loadClass(TestJavaFileUtils.getClassName());
    }

    @Test
    public void testRegisterInfrastructureJar() {
        String className = "com.hyf.hotrefresh.generate.ExtraClass";
        URL resource = Util.getOriginContextClassLoader().getResource("");
        assertNotNull(resource);

        String filePath = resource.getPath();
        Util.getInfrastructureJarClassLoader().registerInfrastructureDirectory("test", filePath);
        Class<?> clazz = InfraUtils.forName(className);
        assertNotNull(clazz);
        assertSame(clazz.getClassLoader(), Util.getInfrastructureJarClassLoader());
    }

    @Test
    public void testRegisterDefaultInfrastructureJar() throws MalformedURLException {
        String identity = "test-directory";
        URL url = new File("E:\\").toURI().toURL();
        InfrastructureJarClassLoader infra = Util.getInfrastructureJarClassLoader();
        URL defaultUrl = infra.getRegisteredURLMap().get(identity);
        if (defaultUrl != null) {
            assertNotNull(defaultUrl);
            infra.registerInfrastructureURL(identity, url);
            Map<String, URL> registeredURLMap = infra.getRegisteredURLMap();
            assertNotEquals(url, registeredURLMap.get(identity));
        }
    }

    @Test
    public void testLoadDefaultInfrastructureJarClass() throws ClassNotFoundException {
       boolean hasCustomByteBuddy = InfrastructureJarClassLoader.getDefaultIdentityMap().containsKey("byte-buddy");
        if (hasCustomByteBuddy) {
            Class<?> clazz = Util.getInfrastructureJarClassLoader().loadClass("net.bytebuddy.agent.ByteBuddyAgent");
            String location = clazz.getProtectionDomain().getCodeSource().getLocation().toString();
            assertTrue(location.contains("E:") || location.contains("6"));
        }
    }
}
