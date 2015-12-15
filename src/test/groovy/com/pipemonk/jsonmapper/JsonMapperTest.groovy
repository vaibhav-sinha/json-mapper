package com.pipemonk.jsonmapper

import org.junit.Test

/**
 * Created by user-1 on 15/12/15.
 */
class JsonMapperTest {

    @Test
    void testJsonMapper() {
        JsonMapper jsonMapper = new JsonMapperImpl();

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
                },
                {
                    "type" : "bi",
                    "fieldA" : "name",
                    "fieldB" : "sku",
                    "fieldType" : "string"
                }
            ]
        }''';

        String fromSchema = "{}";
        String toSchema = "{}";

        String object = '''{
            "name" : "order1",
            "product" : [
                {
                    "name" : "prod1"
                },
                {
                    "name" : "prod2"
                },
                {
                    "name" : "prod3"
                }
            ]
        }'''

        String mapped = jsonMapper.map(mapping, fromSchema, toSchema, object);
        println(mapped);
    }

    @Test
    void testJsonMapperExpression() {
        JsonMapper jsonMapper = new JsonMapperImpl();

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
                },
                {
                    "type" : "AtoB",
                    "fieldA" : "A.name.toUpperCase()",
                    "fieldB" : "sku",
                    "fieldType" : "string",
                    "expressionType" : "expression"
                }
            ]
        }''';

        String fromSchema = "{}";
        String toSchema = "{}";

        String object = '''{
            "name" : "order1",
            "product" : [
                {
                    "name" : "prod1"
                },
                {
                    "name" : "prod2"
                },
                {
                    "name" : "prod3"
                }
            ]
        }'''

        String mapped = jsonMapper.map(mapping, fromSchema, toSchema, object);
        println(mapped);
    }

}
