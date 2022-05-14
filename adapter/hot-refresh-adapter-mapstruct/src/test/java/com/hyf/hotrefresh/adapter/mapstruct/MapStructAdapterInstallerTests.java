package com.hyf.hotrefresh.adapter.mapstruct;

import com.hyf.hotrefresh.util.InfrastructureJarClassLoader;
import org.junit.Before;
import org.junit.Test;

/**
 * @author baB_hyf
 * @date 2022/05/12
 */
public class MapStructAdapterInstallerTests {

    private MapStructAdapterInstaller installer;

    @Before
    public void before() {
        installer = new MapStructAdapterInstaller();
        installer.install();
    }

    @Test
    public void testLoadMapStructClass() throws ClassNotFoundException {
        InfrastructureJarClassLoader.getInstance().loadClass("org.mapstruct.Mapper");
        InfrastructureJarClassLoader.getInstance().loadClass("org.mapstruct.ap.MappingProcessor");
    }
}
