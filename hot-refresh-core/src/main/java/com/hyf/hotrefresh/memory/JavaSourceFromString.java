package com.hyf.hotrefresh.memory;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class JavaSourceFromString extends SimpleJavaFileObject {

    private final String code;

    public JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
