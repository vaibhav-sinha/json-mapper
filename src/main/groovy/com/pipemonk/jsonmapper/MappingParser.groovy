package com.pipemonk.jsonmapper

/**
 * Created by user-1 on 14/12/15.
 */
class MappingParser {

    Processor parse(Map schemaA, Map schemaB, Map mapping) {
        String type = mapping.get('type');
        switch (type) {
            case 'bi' :
                String rawFieldA = mapping.get('fieldA');
                String rawFieldB = mapping.get('fieldB');
                List<String> keysA = rawFieldA.split(/\./) as List<String>;
                List<String> keysB = rawFieldB.split(/\./) as List<String>;

                //Create script for A to B
                String scriptAtoB = '';
                String fieldToModifyB = 'B';
                keysB[0..keysB.size() - 2].each({
                    fieldToModifyB = fieldToModifyB + '["' + it + '"]';
                    scriptAtoB = scriptAtoB + "if ($fieldToModifyB == null) {\n" +
                            "$fieldToModifyB = new LinkedHashMap();\n" +
                            "}\n";
                });

                String fieldToGetA = 'A';
                keysA[0..keysA.size() - 1].each({
                    fieldToGetA = fieldToGetA + '["' + it + '"]';
                });

                scriptAtoB = scriptAtoB + fieldToModifyB + "[\"" + keysB[keysB.size() -1] + "\"]" + " = $fieldToGetA";

                String expressionAtoB = 'B[' + mapping.get('fieldB') + ']' + ' = ' + 'A[' + mapping.get('fieldA') + ']';
        }

        return null;
    }
}
