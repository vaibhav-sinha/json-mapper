package com.pipemonk.jsonmapper

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import javax.management.BadAttributeValueExpException

/**
 * Created by user-1 on 15/12/15.
 */
class JsonMapperImpl implements JsonMapper {

    JsonSlurper jsonSlurper = new JsonSlurper();
    private LoadingCache<CacheKey, ProcessorChain> cache;

    JsonMapperImpl() {
        final CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder().maximumSize(1000);
        cache = cacheBuilder.build(new CacheLoader<CacheKey, ProcessorChain>() {
            @Override
            public ProcessorChain load(final CacheKey key) {
                ProcessorChain processorChain = new ProcessorChain();
                processorChain.addProcessor(MappingParser.parse(key.mapping, null, null, "object", key.fromSchema, key.toSchema));
                return processorChain;
            }
        });
    }

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
        String fromSchemaMappingId = mapping['id']['A_schema_id'];
        String toSchemaMappingId = mapping['id']['B_schema_id'];
        String fromSchemaId = fromSchema['id'];
        String toSchemaId = toSchema['id'];

        Boolean fromAtoB;
        if(fromSchemaMappingId == fromSchemaId && toSchemaMappingId == toSchemaId) {
            fromAtoB = true;
        }
        else if(fromSchemaMappingId == toSchemaId && toSchemaMappingId == fromSchemaId) {
            fromAtoB = false;
        }
        else {
            throw new BadAttributeValueExpException("The id in mapping does not match those in the schemas");
        }

        ProcessorChain processorChain;

        if(fromAtoB) {
            processorChain = cache.get(new CacheKey(mapping, fromSchema, toSchema));
        }
        else {
            processorChain = cache.get(new CacheKey(mapping, toSchema, fromSchema));
        }

        String keyToPut;
        String keyToGet;
        if(fromAtoB) {
            keyToPut = 'A';
            keyToGet = 'B';
        }
        else {
            keyToPut = 'B';
            keyToGet = 'A';
        }
        Map context = [:];
        context[keyToPut] = object;

        Binding binding = new Binding(context);
        GroovyShell groovyShell = new GroovyShell(binding);
        processorChain.map(groovyShell, fromAtoB);
        return context[keyToGet] as Map;
    }
}
