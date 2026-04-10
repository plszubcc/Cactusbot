package com.google.inject.internal.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.internal.asm.$AnnotationVisitor;
import com.google.inject.internal.asm.$ClassReader;
import com.google.inject.internal.asm.$ClassVisitor;
import com.google.inject.internal.asm.$FieldVisitor;
import com.google.inject.internal.asm.$Label;
import com.google.inject.internal.asm.$MethodVisitor;
import com.google.inject.internal.asm.$Type;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

final class LineNumbers {

    private static final Logger logger = Logger.getLogger(LineNumbers.class.getName());
    private static volatile boolean alreadyLoggedReadingFailure;
    private static final int ASM_API_LEVEL = 589824;
    private final Class<?> type;
    private final Map<String, Integer> lines = Maps.newHashMap();
    private String source;
    private int firstLine = 2147483647;

    public LineNumbers(Class<?> type) throws IOException {
        super();
        this.type = type;
        if (!type.isArray()) {
            InputStream in = null;

            try {
                in = type.getResourceAsStream("/" + type.getName().replace('.', '/') + ".class");
            } catch (IllegalStateException illegalstateexception) {
                ;
            }

            if (in != null) {
                try {
                    (new $ClassReader(in)).accept(new LineNumbers.LineNumberReader(), 4);
                } catch (Exception exception) {
                    if (!LineNumbers.alreadyLoggedReadingFailure) {
                        LineNumbers.alreadyLoggedReadingFailure = true;
                        LineNumbers.logger.log(Level.WARNING, "Failed loading line numbers. ASM is probably out of date. Further failures won't be logged.", exception);
                    }
                } finally {
                    try {
                        in.close();
                    } catch (IOException ioexception) {
                        ;
                    }

                }
            }
        }

    }

    public String getSource() {
        return this.source;
    }

    public Integer getLineNumber(Member member) {
        Preconditions.checkArgument(this.type == member.getDeclaringClass(), "Member %s belongs to %s, not %s", member, member.getDeclaringClass(), this.type);
        return (Integer) this.lines.get(this.memberKey(member));
    }

    public int getFirstLine() {
        return this.firstLine == 2147483647 ? 1 : this.firstLine;
    }

    private String memberKey(Member member) {
        Preconditions.checkNotNull(member, "member");
        if (member instanceof Field) {
            return member.getName();
        } else if (member instanceof Method) {
            return member.getName() + $Type.getMethodDescriptor((Method) member);
        } else if (!(member instanceof Constructor)) {
            throw new IllegalArgumentException("Unsupported implementation class for Member, " + member.getClass());
        } else {
            StringBuilder sb = (new StringBuilder()).append("<init>(");

            for (Class<?> param : ((Constructor) member).getParameterTypes()) {
                sb.append($Type.getDescriptor(param));
            }

            return sb.append(")V").toString();
        }
    }

    private class LineNumberReader extends $ClassVisitor {

        private int line = -1;
        private String pendingMethod;
        private String name;

        LineNumberReader() {
            super(589824);
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.name = name;
        }

        public $MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if ((access & 2) != 0) {
                return null;
            } else {
                this.pendingMethod = name + desc;
                this.line = -1;
                return new LineNumbers.LineNumberReader.LineNumberMethodVisitor();
            }
        }

        public void visitSource(String source, String debug) {
            LineNumbers.this.source = source;
        }

        public void visitLineNumber(int line, $Label start) {
            if (line < LineNumbers.this.firstLine) {
                LineNumbers.this.firstLine = line;
            }

            this.line = line;
            if (this.pendingMethod != null) {
                LineNumbers.this.lines.put(this.pendingMethod, line);
                this.pendingMethod = null;
            }

        }

        public $FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return null;
        }

        public $AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return new LineNumbers.LineNumberReader.LineNumberAnnotationVisitor();
        }

        public $AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
            return new LineNumbers.LineNumberReader.LineNumberAnnotationVisitor();
        }

        class LineNumberMethodVisitor extends $MethodVisitor {

            LineNumberMethodVisitor() {
                super(589824);
            }

            public $AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                return LineNumberReader.this.new LineNumberAnnotationVisitor();
            }

            public $AnnotationVisitor visitAnnotationDefault() {
                return LineNumberReader.this.new LineNumberAnnotationVisitor();
            }

            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                if (opcode == 181 && LineNumberReader.this.name.equals(owner) && !LineNumbers.this.lines.containsKey(name) && LineNumberReader.this.line != -1) {
                    LineNumbers.this.lines.put(name, LineNumberReader.this.line);
                }

            }

            public void visitLineNumber(int line, $Label start) {
                LineNumberReader.this.visitLineNumber(line, start);
            }
        }

        class LineNumberAnnotationVisitor extends $AnnotationVisitor {

            LineNumberAnnotationVisitor() {
                super(589824);
            }

            public $AnnotationVisitor visitAnnotation(String name, String desc) {
                return this;
            }

            public $AnnotationVisitor visitArray(String name) {
                return this;
            }

            public void visitLocalVariable(String name, String desc, String signature, $Label start, $Label end, int index) {}
        }
    }
}
