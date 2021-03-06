package com.pipemonk.jsonmapper

/**
 * Created by user-1 on 14/12/15.
 */
class MappingParser {

    static List<Processor> parse(Map mapping, String prefixA, String prefixB, String fieldType, Map schemaA, Map schemaB) {
        List<Processor> processorList = new ArrayList<>();

        prefixA = prefixA == null ? 'A' : prefixA;
        prefixB = prefixB == null ? 'B' : prefixB;

        switch (fieldType) {
            case 'primitive' :
                processorList.add(parseForPrimitive(mapping, prefixA, prefixB));
                break;
            case 'object' :
                processorList.addAll(parseForObject(mapping, prefixA, prefixB, schemaA, schemaB));
                break;
            case 'array' :
                processorList.addAll(parseForArray(mapping, prefixA, prefixB, schemaA, schemaB));
                break;

        }

        return processorList;
    }

    private static Processor parseForPrimitive(Map mapping, String prefixA, String prefixB) {
        String rawFieldA = mapping.get('fieldA');
        String rawFieldB = mapping.get('fieldB');
        List<String> keysA = rawFieldA.split(/\./) as List<String>;
        List<String> keysB = rawFieldB.split(/\./) as List<String>;

        String scriptAtoB = null;
        String scriptBtoA = null;

        String expressionType = mapping.get("expressionType");
        String type = mapping.get('type');
        switch (type) {
            case 'bi' :
                scriptAtoB = getScriptForPrimitive(prefixA, prefixB, keysA, keysB, expressionType, rawFieldA);
                scriptBtoA = getScriptForPrimitive(prefixB, prefixA, keysB, keysA, expressionType, rawFieldB);
                break;
            case 'AtoB' :
                scriptAtoB = getScriptForPrimitive(prefixA, prefixB, keysA, keysB, expressionType, rawFieldA);
                break;
            case 'BtoA' :
                scriptBtoA = getScriptForPrimitive(prefixB, prefixA, keysB, keysA, expressionType, rawFieldB);
                break;
        }

        Processor processor = new SimpleProcessor(scriptAtoB, scriptBtoA);
        return processor;
    }

    private static List<Processor> parseForObject(Map mapping, String prefixA, String prefixB, Map schemaA, Map schemaB) {
        String rawFieldA = mapping.get('fieldA');
        String rawFieldB = mapping.get('fieldB');
        List<String> keysA = rawFieldA == null ? [] : rawFieldA.split(/\./) as List<String>;
        List<String> keysB = rawFieldB == null ? [] : rawFieldB.split(/\./) as List<String>;

        List<Processor> processorList = new ArrayList<>();

        String scriptAtoB = null;
        String scriptBtoA = null;

        String type = mapping.get('type');
        switch (type) {
            case 'AtoB' :
                scriptAtoB = getScriptForObject(prefixA, prefixB, keysA, keysB);
                break;
            case 'BtoA' :
                scriptBtoA = getScriptForObject(prefixB, prefixA, keysB, keysA);
                break;
            default :
                scriptAtoB = getScriptForObject(prefixA, prefixB, keysA, keysB);
                scriptBtoA = getScriptForObject(prefixB, prefixA, keysB, keysA);
                break;
        }

        Processor processor = new SimpleProcessor(scriptAtoB, scriptBtoA);
        processorList.add(processor);
        //Modify the prefixA and prefixB with the key of the current object
        keysA.each {
            prefixA = prefixA + "[\"$it\"]";
        }
        keysB.each {
            prefixB = prefixB + "[\"$it\"]";
        }

        //Run a loop to process all mappings within the current object
        List<Map> mappingList = mapping.get('mappings') as List<Map>;
        mappingList.each {
            //Find out the type of source node from the schema for the current mapping
            String childType = it['fieldType'];
            switch (childType) {
                case 'object' :
                    processorList.addAll(parse(it, prefixA, prefixB, 'object', schemaA, schemaB));
                    break;
                case 'array' :
                    processorList.addAll(parse(it, prefixA, prefixB, 'array', schemaA, schemaB));
                    break;
                default:
                    processorList.addAll(parse(it, prefixA, prefixB, 'primitive', schemaA, schemaB));
            }
        }

        return processorList;
    }

