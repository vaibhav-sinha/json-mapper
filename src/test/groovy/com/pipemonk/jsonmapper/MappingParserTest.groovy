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
        mappingParser.parse(null, null, mappingMap);
    }
}
