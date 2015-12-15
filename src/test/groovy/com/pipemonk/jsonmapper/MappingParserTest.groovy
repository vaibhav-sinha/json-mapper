package com.pipemonk.jsonmapper

import groovy.json.JsonSlurper
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by user-1 on 14/12/15.
 */
class MappingParserTest {

    @Test
    void testSimpleParsing() {

        JsonSlurper jsonSlurper = new JsonSlurper();

        String mapping = '''{
            "type" : "bi",
            "fieldA" : "product.name.fn",
            "fieldB" : "product.name.fn"
        }''';

        Map mappingMap = jsonSlurper.parseText(mapping) as Map;

        MappingParser mappingParser = new MappingParser();
        mappingParser.parse(mappingMap, null, null, 'object', null, null);
    }

    @Test
    void testSimpleNesting() {

        JsonSlurper jsonSlurper = new JsonSlurper();

        String mapping = '''{
            "mappings" : [
                {
                    "type" : "bi",
                    "fieldA" : "product.fn",
                    "fieldB" : "item.fn"
                }
            ]
        }''';

        Map mappingMap = jsonSlurper.parseText(mapping) as Map;

        MappingParser mappingParser = new MappingParser();
        mappingParser.parse(mappingMap, null, null, 'object', null, null);
    }

    @Test
    void testObjectNesting() {

        JsonSlurper jsonSlurper = new JsonSlurper();

        String mapping = '''{
            "mappings" : [
                {
                    "type" : "bi",
                    "fieldA" : "product",
                    "fieldB" : "item",
                    "fieldType" : "object",
                    "mappings" : [
                        {
                            "type" : "bi",
                            "fieldA" : "name",
                            "fieldB" : "fn",
                            "fieldType" : "string"
                        }
                    ]
                }
            ]
        }''';

        Map mappingMap = jsonSlurper.parseText(mapping) as Map;

        MappingParser mappingParser = new MappingParser();
        mappingParser.parse(mappingMap, null, null, 'object', null, null);
    }

    @Test
    void testArrayNesting() {

        JsonSlurper jsonSlurper = new JsonSlurper();

        String mapping = '''{
            "mappings" : [
                {
                    "type" : "bi",
                    "fieldA" : "product",
                    "fieldB" : "item",
                    "fieldType" : "array",
                    "mappings" : [
                        {
                            "type" : "bi",
                            "fieldA" : "name",
                            "fieldB" : "name.fullname",
                            "fieldType" : "string"
                        }
                    ]
                }
            ]
        }''';

        Map mappingMap = jsonSlurper.parseText(mapping) as Map;

        MappingParser mappingParser = new MappingParser();
        mappingParser.parse(mappingMap, null, null, 'object', null, null);
    }
}
