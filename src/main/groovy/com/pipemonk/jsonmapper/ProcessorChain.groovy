package com.pipemonk.jsonmapper

/**
 * Created by user-1 on 14/12/15.
 */
class ProcessorChain implements Processor {

    List<Processor> processorList = new ArrayList<>();

    void addProcessor(Processor processor) {
        processorList.add(processor);
    }

    void addProcessor(List<Processor> processors) {
        processorList.addAll(processors);
    }

    @Override
    void map(GroovyShell shell, boolean evaluateAtoB) {
        processorList.each {
            it.map(shell, evaluateAtoB);
        }
    }
}
