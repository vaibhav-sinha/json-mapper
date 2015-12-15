package com.pipemonk.jsonmapper

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

/**
 * Created by user-1 on 15/12/15.
 */
class JsonMapperImpl implements JsonMapper {

    JsonSlurper jsonSlurper = new JsonSlurper();

    @Override
    String map(String mapping, String fromSchema, String toSchema, String object) {
        Map fromSchemaMap = jsonSlurper.parseText(fromSchema) as Map;
        Map toSchemaMap = jsonSlurper.parseText(toSchema) as Map;
        Map objectMap = jsonSlurper.parseText(object) as Map;
        Map mappingMap = jsonSlurper.parseText(mapping) as Map;
        Map mapped = map(mappingMap, fromSchemaMap, toSchemaMap, objectMap);
        return JsonOutput.toJson(mapped);
    }

    @Override
    Map map(Map mapping, Map fromSchema, Map toSchema, Map object) {
        ProcessorChain processorChain = new ProcessorChain();
        processorChain.addProcessor(MappingParser.parse(mapping, null, null, "object", fromSchema, toSchema));
        Map context = ['A' : object];
        Binding binding = new Binding(context);
        GroovyShell groovyShell = new GroovyShell(binding);
        processorChain.map(groovyShell, true);
        return context['B'];
    }
}
