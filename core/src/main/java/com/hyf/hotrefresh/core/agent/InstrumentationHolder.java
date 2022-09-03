package com.hyf.hotrefresh.core.agent;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.core.util.InfraUtils;
import com.hyf.hotrefresh.core.util.Util;
import com.hyf.hotrefresh.shadow.infrastructure.Infrastructure;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * this class is used for get the InstrumentationImpl created when the jvm process starting, same as normal VM OPTIONS: -javaagent:xxx.jar
 *
 * @author baB_hyf
 * @date 2022/07/03
 * @see Instrumentation
 * @see InfraUtils#getSystemStartProcessInstrumentation
 */
@Infrastructure
public class InstrumentationHolder {

    private static final    AtomicBoolean        initialized                    = new AtomicBoolean(false);
    private static final    Set<Instrumentation> intermediateInstrumentationSet = new CopyOnWriteArraySet<>();
    private static volatile Instrumentation      systemStartProcessInstrumentation;

    static {
        // eager load to avoid not found exception
        JustForInvokeTransformer.class.getName();

        init();
    }

    private static void init() {
        if (initialized.compareAndSet(false, true)) {
            try {

                // enhance
                pilingInstrumentationSetStore();

                // set
                initInstrumentationSet();

                // get
                initInstrumentation();

            } catch (Throwable t) {
                throw new RuntimeException("Failed to init instrumentation", t);
            }
        }
    }

    private static void pilingInstrumentationSetStore() throws UnmodifiableClassException {
        Instrumentation instrumentation = InfraUtils.getInstrumentation();
        ReplaceRetransformClassesMethodTransformer transformer = new ReplaceRetransformClassesMethodTransformer();
        instrumentation.addTransformer(transformer, true);
        try {
            instrumentation.retransformClasses(instrumentation.getClass());
        } finally {
            instrumentation.removeTransformer(transformer);
        }
    }

    private static void initInstrumentationSet() throws UnmodifiableClassException {
        ClassFileTransformer justForInvokeTransformer = new JustForInvokeTransformer();
        Instrumentation instrumentation = InfraUtils.getInstrumentation();
        instrumentation.addTransformer(justForInvokeTransformer, true);
        ClassLoader originClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(Util.getInfrastructureJarClassLoader());
        try {
            instrumentation.retransformClasses(instrumentation.getClass());
        } finally {
            instrumentation.removeTransformer(justForInvokeTransformer);
            Thread.currentThread().setContextClassLoader(originClassLoader);
        }
    }

    private static void initInstrumentation() {
        if (!intermediateInstrumentationSet.isEmpty()) {
            systemStartProcessInstrumentation = intermediateInstrumentationSet.iterator().next(); // the first is oldest
        }
        else {
            throw new RuntimeException("Failed to get instrumentation from intermediateInstrumentationSet, use origin instrumentation");
            // instrumentation = Util.getInstrumentation(); // TODO enhance failed?
        }
    }

    // not for user invoke
    public static void addInstrumentation(Instrumentation instrumentation) {
        intermediateInstrumentationSet.add(instrumentation);
    }

    public static Instrumentation getSystemStartProcessInstrumentation() {
        return systemStartProcessInstrumentation;
    }

    private static class ReplaceRetransformClassesMethodTransformer implements ClassFileTransformer {

        public static final String INSTRUMENTATION_IMPL_NAME = "sun.instrument.InstrumentationImpl";
        public static final String CLASS_NAME                = InstrumentationHolder.class.getName();
        public static final String METHOD_NAME               = "addInstrumentation";

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

            // try {
            //     Method addInstrumentationMethod = Class.forName(InstrumentationHolder.class.getName(), false,
            //             Thread.currentThread().getContextClassLoader())
            //             .getMethod("addInstrumentation", Instrumentation.class);
            //     addInstrumentationMethod.setAccessible(true);
            //     addInstrumentationMethod.invoke(null, this);
            // } catch (Throwable t) {
            //     t.printStackTrace();
            // }


            if (!INSTRUMENTATION_IMPL_NAME.replace(".", "/").equals(className)) {
                return classfileBuffer;
            }

            try {
                ClassReader reader = new ClassReader(className.replace("/", "."));
                ClassNode classNode = new ClassNode(Opcodes.ASM5);
                reader.accept(classNode, ClassReader.SKIP_FRAMES);

                MethodNode transformMethodNode = getMethodNode(classNode, "transform");

                if (transformMethodNode == null) {
                    return classfileBuffer;
                }

                generateInstrumentationCollectInsn(transformMethodNode);

                replaceMethodNode(classNode, transformMethodNode);

                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                classNode.accept(writer);
                byte[] bytes = writer.toByteArray();
                // ClassBytesDumper.dump(bytes, "E:\\AAA.class");
                return bytes;
            } catch (Throwable t) {
                Log.error("Failed to replace instrumentation class", t);
                return classfileBuffer;
            }
        }

        private MethodNode getMethodNode(ClassNode classNode, String methodName) {
            MethodNode retransformClassesMethod = null;
            for (Object method : classNode.methods) {
                MethodNode methodNode = (MethodNode) method;
                if (methodName.equals(methodNode.name)) {
                    retransformClassesMethod = methodNode;
                    break;
                }
            }

            return retransformClassesMethod;
        }

