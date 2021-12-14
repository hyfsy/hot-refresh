package com.hyf.hotrefresh.memory;

import com.hyf.hotrefresh.exception.CompileException;

import javax.tools.*;
import java.util.*;

/**
 * https://www.cnblogs.com/eoss/p/6136943.html
 *
 * @author baB_hyf
 * @date 2021/12/12
 */
public class MemoryCodeCompiler {

    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

    private static final List<String> OPTIONS = new ArrayList<String>() {{
        add("-Xlint:unchecked");
    }};

    public static Map<String, byte[]> compile(MemoryCode memoryCode) throws CompileException {
        return compile(Collections.singletonList(memoryCode));
    }

    public static Map<String, byte[]> compile(List<MemoryCode> memoryCodeList) throws CompileException {
        if (memoryCodeList == null || memoryCodeList.isEmpty()) {
            return new HashMap<>();
        }

        // check
        if (COMPILER == null) {
            throw new IllegalStateException("Cannot load JavaCompiler, please confirm the application running in JDK not JRE.");
        }

        try {
            MemoryByteCodeManager memoryCodeManager = new MemoryByteCodeManager(COMPILER.getStandardFileManager(null, null, null));
            DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();

            boolean success = COMPILER.getTask(null, memoryCodeManager, collector, OPTIONS, null, memoryCodeList).call();

            if (!success || collector.getDiagnostics().size() > 0) {
                handleDiagnosticMessage(collector);
            }

            return memoryCodeManager.getByteCodes();
        } catch (CompileException e) {
            throw e;
        } catch (Throwable e) {
            throw new CompileException("Compilation Error", e);
        }
    }

    private static void handleDiagnosticMessage(DiagnosticCollector<JavaFileObject> collector) throws CompileException {

        List<Diagnostic<? extends JavaFileObject>> errors = new ArrayList<>();
        // List<Diagnostic<? extends JavaFileObject>> warnings = new ArrayList<>();

        for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
            switch (diagnostic.getKind()) {
                case NOTE:
                case MANDATORY_WARNING:
                case WARNING:
                    // warnings.add(diagnostic);
                    break;
                case OTHER:
                case ERROR:
                default:
                    errors.add(diagnostic);
                    break;
            }
        }

        if (!errors.isEmpty()) {
            throw new CompileException("Compilation Error", errors);
        }
    }
}