    private static List<Processor> parseForArray(Map mapping, String prefixA, String prefixB, Map schemaA, Map schemaB) {
        String rawFieldA = mapping.get('fieldA');
        String rawFieldB = mapping.get('fieldB');
        List<String> keysA = rawFieldA == null ? [] : rawFieldA.split(/\./) as List<String>;
        List<String> keysB = rawFieldB == null ? [] : rawFieldB.split(/\./) as List<String>;

        List<Processor> processorList = new ArrayList<>();

        String scriptAtoB = null;
        String scriptBtoA = null;

        String type = mapping.get('type');
        switch (type) {
            case 'AtoB' :
                scriptAtoB = getScriptForArray(prefixA, prefixB, keysA, keysB);
                break;
            case 'BtoA' :
                scriptBtoA = getScriptForArray(prefixB, prefixA, keysB, keysA);
                break;
            default :
                scriptAtoB = getScriptForArray(prefixA, prefixB, keysA, keysB);
                scriptBtoA = getScriptForArray(prefixB, prefixA, keysB, keysA);
                break;
        }

        Processor processor = new SimpleProcessor(scriptAtoB, scriptBtoA);
        processorList.add(processor);
        //Modify the prefixA and prefixB with the key of the current object
        keysA.each {
            prefixA = prefixA + "[\"$it\"][loopIndex]";
        }
        keysB.each {
            prefixB = prefixB + "[\"$it\"][loopIndex]";
        }

        //Run a loop to process all mappings within the current object
        List<Map> mappingList = mapping.get('mappings') as List<Map>;
        mappingList.each {
            //Find out the type of source node from the schema for the current mapping
            String childType = '';
            switch (childType) {
                case 'object' :
                    processorList.addAll(parse(it, prefixA, prefixB, 'object', schemaA, schemaB));
                    break;
                case 'array' :
                    processorList.addAll(parse(it, prefixA, prefixB, 'array', schemaA, schemaB));
                    break;
                default:
                    processorList.addAll(parse(it, prefixA, prefixB, 'primitive', schemaA, schemaB));
            }
        }

        return processorList;
    }

    private static String getScriptForPrimitive(String sourcePrefix, String destinationPrefix, List<String> sourceKeys, List<String> destinationKeys, String expressionType, String rawSourceField) {
        boolean loop = sourcePrefix.endsWith('[loopIndex]');
        String script = "";
        String fieldToModify = destinationPrefix;
        String lastDestinationKey = destinationKeys[destinationKeys.size() - 1];

        if(loop) {
            String arrayA = sourcePrefix[0..-12];
            String arrayB = destinationPrefix[0..-12];
            script = script + "for(int loopIndex = 0; loopIndex < ${arrayA}.size(); loopIndex++) {\n";
            script = script + "if(${fieldToModify} == null)\n" +
                    "{\n" +
                    "${arrayB} << [:]\n" +
                    "}\n";
        }

        destinationKeys.take(destinationKeys.size() - 1).each({
            fieldToModify = fieldToModify + "[\"$it\"]";
            script = script + "if($fieldToModify == null)\n" +
                    "{\n" +
                    "$fieldToModify = [:]\n" +
                    "}\n";
        });

        String fieldToGet = sourcePrefix;
        sourceKeys.each({
            fieldToGet = fieldToGet + "[\"$it\"]";
        });

        if(expressionType == 'expression') {
            script = script + "def value = $rawSourceField \n";
            script = script + "$fieldToModify['$lastDestinationKey'] = value";
        }
        else {
            script = script + "$fieldToModify['$lastDestinationKey'] = $fieldToGet";
        }
        if(loop) {
            script = script + "\n}";
        }
        return script;
    }

    private static String getScriptForObject(String sourcePrefix, String destinationPrefix, List<String> sourceKeys, List<String> destinationKeys) {
        String fieldToModify = destinationPrefix;
        destinationKeys.each({
            fieldToModify = fieldToModify + "[\"$it\"]";
        });

        return "$fieldToModify = [:];";
    }

    private static String getScriptForArray(String sourcePrefix, String destinationPrefix, List<String> sourceKeys, List<String> destinationKeys) {
        String fieldToModify = destinationPrefix;
        destinationKeys.each({
            fieldToModify = fieldToModify + "[\"$it\"]";
        });

        return "$fieldToModify = [];";
    }
}
