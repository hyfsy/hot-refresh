package com.hyf.hotrefresh.core.memory;

import com.hyf.hotrefresh.common.Services;
import com.hyf.hotrefresh.common.util.ResourceUtils;
import com.hyf.hotrefresh.core.exception.CompileException;
import com.hyf.hotrefresh.core.util.InfraUtils;
import com.hyf.hotrefresh.core.util.Util;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * https://www.cnblogs.com/eoss/p/6136943.html
 *
 * @author baB_hyf
 * @date 2021/12/12
 */
public class MemoryCodeCompiler {

    public static final String OPTIONS_FILE_RESOURCE_PATH = "compile-options.properties";

    private static final List<String> OPTIONS = new ArrayList<>();

    private static final JavaCompiler COMPILER = InfraUtils.getJavaCompiler();

    static {
        initOptions();
    }

    public static Map<String, byte[]> compile(MemoryCode memoryCode) throws CompileException {
        return compile(Collections.singleton(memoryCode));
    }

    public static Map<String, byte[]> compile(MemoryCode memoryCode, ClassLoader classLoader) throws CompileException {
        return compile(Collections.singleton(memoryCode), classLoader);
    }

    public static Map<String, byte[]> compile(Set<MemoryCode> memoryCodeList) throws CompileException {
        return compile(memoryCodeList, MemoryClassLoader.newInstance(Util.getInfrastructureJarClassLoader()));
    }

    public static Map<String, byte[]> compile(Set<MemoryCode> memoryCodeList, ClassLoader classLoader) throws CompileException {
        if (memoryCodeList == null || memoryCodeList.isEmpty()) {
            return new HashMap<>();
        }

        if (COMPILER == null) {
            throw new IllegalStateException("Cannot load JavaCompiler, please confirm the application running in JDK not JRE.");
        }

        try {
            MemoryJavaFileManager memoryByteCodeManager = new MemoryJavaFileManager(COMPILER.getStandardFileManager(null, null, null), classLoader);
            DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();

            boolean success = COMPILER.getTask(null, memoryByteCodeManager, collector, OPTIONS, null, memoryCodeList).call();

            if (!success || collector.getDiagnostics().size() > 0) {
                handleDiagnosticMessage(collector);
            }

            Map<String, byte[]> compiledBytes = memoryByteCodeManager.getByteCodes();
            return obfuscation(compiledBytes);
        } catch (CompileException e) {
            throw e;
        } catch (Throwable t) {
            String fileNames = memoryCodeList.stream().map(MemoryCode::getFileName).collect(Collectors.joining(",", "[", "]"));
            throw new CompileException("Compilation Error: " + fileNames, t);
        }
    }

    public static List<String> getCompileOptions() {
        return Collections.unmodifiableList(OPTIONS);
    }

    private static void initOptions() {
        List<String> optionsList = ResourceUtils.readPropertiesAsRows(OPTIONS_FILE_RESOURCE_PATH, Util.getOriginContextClassLoader());
        OPTIONS.addAll(new HashSet<>(optionsList));
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

    private static Map<String, byte[]> obfuscation(Map<String, byte[]> compiledBytes) throws CompileException {
        try {
            List<ObfuscationHandler> obfuscationHandlers = Services.gets(ObfuscationHandler.class);
            for (ObfuscationHandler handler : obfuscationHandlers) {
                compiledBytes = handler.handle(compiledBytes);
            }
        } catch (Throwable t) {
            throw new CompileException("Obfuscation failed: " + compiledBytes.keySet(), t);
        }

        return compiledBytes;
    }
}
