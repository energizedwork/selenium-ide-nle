/*
 * Copyright 2010 Acuminous Ltd
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

package uk.co.acuminous.books.utils

import org.joda.time.DateTimeZone
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class BooksUtils {
    static DateTimeZone getTimeZone() {
        return DateTimeZone.forID('Europe/London') 
    }

    static String getDatePickerFormat() {
        ConfigurationHolder.config.datapicker.format.org.joda.time.LocalDate
    }

    static String getJodaFormat() {
        ConfigurationHolder.config.jodatime.format.org.joda.time.LocalDate        
    }
}
