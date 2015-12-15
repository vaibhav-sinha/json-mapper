package com.pipemonk.jsonmapper

/**
 * Created by user-1 on 15/12/15.
 */
class CacheKey {
    Map mapping;
    Map fromSchema;
    Map toSchema;

    CacheKey(Map mapping, Map fromSchema, Map toSchema) {
        this.mapping = mapping
        this.fromSchema = fromSchema
        this.toSchema = toSchema
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        CacheKey cacheKey = (CacheKey) o

        return cacheKey.mapping['id'] == this.mapping['id'];
    }

    int hashCode() {
        int result
        result = mapping.hashCode()
        result = 31 * result + (fromSchema != null ? fromSchema.hashCode() : 0)
        result = 31 * result + (toSchema != null ? toSchema.hashCode() : 0)
        return result
    }
}
