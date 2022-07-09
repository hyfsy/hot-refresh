package com.hyf.hotrefresh.adapter.spring.agent;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.core.util.AsmUtils;
import com.hyf.hotrefresh.core.util.TransformUtils;
import com.hyf.hotrefresh.shadow.infrastructure.Infrastructure;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.springframework.web.servlet.handler.MappedInterceptor;

@Infrastructure
public class MappedInterceptorEscape {

    public static void escapeMappedInterceptor() throws Exception {

        TransformUtils.signalTransformer(MappedInterceptor.class, classNode -> {
            MethodNode matchesMethodNode = AsmUtils.findMethod(classNode, "matches");

            if (matchesMethodNode == null) {
                Log.warn("Cannot find matches method in MappedInterceptor class");
                return;
            }

            LabelNode firstInsnNode = (LabelNode) matchesMethodNode.instructions.getFirst();

            VarInsnNode loadLookupPathParam = new VarInsnNode(Opcodes.ALOAD, 1);
            JumpInsnNode ifLoadLookupPathParamIsNull = new JumpInsnNode(Opcodes.IFNULL, firstInsnNode);
            VarInsnNode loadLookupPathParamSecond = new VarInsnNode(Opcodes.ALOAD, 1);
            LdcInsnNode loadHotRefreshStringParam = new LdcInsnNode(Constants.REFRESH_API);
            MethodInsnNode invokeEndsWithMethod = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "endsWith", "(Ljava/lang/String;)Z", false);
            JumpInsnNode ifConditionsEquals = new JumpInsnNode(Opcodes.IFEQ, firstInsnNode);
            InsnNode addFalseReturnValue = new InsnNode(Opcodes.ICONST_0);
            InsnNode returnTrueValue = new InsnNode(Opcodes.IRETURN);
            FrameNode frameNode = new FrameNode(Opcodes.F_SAME, 0, null, 0, null);

            AsmUtils.addInsnBeforeMethod(matchesMethodNode.instructions,
                    loadLookupPathParam,
                    ifLoadLookupPathParamIsNull,
                    loadLookupPathParamSecond,
                    loadHotRefreshStringParam,
                    invokeEndsWithMethod,
                    ifConditionsEquals,
                    addFalseReturnValue,
                    returnTrueValue,
                    frameNode
            );
        });
    }
}
