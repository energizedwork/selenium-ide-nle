package uk.co.acuminous.test

import java.lang.reflect.Field
import org.apache.commons.lang.RandomStringUtils

abstract class EntityBuilder {

    def entity    
    Map fields = [:]
    Map factories = [:]

    EntityBuilder() {
        setFactory String, { def entity -> RandomStringUtils.randomAlphanumeric(10) }       
    }

    void setFactory(def key, Closure factory) {
        factories[key] = factory
    }    

    abstract Object build()
    Object buildAndSave() {
        build()
        save()
        return entity        
    }

    Collection getAutoAssignedFieldNames() {
        []
    }

    void save(def entity = this.entity) {
        assert entity.save(), entity.errors
    }

    boolean isSpecified(fieldName) {
        return fields.containsKey(fieldName)        
    }

    void setDefaultValue(String fieldName, def value) {
        if (!isSpecified(fieldName)) {
            fields[fieldName] = value
        }
    }

    void assignValues() {
        assignSpecifiedValues()
        assignGeneratedValues()
    }

    void assignSpecifiedValues() {
        fields.each { String fieldName, def value ->
            entity."$fieldName" = value
        }
    }

    void assignGeneratedValues() {
        autoAssignedFieldNames.each { String fieldName ->
            if (!isSpecified(fieldName)) {
                entity."${fieldName}" = generateValue(fieldName)
            }
        }        
    }

    def generateValue(String fieldName) {
        Closure factory = getCustomDataFactory(entity, fieldName) ?: getEnumDataFactory(fieldName) ?: getClassDataFactory(fieldName)
        assert factory, "No custom or class data factory for '${entity.class.simpleName}.${fieldName}'"
        return factory(entity)
    }

    Closure getCustomDataFactory(entity, fieldName) {
        String key = "${entity.class.simpleName}.${fieldName}"
        return factories[key]
    }

    Closure getClassDataFactory(fieldName) {
        Field field = entity.class.declaredFields.find { Field f -> f.name == fieldName }
        assert field, "Cannot find $fieldName on ${entity.class}"
        return factories[field.type]
    }

    Closure getEnumDataFactory(fieldName) {        
        Field field = entity.class.declaredFields.find { Field f -> f.name == fieldName }
        assert field, "Cannot find $fieldName on ${entity.class}"        
        if (field.type.isEnum()) {
            return {
                oneOf(field.type.values())
            }
        }
    }

    Object oneOf(def values) {
        Integer size = values.length
        Integer index = Math.random() * size
        return values[index]
    }

    String randomWord(Integer size = randomInt(10)) {
        RandomStringUtils.randomAlphabetic(size)
    }

    String randomLowerCaseWord(Integer size = randomInt(10)) {
        randomWord(size).toLowerCase()
    }

    Integer randomInt(Integer max = 100) {
        randomInt(1, max)
    }

    Integer randomInt(Integer min, Integer max) {
        new Random().nextInt((max + 1) - min) + min
    }

    def propertyMissing(String name) {
        return fields[name]
    }

    def methodMissing(String name, args) {
        fields[name] = args[0]
        return this
    }
}

