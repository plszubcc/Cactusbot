package com.google.inject.internal.aop;

import com.google.inject.internal.asm.$MethodVisitor;
import com.google.inject.internal.asm.$Type;

final class BytecodeTasks {

    private BytecodeTasks() {
        super();
    }

    public static void pushInteger($MethodVisitor mv, int value) {
        if (value < -1) {
            mv.visitLdcInsn(value);
        } else if (value <= 5) {
            mv.visitInsn(3 + value);
        } else if (value <= 127) {
            mv.visitIntInsn(16, value);
        } else if (value <= 32767) {
            mv.visitIntInsn(17, value);
        } else {
            mv.visitLdcInsn(value);
        }

    }

    public static void packArguments($MethodVisitor mv, Class<?>[] parameterTypes) {
        pushInteger(mv, parameterTypes.length);
        mv.visitTypeInsn(189, "java/lang/Object");
        int parameterIndex = 0;
        int slot = 1;

        for (Class<?> parameterType : parameterTypes) {
            mv.visitInsn(89);
            pushInteger(mv, parameterIndex++);
            slot += loadArgument(mv, parameterType, slot);
            if (parameterType.isPrimitive()) {
                box(mv, $Type.getType(parameterType));
            }

            mv.visitInsn(83);
        }

    }

    public static void unpackArguments($MethodVisitor mv, Class<?>[] parameterTypes) {
        int parameterIndex = 0;

        for (Class<?> parameterType : parameterTypes) {
            mv.visitVarInsn(25, 2);
            pushInteger(mv, parameterIndex++);
            mv.visitInsn(50);
            if (parameterType.isPrimitive()) {
                unbox(mv, $Type.getType(parameterType));
            } else {
                mv.visitTypeInsn(192, $Type.getInternalName(parameterType));
            }
        }

    }

    public static int loadArgument($MethodVisitor mv, Class<?> parameterType, int slot) {
        if (!parameterType.isPrimitive()) {
            mv.visitVarInsn(25, slot);
            return 1;
        } else {
            $Type primitiveType = $Type.getType(parameterType);

            mv.visitVarInsn(primitiveType.getOpcode(21), slot);
            return primitiveType.getSize();
        }
    }

    public static void box($MethodVisitor mv, $Type primitiveType) {
        String wrapper;
        String descriptor;

        switch (primitiveType.getSort()) {
            case 1:
                wrapper = "java/lang/Boolean";
                descriptor = "(Z)Ljava/lang/Boolean;";
                break;
            case 2:
                wrapper = "java/lang/Character";
                descriptor = "(C)Ljava/lang/Character;";
                break;
            case 3:
                wrapper = "java/lang/Byte";
                descriptor = "(B)Ljava/lang/Byte;";
                break;
            case 4:
                wrapper = "java/lang/Short";
                descriptor = "(S)Ljava/lang/Short;";
                break;
            case 5:
                wrapper = "java/lang/Integer";
                descriptor = "(I)Ljava/lang/Integer;";
                break;
            case 6:
                wrapper = "java/lang/Float";
                descriptor = "(F)Ljava/lang/Float;";
                break;
            case 7:
                wrapper = "java/lang/Long";
                descriptor = "(J)Ljava/lang/Long;";
                break;
            case 8:
                wrapper = "java/lang/Double";
                descriptor = "(D)Ljava/lang/Double;";
                break;
            default:
                return;
        }

        mv.visitMethodInsn(184, wrapper, "valueOf", descriptor, false);
    }

    public static void unbox($MethodVisitor mv, $Type primitiveType) {
        String wrapper;
        String method;
        String descriptor;

        switch (primitiveType.getSort()) {
            case 1:
                wrapper = "java/lang/Boolean";
                method = "booleanValue";
                descriptor = "()Z";
                break;
            case 2:
                wrapper = "java/lang/Character";
                method = "charValue";
                descriptor = "()C";
                break;
            case 3:
                wrapper = "java/lang/Byte";
                method = "byteValue";
                descriptor = "()B";
                break;
            case 4:
                wrapper = "java/lang/Short";
                method = "shortValue";
                descriptor = "()S";
                break;
            case 5:
                wrapper = "java/lang/Integer";
                method = "intValue";
                descriptor = "()I";
                break;
            case 6:
                wrapper = "java/lang/Float";
                method = "floatValue";
                descriptor = "()F";
                break;
            case 7:
                wrapper = "java/lang/Long";
                method = "longValue";
                descriptor = "()J";
                break;
            case 8:
                wrapper = "java/lang/Double";
                method = "doubleValue";
                descriptor = "()D";
                break;
            default:
                return;
        }

        mv.visitTypeInsn(192, wrapper);
        mv.visitMethodInsn(182, wrapper, method, descriptor, false);
    }
}
