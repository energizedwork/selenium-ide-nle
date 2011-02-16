package uk.co.acuminous.books.jbehave.converter

import java.lang.reflect.Type
import org.jbehave.core.steps.ParameterConverters.ParameterConverter
import uk.co.acuminous.books.VatRate

class BigDecimalConverter implements ParameterConverter {

    boolean accept(Type type) {
        return [BigDecimal].contains(type)
    }

    Object convertValue(String value, Type type) {
        return new BigDecimal(value.replaceAll('[^0-9\\.]', ''))
    }
}
