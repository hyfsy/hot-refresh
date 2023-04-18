package com.hyf.hotrefresh.core.util;

import com.hyf.hotrefresh.shadow.infrastructure.Infrastructure;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

@Infrastructure
public abstract class AsmUtils {

    public static MethodNode findMethod(ClassNode classNode, String methodName) {
        MethodNode lookupMethod = null;
        for (Object method : classNode.methods) {
            MethodNode methodNode = (MethodNode) method;
            if (methodName.equals(methodNode.name)) { // signature?
                lookupMethod = methodNode;
                break;
            }
        }
        return lookupMethod;
    }

    public static List<MethodNode> findMethods(ClassNode classNode, String methodName) {
        List<MethodNode> methodNodes = new ArrayList<>();
        for (Object method : classNode.methods) {
            MethodNode methodNode = (MethodNode) method;
            if (methodName.equals(methodNode.name)) {
                methodNodes.add(methodNode);
            }
        }
        return methodNodes;
    }

    public static void replaceMethod(ClassNode classNode, MethodNode methodNode) {
        for (int index = 0; index < classNode.methods.size(); ++index) {
            MethodNode tmp = (MethodNode) classNode.methods.get(index);
            if (tmp.name.equals(methodNode.name) && tmp.desc.equals(methodNode.desc)) {
                classNode.methods.set(index, methodNode);
            }
        }
    }

    public static void addInsnBeforeMethod(InsnList insnList, AbstractInsnNode... insnNodes) {
        int length = insnNodes.length;
        for (int i = length - 1; i >= 0; i--) {
            insnList.insertBefore(insnList.get(0), insnNodes[i]);
        }
    }
}
