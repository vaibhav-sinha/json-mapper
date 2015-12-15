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
            "id" : {
                "A_schema_id" : "shopify/order/v1",
                "B_schema_id" : "qbo/order/v2"
            },
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

        String fromSchema = '''{
            "id" : "shopify/order/v1"
        }''';
        String toSchema = '''{
            "id" : "qbo/order/v2"
        }''';

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
        }''';

        String object2 = '''{
            "sku" : "order1",
            "item" : [
                {
                    "name" : {
                        "fullname" : "prod1"
                    }
                },
                {
                    "name" : {
                        "fullname" : "prod2"
                    }
                }
            ]
        }''';

        String mapped = jsonMapper.map(mapping, fromSchema, toSchema, object);
        String mapped2 = jsonMapper.map(mapping, toSchema, fromSchema, object2);
        println(mapped);
        println(mapped2);
    }

}
