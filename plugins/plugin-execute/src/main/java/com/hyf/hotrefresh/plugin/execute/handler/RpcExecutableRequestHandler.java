package com.hyf.hotrefresh.plugin.execute.handler;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.util.ExceptionUtils;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.common.util.ReflectionUtils;
import com.hyf.hotrefresh.common.util.StringUtils;
import com.hyf.hotrefresh.core.memory.MemoryCode;
import com.hyf.hotrefresh.core.memory.MemoryCodeCompiler;
import com.hyf.hotrefresh.core.util.InfraUtils;
import com.hyf.hotrefresh.core.util.Util;
import com.hyf.hotrefresh.plugin.execute.Executable;
import com.hyf.hotrefresh.plugin.execute.ExecutableClassLoader;
import com.hyf.hotrefresh.plugin.execute.exception.ExecutionException;
import com.hyf.hotrefresh.plugin.execute.payload.RpcExecutableRequest;
import com.hyf.hotrefresh.plugin.execute.payload.RpcExecutableResponse;
import com.hyf.hotrefresh.plugin.fastjson.FastjsonUtils;
import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandler;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcResponse;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/17
 */
public class RpcExecutableRequestHandler implements RpcMessageHandler<RpcExecutableRequest, RpcExecutableResponse> {

    @Override
    public RpcExecutableResponse handle(RpcExecutableRequest request) throws Exception {

        try (InputStream content = request.getBody()) {

            Class<?> clazz = loadContent(content, request.getFileName(), request.getFileLocation());

            RpcExecutableResponse response = new RpcExecutableResponse();
            Object result = null;
            try {
                if (Executable.class.isAssignableFrom(clazz)) {
                    Executable<?> executable = (Executable<?>) ReflectionUtils.newClassInstance(clazz);
                    result = executable.execute();
                }
                else if (hasExecuteMethod(clazz)) {
                    Method executeMethod = getExecuteMethod(clazz);
                    assert executeMethod != null;
                    if (isStaticMethod(executeMethod)) {
                        result = ReflectionUtils.invokeMethod(executeMethod, null);
                    }
                    else {
                        Object executable = ReflectionUtils.newClassInstance(clazz);
                        result = ReflectionUtils.invokeMethod(executeMethod, executable);
                    }
                }
                else if (hasMainMethod(clazz)) {
                    Method mainMethod = getMainMethod(clazz);
                    assert mainMethod != null;
                    result = ReflectionUtils.invokeMethod(mainMethod, null, /* must specify mandatory */ (Object) new String[0]);
                }
                else {
                    if (Log.isDebugMode()) {
                        Log.debug("Uploaded class not an executable class, please implements Executable interface or create standard main method: " + request.getFileLocation());
                    }
                    response.setStatus(RpcResponse.SUCCESS);
                    return response;
                }

                response.setStatus(RpcResponse.SUCCESS);
                response.setData(FastjsonUtils.objectToJson(result).getBytes(RemotingConstants.DEFAULT_ENCODING.getCharset()));
            } catch (Throwable t) {
                response.setStatus(RpcResponse.ERROR);
                HashMap<String, Object> extra = new HashMap<>();
                extra.put(RemotingConstants.EXTRA_EXCEPTION_STACK, ExceptionUtils.getStackMessage(t));
                response.setExtra(extra);
            }

            return response;
        }
    }

    private Class<?> loadContent(InputStream content, String fileName, String fileLocation) throws Exception {

        ExecutableClassLoader cl = new ExecutableClassLoader();

        String className;
        if (fileName.endsWith(".java")) {
            String javaFileContent = IOUtils.readAsString(content);
            Map<String, byte[]> compiledBytes = MemoryCodeCompiler.compile(new MemoryCode(fileName, javaFileContent), Util.getThrowawayHotRefreshClassLoader());
            if (compiledBytes.isEmpty()) {
                throw new ExecutionException("Nothing has been compiled: \n" + javaFileContent);
            }
            compiledBytes.forEach(cl::addExecutable);
            className = compiledBytes.keySet().toArray(new String[0])[0];
        }
        else if (fileName.endsWith(".class")) {
            byte[] bytes = IOUtils.readAsByteArray(content);
            className = InfraUtils.getClassName(bytes);
            cl.addExecutable(className, bytes);
        }
        else {
            throw new ExecutionException("File name illegal: " + fileName);
        }

        if (StringUtils.isBlank(className)) {
            throw new ExecutionException("Cannot determinate the class name, file location: " + fileLocation);
        }

        isolateSensitiveInstruction(cl);
        return cl.loadClass(className);
    }

    private void isolateSensitiveInstruction(ExecutableClassLoader cl) {
        // TODO System.out -> custom PrintStream
    }

    private boolean hasMainMethod(Class<?> clazz) {
        return getMainMethod(clazz) != null;
    }

    private Method getMainMethod(Class<?> clazz) {
        try {
            Method mainMethod = ReflectionUtils.getMethod(clazz, "main", String[].class);
            return isStaticMethod(mainMethod) ? mainMethod : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private boolean hasExecuteMethod(Class<?> clazz) {
        return getExecuteMethod(clazz) != null;
    }

    private Method getExecuteMethod(Class<?> clazz) {
        try {
            return ReflectionUtils.getMethod(clazz, "execute");
        } catch (Throwable t) {
            return null;
        }
    }

    private boolean isStaticMethod(Method method) {
        return (Modifier.STATIC & method.getModifiers()) != 0;
    }
}
