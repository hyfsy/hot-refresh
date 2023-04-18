package com.hyf.hotrefresh.adapter.skywalking;

import com.hyf.hotrefresh.common.util.FastReflectionUtils;
import com.hyf.hotrefresh.core.util.AsmUtils;
import com.hyf.hotrefresh.core.util.InfraUtils;
import com.hyf.hotrefresh.core.util.Util;
import com.hyf.hotrefresh.shadow.infrastructure.Infrastructure;
import jdk.internal.org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class SkywalkingHelper {

    public static final String SKYWALKING_ENHANCED_INTERFACE             = "org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance";
    public static final String ENHANCED_INSTANCE_INTERFACE               = SKYWALKING_ENHANCED_INTERFACE.replace(".", "/");
    public static final String ENHANCED_INSTANCE_INTERFACE_METHOD_GETTER = "getSkyWalkingDynamicField";
    public static final String ENHANCED_INSTANCE_INTERFACE_METHOD_SETTER = "setSkyWalkingDynamicField";

    public static boolean skywalkingAgentPresent() {
        return FastReflectionUtils.exists("org.apache.skywalking.apm.agent.SkyWalkingAgent");
    }

    public static boolean agentCacheEnhancedClassEnabled() {
        try {
            Class<?> agentConfigClass = FastReflectionUtils.forName("org.apache.skywalking.apm.agent.core.conf.Config$Agent", Util.getOriginContextClassLoader());
            return FastReflectionUtils.fastGetField(agentConfigClass, "IS_CACHE_ENHANCED_CLASS");
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean enhancedBySkywalking(Class<?> classBeingRedefined) {
        // instance first, hit more quickly
        return instanceHasBeenEnhanced(classBeingRedefined) || classHasBeenEnhanced(classBeingRedefined);
    }

    /**
     * see AbstractClassEnhancePluginDefine#enhanceClass
     *
     * @param classBeingRedefined enhanced class
     * @return enhanced class return true, else false
     */
    public static boolean classHasBeenEnhanced(Class<?> classBeingRedefined) {
        // enhance static method, including xxxWithOverrideArgs、V2 ...
        return Arrays.stream(classBeingRedefined.getDeclaredFields()).map(f -> f.getType().getName())
                .anyMatch(n -> n.startsWith("org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance")
                        && n.substring(n.lastIndexOf(".") + 1).startsWith("StaticMethodsInter"));
    }

    /**
     * see AbstractClassEnhancePluginDefine#enhanceInstance
     *
     * @param classBeingRedefined enhanced class
     * @return enhanced instance return true, else false
     */
    public static boolean instanceHasBeenEnhanced(Class<?> classBeingRedefined) {
        // only when enhance instance constructor or method
        return Arrays.stream(classBeingRedefined.getInterfaces()).anyMatch(i -> SKYWALKING_ENHANCED_INTERFACE.equals(i.getName()));
    }

    /**
     * 无法通过 classBeingRedefined 获取字节码，只能拿到源代码的字节码，暂时没有解决方案
     */
    @Deprecated
    public static byte[] parseBytes(ClassLoader classLoader,
                                    String internalTypeName,
                                    Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain) throws IOException {
        return FastReflectionUtils.fastInvokeMethod(InfraUtils.forName(Helper.class.getName()), "parseBytes",
                new Class[]{ClassLoader.class, String.class, Class.class, ProtectionDomain.class},
                classLoader, internalTypeName, classBeingRedefined, protectionDomain);
    }

    public static byte[] amendClassBytes(Class<?> classBeingRedefined, byte[] classfileBuffer, byte[] memoryClassfileBuffer) {
        Class<?> clazz = InfraUtils.forName(Helper.class.getName());
        return FastReflectionUtils.fastInvokeMethod(clazz, "amendClassBytes", new Class[]{Class.class, byte[].class, byte[].class}, classBeingRedefined, classfileBuffer, memoryClassfileBuffer);
    }

    @Infrastructure
    private static class Helper {

        public static final String INITIALIZER_NAME        = "<init>";
        public static final String STATIC_INITIALIZER_NAME = "<clinit>";

        public static final String SYNTHETIC_IDENTITY = "$";

        // ByteBuddy related constants

        // ByteBuddy.BYTE_BUDDY_DEFAULT_SUFFIX
        // AuxiliaryType.NamingStrategy.SuffixingRandom
        public static final String IDENTITY_AUXILIARY            = "$auxiliary$";
        // Implementation.Context.Default.ACCESSOR_METHOD_SUFFIX
        // Implementation.Context.Default.AccessorMethod/FieldGetter/FieldSetter
        public static final String IDENTITY_ACCESSOR             = "$accessor$";
        // Implementation.Context.Default.FIELD_CACHE_PREFIX
        // Implementation.Context.Default.CacheValueField
        public static final String IDENTITY_CACHE_VALUE_IDENTITY = "cachedValue$";
        // MethodDelegation.ImplementationDelegate.FIELD_NAME_PREFIX
        // MethodDelegation.WithCustomProperties
        public static final String IDENTITY_DELEGATE             = "delegate$";
        // MethodNameTransformer.Suffixing.DEFAULT_SUFFIX
        public static final String IDENTITY_ORIGINAL             = "original";

        @Deprecated
        public static byte[] parseBytes(ClassLoader classLoader,
                                        String internalTypeName,
                                        Class<?> classBeingRedefined,
                                        ProtectionDomain protectionDomain) throws IOException {

            // String classResourcePath = classBeingRedefined.getName().replace(".", "/") + ".class";
            // byte[] binaryRepresentation;
            // try (InputStream is = Util.getOriginContextClassLoader().getResourceAsStream(classResourcePath)) {
            //     if (is == null) {
            //         throw new IllegalStateException("Class resource not found: " + classResourcePath);
            //     }
            //     ClassReader reader = new ClassReader(is);
            //     ClassNode classNode = new ClassNode();
            //     reader.accept(classNode, ClassReader.SKIP_FRAMES);
            //     ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            //     classNode.accept(writer);
            //     binaryRepresentation = writer.toByteArray();
            // }
            // Class<?> byteBuddyUtilsClass = FastReflectionUtils.forName(BYTE_BUDDY_UTILS_CLASS_NAME, SkywalkingByteBuddyShadeClassLoader.INSTANCE);
            // return FastReflectionUtils.fastInvokeMethod(byteBuddyUtilsClass, "parseClass",
            //         new Class[]{ClassLoader.class, String.class, Class.class, ProtectionDomain.class, byte[].class},
            //         classLoader, internalTypeName, classBeingRedefined, protectionDomain, binaryRepresentation);
            return null;
        }

        /**
         * 修正 classfileBuffer 原方法、辅助方法指令
         * <p>
         * TODO 实际方法被前面其他transform修改后的字节码怎么办？目前还没有出现这种场景
         *
         * @param classBeingRedefined   热刷新的类
         * @param classfileBuffer       skywalking处理后的字节码
         * @param memoryClassfileBuffer 内存字节码
         * @return 修正名称、方法指令后的字节码
         */
        public static byte[] amendClassBytes(Class<?> classBeingRedefined, byte[] classfileBuffer, byte[] memoryClassfileBuffer) {

            // 找到 classfileBuffer 内的所有字段、所有方法
            // 找到 memoryClassfileBuffer 内的所有字段、所有方法
            // 将 memoryClassfileBuffer 的类签名与 classfileBuffer 的对比，写入到 classfileBuffer 内
            // 将 memoryClassfileBuffer 的所有字段与 classfileBuffer 的对比，写入到 classfileBuffer 内
            // 将 memoryClassfileBuffer 的所有方法签名与 classfileBuffer 的对比，写入到 classfileBuffer 内
            // 将 memoryClassfileBuffer 的所有方法指令与 classfileBuffer 的对比，替换 classfileBuffer 内的
            // 返回 classfileBuffer

            // TODO 粗糙的先实现一版功能

            // try {
            //     ClassBytesDumper.dump(classfileBuffer, "E:\\" + classBeingRedefined.getName() + ".original.class");
            // } catch (IOException e) {
            //     e.printStackTrace();
            // }


            ClassReader reader = new ClassReader(classfileBuffer);
            ClassNode classNode = new ClassNode(Opcodes.ASM5);
            reader.accept(classNode, ClassReader.SKIP_FRAMES);

            ClassReader memoryReader = new ClassReader(memoryClassfileBuffer);
            ClassNode memoryClassNode = new ClassNode(Opcodes.ASM5);
            memoryReader.accept(memoryClassNode, ClassReader.SKIP_FRAMES);

            Map<String, FieldNode> fieldNodeMap = new HashMap<>();
            Map<String, Integer> fieldNodeIndexMap = new HashMap<>();

            for (int i = 0; i < classNode.fields.size(); i++) {
                FieldNode fieldNode = (FieldNode) classNode.fields.get(i);
                fieldNodeMap.put(fieldNode.name + fieldNode.desc, fieldNode);
                fieldNodeIndexMap.put(fieldNode.name + fieldNode.desc, i);
            }

            classNode.access = memoryClassNode.access;
            classNode.signature = memoryClassNode.signature;
            classNode.superName = memoryClassNode.superName;
            classNode.interfaces = memoryClassNode.interfaces;
            if (instanceHasBeenEnhanced(classBeingRedefined)) {
                classNode.interfaces.add(ENHANCED_INSTANCE_INTERFACE); // ws interface
            }
            classNode.sourceFile = memoryClassNode.sourceFile;
            classNode.sourceDebug = memoryClassNode.sourceDebug;
            classNode.outerClass = memoryClassNode.outerClass;
            classNode.outerMethod = memoryClassNode.outerMethod;
            classNode.outerMethodDesc = memoryClassNode.outerMethodDesc;
            classNode.visibleAnnotations = memoryClassNode.visibleAnnotations;
            classNode.visibleTypeAnnotations = memoryClassNode.visibleTypeAnnotations;
            classNode.invisibleAnnotations = memoryClassNode.invisibleAnnotations;
            classNode.invisibleTypeAnnotations = memoryClassNode.invisibleTypeAnnotations;
            // classNode.attrs = memoryClassNode.attrs; // 非标准属性，忽略
            classNode.innerClasses = memoryClassNode.innerClasses;

            // 字段更新，非人工合成、非_$EnhancedClassField_ws

            // TODO attribute?
            // 字段有顺序要求
            Iterator<FieldNode> it = memoryClassNode.fields.iterator();
            while (it.hasNext()) {
                FieldNode node = it.next();
                FieldNode fieldNode = fieldNodeMap.get(node.name + node.desc);
                if (fieldNode != null) {
                    Integer i = fieldNodeIndexMap.get(fieldNode.name + fieldNode.desc);
                    classNode.fields.remove(fieldNode);
                    classNode.fields.add(i, fieldNode);
                }
                else {
                    classNode.fields.add(node); // 不同的字段签名，新字段
                }
            }


            // 所有人工合成方法
            List<MethodNode> classMethods = classNode.methods;
            List<String> relatedSyntheticMethodSignatures = classMethods.stream()
                    .filter(m -> (m.access & Opcodes.ACC_SYNTHETIC) != 0 && (m.access & Opcodes.ACC_BRIDGE) == 0)
                    .filter(m -> m.name.contains(SYNTHETIC_IDENTITY))
                    .map(Helper::extractSyntheticIdentity)
                    .distinct()
                    .collect(Collectors.toList());
            List<String> classMethodSignatureList = classMethods.stream().map(m -> m.name + m.desc).collect(Collectors.toList());


            // 人工合成关联、构造函数、静态代码块

            List<MethodNode> memoryClassMethods = memoryClassNode.methods; // 修改指令的方法
            List<MethodNode> needAddMemoryMethodNodes = new ArrayList<>(memoryClassMethods); // 新增方法
            needAddMemoryMethodNodes.removeIf(m -> {
                // 人工合成关联
                return relatedSyntheticMethodSignatures.contains(generateIdentity(m))
                        || Arrays.asList(INITIALIZER_NAME, STATIC_INITIALIZER_NAME).contains(m.name);
            });
            // 不同的方法签名，新方法
            needAddMemoryMethodNodes.addAll(memoryClassMethods.stream()
                    .filter(m -> !classMethodSignatureList.contains(m.name + m.desc)).collect(Collectors.toList()));

            // 非人工合成、非桥接方法、非人工合成关联、非sw getter/setter、构造函数（需要判断）、静态代码块（需要剪切）

            classMethods.removeIf(m -> {
                // 非人工合成
                return (m.access & Opcodes.ACC_SYNTHETIC) == 0
                        // 非桥接方法
                        && (m.access & Opcodes.ACC_BRIDGE) == 0
                        // 非人工合成关联
                        && !relatedSyntheticMethodSignatures.contains(generateIdentity(m))
                        // 非sw getter/setter、构造函数、静态代码块
                        && !Arrays.asList(ENHANCED_INSTANCE_INTERFACE_METHOD_GETTER, ENHANCED_INSTANCE_INTERFACE_METHOD_SETTER, INITIALIZER_NAME, STATIC_INITIALIZER_NAME).contains(m.name);
            });

            classMethods.addAll(needAddMemoryMethodNodes);

            handleClinit(
                    AsmUtils.findMethod(classNode, STATIC_INITIALIZER_NAME),
                    AsmUtils.findMethod(memoryClassNode, STATIC_INITIALIZER_NAME));
            List<String> originMethodNames = memoryClassMethods.stream().map(Helper::generateIdentity).collect(Collectors.toList());
            handleOriginalConstructorAndMethodSignatures(classMethods.stream().filter(m -> originMethodNames.contains(generateIdentity(m))).collect(Collectors.toList()), memoryClassMethods);
            handleOriginalConstructors(
                    AsmUtils.findMethods(classNode, INITIALIZER_NAME),
                    AsmUtils.findMethods(memoryClassNode, INITIALIZER_NAME));
            handleSyntheticConstructors(
                    AsmUtils.findMethods(classNode, INITIALIZER_NAME).stream().filter(m -> (m.access & Opcodes.ACC_SYNTHETIC) != 0).collect(Collectors.toList()),
                    AsmUtils.findMethods(memoryClassNode, INITIALIZER_NAME));
            handleOriginalMethods(
                    classMethods.stream().filter(m -> !relatedSyntheticMethodSignatures.contains(generateIdentity(m))
                            && !Arrays.asList(ENHANCED_INSTANCE_INTERFACE_METHOD_GETTER, ENHANCED_INSTANCE_INTERFACE_METHOD_SETTER).contains(m.name)
                            && (m.access & Opcodes.ACC_SYNTHETIC) == 0 && (m.access & Opcodes.ACC_BRIDGE) == 0
                            && !Arrays.asList(INITIALIZER_NAME, STATIC_INITIALIZER_NAME).contains(m.name) // 静态代码块的内容由 handleClinit 处理，代码块的内容上面单独过滤处理
                    ).collect(Collectors.toList()),
                    memoryClassMethods.stream().filter(m -> !relatedSyntheticMethodSignatures.contains(generateIdentity(m))
                            && !Arrays.asList(ENHANCED_INSTANCE_INTERFACE_METHOD_GETTER, ENHANCED_INSTANCE_INTERFACE_METHOD_SETTER).contains(m.name)
                            && (m.access & Opcodes.ACC_BRIDGE) == 0
                            && !Arrays.asList(INITIALIZER_NAME, STATIC_INITIALIZER_NAME).contains(m.name)
                    ).collect(Collectors.toList()));
            handleSyntheticMethods(
                    classMethods.stream().filter(m -> (m.access & Opcodes.ACC_SYNTHETIC) != 0).filter(m -> !m.name.contains(IDENTITY_ACCESSOR)).filter(m -> !Arrays.asList(INITIALIZER_NAME, STATIC_INITIALIZER_NAME).contains(m.name)).collect(Collectors.toList()),
                    memoryClassMethods.stream().filter(m -> relatedSyntheticMethodSignatures.contains(generateIdentity(m))).collect(Collectors.toList()));


            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);

            byte[] bytes = writer.toByteArray();

            // try {
            //     ClassBytesDumper.dump(bytes, "E:\\" + classBeingRedefined.getName() + ".result.class");
            // } catch (IOException e) {
            //     e.printStackTrace();
            // }

            return bytes;
        }

        private static void handleClinit(MethodNode clinit, MethodNode memoryClinit) {

            if (memoryClinit == null || memoryClinit.instructions == null) {
                return;
            }

            // -   0: goto          16
            // |   3: new           #2
            // - ...
            // -  12: invokevirtual #5
            // |  16: invokestatic  #212
            // | 115: putstatic     #169
            // - 118: goto          3

            // 非法的字节码可能为null
            if (clinit == null) {
                return;
            }
            InsnList instructions = clinit.instructions;
            if (instructions == null) {
                clinit.instructions = memoryClinit.instructions;
                return;
            }

            if (!(instructions.getFirst() instanceof JumpInsnNode) || (!(instructions.getLast() instanceof JumpInsnNode))) {
                return;
            }

            JumpInsnNode first = (JumpInsnNode) instructions.getFirst();
            AbstractInsnNode insn = first.label;
            instructions.clear();
            while (insn != null) {
                instructions.add(insn);
                insn = insn.getNext();
            }
            JumpInsnNode last = (JumpInsnNode) instructions.getLast();
            instructions.remove(last);

            instructions.add(memoryClinit.instructions);

            // change body except instructions
            replaceMethodBody(clinit, memoryClinit, false);
        }

        private static void handleOriginalMethods(List<MethodNode> originalMethods, List<MethodNode> memoryOriginalMethods) {
            mapToPair(originalMethods, memoryOriginalMethods).forEach(p -> replaceMethodBody(p.main, p.node));
        }

        private static void handleOriginalConstructorAndMethodSignatures(List<MethodNode> originalMethods, List<MethodNode> memoryOriginalMethods) {
            mapToPair(originalMethods, memoryOriginalMethods).forEach(p -> replaceMethodSignature(p.main, p.node));
        }

        private static void handleOriginalConstructors(List<MethodNode> originConstructors, List<MethodNode> memoryConstructors) {
            List<String> syntheticRelatedDescList = new ArrayList<>();
            Iterator<MethodNode> iterator = originConstructors.iterator();
            while (iterator.hasNext()) {
                MethodNode originConstructor = iterator.next();
                if (originConstructor.desc.contains(IDENTITY_AUXILIARY)) {
                    String desc = removeAuxiliarySignatureParameter(originConstructor.desc);
                    syntheticRelatedDescList.add(desc);
                    iterator.remove();
                }
            }
            originConstructors.removeIf(c -> syntheticRelatedDescList.contains(c.desc));
            handleOriginalMethods(originConstructors, memoryConstructors);
        }

        private static void handleSyntheticConstructors(List<MethodNode> syntheticConstructors, List<MethodNode> memoryConstructors) {

            // origin desc -> synthetic method
            Map<String, MethodNode> syntheticMethodNameMap = new HashMap<>();

            // 标准化合成构造器描述
            for (MethodNode constructor : syntheticConstructors) {
                String desc = removeAuxiliarySignatureParameter(constructor.desc);
                syntheticMethodNameMap.put(desc, constructor);
            }

            mapToPair(new ArrayList<>(syntheticMethodNameMap.values()), memoryConstructors)
                    .forEach(p -> replaceMethodBody(p.main, p.node));
        }

        private static String removeAuxiliarySignatureParameter(String desc) {
            int checkpoint = 0; // (Lcom/hyf/hotrefresh/hello/controller/SkywalkingController$auxiliary$KXoeua0D;)V
            int cursor = 0;

            while (cursor < desc.length()) {
                int auxiliaryIdentityIdx = desc.indexOf(IDENTITY_AUXILIARY, checkpoint);
                if (auxiliaryIdentityIdx == -1) {
                    break;
                }

                int separatorIdx = 0;
                while (separatorIdx < auxiliaryIdentityIdx) {
                    separatorIdx = desc.indexOf(";", checkpoint + 1);
                    if (separatorIdx < auxiliaryIdentityIdx) {
                        checkpoint = separatorIdx;
                    }
                }

                String prev = desc.substring(0, checkpoint + 1);
                String post = desc.substring(separatorIdx + 1);
                desc = prev + post;
                cursor = checkpoint;
            }
            return desc;
        }

        private static void handleSyntheticMethods(List<MethodNode> syntheticMethods, List<MethodNode> memoryRelatedSyntheticMethods) {
            mapToPair(syntheticMethods, memoryRelatedSyntheticMethods,
                    (m, n) -> m.name.startsWith(n.name + SYNTHETIC_IDENTITY + IDENTITY_ORIGINAL) && m.desc.equals(n.desc))
                    .forEach(p -> replaceMethodBody(p.main, p.node));
        }

        private static void replaceMethodSignature(MethodNode needReplace, MethodNode template) {
            needReplace.access = template.access;
            needReplace.name = template.name;
            needReplace.desc = template.desc;
            needReplace.signature = template.signature;
            needReplace.exceptions = template.exceptions;
            needReplace.parameters = template.parameters;
            needReplace.annotationDefault = template.annotationDefault;
            needReplace.visibleParameterAnnotations = template.visibleParameterAnnotations;
            needReplace.invisibleParameterAnnotations = template.invisibleParameterAnnotations;
            needReplace.visibleAnnotations = template.visibleAnnotations;
            needReplace.invisibleAnnotations = template.invisibleAnnotations;
            needReplace.visibleTypeAnnotations = template.visibleTypeAnnotations;
            needReplace.invisibleTypeAnnotations = template.invisibleTypeAnnotations;
        }

        private static void replaceMethodBody(MethodNode needReplace, MethodNode template) {
            replaceMethodBody(needReplace, template, true);
        }

        private static void replaceMethodBody(MethodNode needReplace, MethodNode template, boolean replaceInstructions) {
            // needReplace.attrs = template.attrs;
            if (replaceInstructions) {
                needReplace.instructions = template.instructions;
            }
            needReplace.tryCatchBlocks = template.tryCatchBlocks;
            needReplace.maxStack = template.maxStack;
            needReplace.maxLocals = template.maxLocals;
            needReplace.localVariables = template.localVariables;
            needReplace.visibleLocalVariableAnnotations = template.visibleLocalVariableAnnotations;
            needReplace.invisibleLocalVariableAnnotations = template.invisibleLocalVariableAnnotations;
        }

        private static String extractSyntheticIdentity(MethodNode methodNode) {
            return methodNode.name.substring(0, methodNode.name.indexOf(SYNTHETIC_IDENTITY)) + "(" + generateParametersSignature(methodNode) + ")";
        }

        private static String generateIdentity(MethodNode methodNode) {
            return methodNode.name + "(" + generateParametersSignature(methodNode) + ")";
        }

        private static String generateParametersSignature(MethodNode methodNode) {
            StringBuilder sb = new StringBuilder();
            if (methodNode.parameters != null) {
                for (Object parameter : methodNode.parameters) {
                    ParameterNode parameterNode = (ParameterNode) parameter;
                    if (sb.length() != 0) {
                        sb.append(",");
                    }
                    sb.append(parameterNode.name);
                }
            }
            return sb.toString();
        }

        private static List<Pair> mapToPair(List<MethodNode> mains, List<MethodNode> nodes) {
            return mapToPair(mains, nodes, (m, n) -> m.name.equals(n.name) && m.desc.equals(n.desc));
        }

        private static List<Pair> mapToPair(List<MethodNode> mains, List<MethodNode> nodes, BiPredicate<MethodNode, MethodNode> predicate) {
            List<Pair> pairs = new ArrayList<>();
            for (MethodNode main : mains) {
                for (MethodNode node : nodes) {
                    if (predicate.test(main, node)) {
                        pairs.add(new Pair(main, node));
                        nodes.remove(node);
                        break;
                    }
                }
            }
            return pairs;
        }

        private static class Pair {
            private final MethodNode main;
            private final MethodNode node;

            public Pair(MethodNode main, MethodNode node) {
                this.main = main;
                this.node = node;
            }
        }
    }
}