        private void replaceMethodNode(ClassNode classNode, MethodNode methodNode) {
            for (int index = 0; index < classNode.methods.size(); ++index) {
                MethodNode tmp = (MethodNode) classNode.methods.get(index);
                if (tmp.name.equals(methodNode.name) && tmp.desc.equals(methodNode.desc)) {
                    classNode.methods.set(index, methodNode);
                }
            }
        }

        private void generateInstrumentationCollectInsn(MethodNode methodNode) {

            LabelNode originInvokeFirstLabel = (LabelNode) methodNode.instructions.getFirst();
            // AbstractInsnNode originInvokeLastLabel = methodNode.instructions.getLast();

            LabelNode l0 = new LabelNode();
            LabelNode l1 = new LabelNode();
            LabelNode l2 = new LabelNode();

            TryCatchBlockNode tryCatchBlockNode = new TryCatchBlockNode(l0, l1, l2, "java/lang/Throwable");
            methodNode.tryCatchBlocks.add(tryCatchBlockNode);

            // l0

            LdcInsnNode className = new LdcInsnNode(CLASS_NAME);
            InsnNode loadClassNameIdx = new InsnNode(Opcodes.ICONST_0);

            MethodInsnNode currentThread = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            MethodInsnNode getContextClassLoader = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Thread", "getContextClassLoader", "()Ljava/lang/ClassLoader;", false);
            MethodInsnNode forName = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", false);
            LdcInsnNode addInstrumentation = new LdcInsnNode(METHOD_NAME);

            InsnNode loadAddInstrumentationIdx = new InsnNode(Opcodes.ICONST_1);
            TypeInsnNode paramClassArrayType = new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Class");
            InsnNode loadParamArrayType = new InsnNode(Opcodes.DUP);
            InsnNode loadClassTypeIdx = new InsnNode(Opcodes.ICONST_0);
            LdcInsnNode InstrumentationClassType = new LdcInsnNode(Type.getType("Ljava/lang/instrument/Instrumentation;"));
            InsnNode storeInstrumentationClassType = new InsnNode(Opcodes.AASTORE);

            MethodInsnNode invokeGetMethod = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            VarInsnNode storeInvokeGetMethod = new VarInsnNode(Opcodes.ASTORE, 7);

            VarInsnNode loadAddInstrumentationMethod = new VarInsnNode(Opcodes.ALOAD, 7);
            InsnNode storeTrueParam = new InsnNode(Opcodes.ICONST_1);
            MethodInsnNode invokeSetAccessibleMethod = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Method", "setAccessible", "(Z)V", false);

            VarInsnNode loadAddInstrumentationMethodSecond = new VarInsnNode(Opcodes.ALOAD, 7);

            InsnNode objectParam = new InsnNode(Opcodes.ACONST_NULL);
            InsnNode storeObjectParamIdx = new InsnNode(Opcodes.ICONST_1);
            TypeInsnNode paramArray = new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object");
            InsnNode loadParamArray = new InsnNode(Opcodes.DUP);
            InsnNode storeParamArrayIdx = new InsnNode(Opcodes.ICONST_0);
            VarInsnNode getParamArray = new VarInsnNode(Opcodes.ALOAD, 0);
            InsnNode storeParamArray = new InsnNode(Opcodes.AASTORE);
            MethodInsnNode invokeAddInstrumentationMethod = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            InsnNode returnResult = new InsnNode(Opcodes.POP);

            // l1

            JumpInsnNode jumpInsnNode = new JumpInsnNode(Opcodes.GOTO, originInvokeFirstLabel);

            // l2

            FrameNode throwableFrame = new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
            VarInsnNode storeThrowable = new VarInsnNode(Opcodes.ASTORE, 7);
            VarInsnNode loadThrowable = new VarInsnNode(Opcodes.ALOAD, 7);
            MethodInsnNode invokePrintStackTrace = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Throwable", "printStackTrace", "()V", false);
            InsnNode throwException = new InsnNode(Opcodes.ATHROW);

            addInsn(methodNode.instructions,
                    l0,
                    className,
                    loadClassNameIdx,
                    currentThread,
                    getContextClassLoader,
                    forName,
                    addInstrumentation,
                    loadAddInstrumentationIdx,
                    paramClassArrayType,
                    loadParamArrayType,
                    loadClassTypeIdx,
                    InstrumentationClassType,
                    storeInstrumentationClassType,
                    invokeGetMethod,
                    storeInvokeGetMethod,
                    loadAddInstrumentationMethod,
                    storeTrueParam,
                    invokeSetAccessibleMethod,
                    loadAddInstrumentationMethodSecond,
                    objectParam,
                    storeObjectParamIdx,
                    paramArray,
                    loadParamArray,
                    storeParamArrayIdx,
                    getParamArray,
                    storeParamArray,
                    invokeAddInstrumentationMethod,
                    returnResult,
                    l1,
                    jumpInsnNode,
                    l2,
                    throwableFrame,
                    storeThrowable,
                    loadThrowable,
                    invokePrintStackTrace
                    // throwException
            );
        }

        private void addInsn(InsnList insnList, AbstractInsnNode... insnNodes) {
            int length = insnNodes.length;
            for (int i = length - 1; i >= 0; i--) {
                insnList.insertBefore(insnList.get(0), insnNodes[i]);
            }
        }
    }

    private static class JustForInvokeTransformer implements ClassFileTransformer {

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            return null; // trigger collect and reset enhanced instrumentation
        }
    }
}
