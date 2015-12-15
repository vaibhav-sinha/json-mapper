package com.pipemonk.jsonmapper

/**
 * Created by user-1 on 14/12/15.
 */
interface JsonMapper {

    String map(String mapping, String fromSchema, String toSchema, String object);
    Map map(Map mapping, Map fromSchema, Map toSchema, Map object);

}