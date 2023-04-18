package com.hyf.hotrefresh.core.classloader;

import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.core.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class ShadeClassLoader extends ExtendClassLoader {

    private final List<String> builtInClasses = new LinkedList<>();
    private final String       sourcePackage;
    private final String       shadePackage;

    public ShadeClassLoader(String sourcePackage, String shadePackage) {
        this(Util.getOriginContextClassLoader(), sourcePackage, shadePackage);
    }

    public ShadeClassLoader(ClassLoader parent, String sourcePackage, String shadePackage) {
        super(parent);
        if (parent == null) {
            throw new IllegalArgumentException("Parent must not be null");
        }
        this.sourcePackage = sourcePackage;
        this.shadePackage = shadePackage;
    }

    public void addBuiltInClass(String name) {
        builtInClasses.add(name);
    }

    public void removeBuiltInClass(String name) {
        builtInClasses.remove(name);
    }

    @Override
    protected Class<?> brokenLoadClass(String name) throws ClassNotFoundException {

        if (name.startsWith(sourcePackage)) {
            name = name.replace(sourcePackage, shadePackage);
        }
        else if (!builtInClasses.contains(name)) { // let parent to load
            throw new ClassNotFoundException(name);
        }

        try (InputStream is = getParent().getResourceAsStream(name.replace(".", "/") + ".class")) {
            byte[] bytes = IOUtils.readAsByteArray(is);
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }
}
