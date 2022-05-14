package com.hyf.hotrefresh.memory;

import com.hyf.hotrefresh.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
class MemoryByteCodeCollectClassLoader extends ClassLoader {

    static {
        registerAsParallelCapable();
    }

    /** className -> classBytes */
    private final Map<String, MemoryByteCode> collectedByteCode = new HashMap<>();

    MemoryByteCodeCollectClassLoader() {
        super(Util.getThrowawayMemoryClassLoader());
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        MemoryByteCode memoryByteCode = collectedByteCode.get(name);

        if (memoryByteCode != null) {
            return defineClass(name, memoryByteCode.getByteCode(), 0, memoryByteCode.getByteCode().length);
        }

        return super.findClass(name);
    }

    public MemoryByteCode get(String className) {
        return collectedByteCode.get(className);
    }

    public MemoryByteCode collect(MemoryByteCode memoryByteCode) {
        return collectedByteCode.put(memoryByteCode.getClassName(), memoryByteCode);
    }

    public Map<String, byte[]> getCollectedByteCodes() {
        Map<String, byte[]> bytesMap = new HashMap<>();
        for (Map.Entry<String, MemoryByteCode> entry : collectedByteCode.entrySet()) {
            String key = entry.getKey();
            MemoryByteCode value = entry.getValue();
            bytesMap.put(key, value.getByteCode());
        }
        return bytesMap;
    }
}
