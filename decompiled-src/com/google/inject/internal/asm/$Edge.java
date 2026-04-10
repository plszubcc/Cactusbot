package com.google.inject.internal.asm;

final class $Edge {

    static final int JUMP = 0;
    static final int EXCEPTION = 2147483647;
    final int info;
    final $Label successor;
    $Edge nextEdge;

    $Edge(int info, $Label successor, $Edge nextEdge) {
        super();
        this.info = info;
        this.successor = successor;
        this.nextEdge = nextEdge;
    }
}
