package com.hyf.hotrefresh.core;

import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.core.util.Util;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class TestJavaModel {

    private final String name;

    private final String _package;

    public final String content;

    public TestJavaModel() {
        this("Test_" + UUID.randomUUID().toString().replace("-", ""));
    }

    public TestJavaModel(String name) {
        this(name, "com.hyf.hotrefresh");
    }

    public TestJavaModel(String name, String _package) {
        this.name = name;
        this._package = _package;
        this.content = "package " + _package + ";\n" +
                "public class " + name + "\n" +
                "{\n" +
                "    public static boolean get() {\n" +
                "        return false;\n" +
                "    }\n" +
                "}\n";
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return _package + "." + name;
    }

    public String getFileName() {
        return name + ".java";
    }

    public String getClassFileName() {
        return name + ".class";
    }

    public String getInnerClassFileName() {
        return getClassFileName() + ".HOT_REFRESH";
    }

    public String getContent() {
        return content;
    }

    public String getReplacedContent() {
        String content = getContent();
        return content.replace("return false", "return true");
    }

    public byte[] getClassBytes() {
        try (InputStream is = Util.getOriginContextClassLoader().getResourceAsStream("Test.class.HOT_REFRESH");
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtils.writeTo(is, baos);
            byte[] bytes = baos.toByteArray();

            ClassReader classReader = new ClassReader(bytes);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classReader.accept(new ClassVisitor(Opcodes.ASM5, classWriter) {
                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    super.visit(version, access, getName(), signature, superName, interfaces);
                }
            }, Opcodes.ASM5);
            return classWriter.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // public String getFilePath() {
    //     URL resource = Util.getOriginContextClassLoader().getResource(getFileName());
    //     return resource == null ? null : resource.toString();
    // }
}
