package com.hyf.hotrefresh.memory;

import com.hyf.hotrefresh.exception.CompileException;

import javax.tools.*;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class MemoryCompiler {

    private static final JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

    public static Map<String, byte[]> compile(String fileName, String content) throws CompileException {
        // simpleClassName
        int i = fileName.lastIndexOf(".");
        String name = i != -1 ? fileName.substring(0, i) : fileName;

        // 需要进行编译的代码
        Iterable<? extends JavaFileObject> compilationUnits = new ArrayList<JavaFileObject>() {
            {
                add(new JavaSourceFromString(name, content));
            }
        };

        // 编译的选项，对应于命令行参数
        List<String> options = new ArrayList<>();

        StandardJavaFileManager standardJavaFileManager = javaCompiler.getStandardFileManager(null, null, null);
        ScriptFileManager scriptFileManager = new ScriptFileManager(standardJavaFileManager);

        StringWriter errorStringWriter = new StringWriter();

        boolean ok = javaCompiler.getTask(errorStringWriter, scriptFileManager, diagnostic -> {
            if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {

                errorStringWriter.append(diagnostic.toString());
            }
        }, options, null, compilationUnits).call();

        if (!ok) {
            String errorMessage = errorStringWriter.toString();
            throw new CompileException(errorMessage);
        }

        return scriptFileManager.getAllBuffers();
    }
}
