package com.hyf.hotrefresh.core.memory;

import javax.tools.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private static final String[] LOCATION_NAMES = {StandardLocation.PLATFORM_CLASS_PATH.name(), /* JPMS StandardLocation.SYSTEM_MODULES **/ "SYSTEM_MODULES"};

    private final MemoryByteCodeCollectClassLoader bcc;

    private final DependencyLookup dependencyLookup;

    public MemoryJavaFileManager(JavaFileManager fileManager, ClassLoader parent) {
        super(fileManager);
        this.bcc = new MemoryByteCodeCollectClassLoader(parent);
        this.dependencyLookup = new DependencyLookup(bcc);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {

        // deal our class file
        if (kind == JavaFileObject.Kind.CLASS) {
            MemoryByteCode memoryByteCode = bcc.get(className);
            if (memoryByteCode != null) {
                return memoryByteCode;
            }

            memoryByteCode = new MemoryByteCode(className);
            bcc.collect(memoryByteCode);
            return memoryByteCode;
        }

        // delegate JavacFileManager to get java file object for output
        return super.getJavaFileForOutput(location, className, kind, sibling);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        // annotation processor
        return AnnotationProcessorCompositeClassLoader.getInstance();
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof URIJavaFileObject) {
            return ((URIJavaFileObject) file).binaryName();
        }
        else if (file instanceof MemoryByteCode) {
            return ((MemoryByteCode) file).getClassName();
        }
        else {
            /*
             * if it's not custom JavaFileObject, then it's coming from standard file manager
             * - let it handle the file
             */
            return super.inferBinaryName(location, file);
        }
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        if (location instanceof StandardLocation) {
            String locationName = ((StandardLocation) location).name();
            for (String name : LOCATION_NAMES) {
                if (name.equals(locationName)) {
                    return super.list(location, packageName, kinds, recurse);
                }
            }
        }

        // merge JavaFileObjects from specified ClassLoader
        if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
            return new IterableJoin<>(dependencyLookup.find(packageName), super.list(location, packageName, kinds, recurse));
        }

        return super.list(location, packageName, kinds, recurse);
    }

    public Map<String, byte[]> getByteCodes() {
        return bcc.getCollectedByteCodes();
    }

    private static class IterableJoin<T> implements Iterable<T> {

        private final Iterable<T> first, next;

        public IterableJoin(Iterable<T> first, Iterable<T> next) {
            this.first = first;
            this.next = next;
        }

        @Override
        public Iterator<T> iterator() {
            return new IteratorJoin<>(first.iterator(), next.iterator());
        }
    }

    private static class IteratorJoin<T> implements Iterator<T> {

        private final Iterator<T> first, next;

        public IteratorJoin(Iterator<T> first, Iterator<T> next) {
            this.first = first;
            this.next = next;
        }

        @Override
        public boolean hasNext() {
            return first.hasNext() || next.hasNext();
        }

        @Override
        public T next() {
            if (first.hasNext()) {
                return first.next();
            }
            return next.next();
        }
    }
}
