/*
 * Copyright 2010 Stephen Mark Cresswell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.acuminous.books.jbehave.converter

import org.jbehave.core.steps.ParameterConverters.ParameterConverter
import java.lang.reflect.Type
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import uk.co.acuminous.books.utils.BooksUtils
import org.joda.time.DateTime

class JodaConverter implements ParameterConverter {

    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern('dd-MMM-yy')
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern('dd-MMM-yy HH:mm:ss')

    boolean accept(Type type) {
        return [DateTime, LocalDate].contains(type)
    }

    Object convertValue(String value, Type type) {
        switch(type) {
            case LocalDate:
                return DATE_FORMATTER.parseDateTime(value).withZone(BooksUtils.timeZone).toLocalDate()
            case DateTime:
                DATE_TIME_FORMATTER.parseDateTime(value).withZone(BooksUtils.timeZone)
        }
    }
}
