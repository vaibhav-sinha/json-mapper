package com.pipemonk.jsonmapper

/**
 * Created by user-1 on 14/12/15.
 */
class SimpleProcessor implements Processor {

    String expressionAtoB;
    String expressionBtoA;

    SimpleProcessor(String expressionAtoB, String expressionBtoA) {
        this.expressionAtoB = expressionAtoB
        this.expressionBtoA = expressionBtoA
    }

    @Override
    void map(GroovyShell shell, boolean evaluateAtoB) {
        if(evaluateAtoB && expressionAtoB != null) {
            shell.evaluate(expressionAtoB);
        }
        if(!evaluateAtoB && expressionBtoA != null) {
            shell.evaluate(expressionBtoA);
        }
    }
}
