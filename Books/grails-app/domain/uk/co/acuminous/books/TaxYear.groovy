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

package uk.co.acuminous.books

import org.joda.time.DateTime

class TaxYear {

    DateTime start
    DateTime end

    static mapping = {
        start column: 'start_date'
        end column: 'end_date'
    }

    boolean equals(Object other) {
        if (other == null) {
            return false
        } else if (!TaxYear.isAssignableFrom(other.class)) {
             return false
        } else {
            TaxYear otherTaxYear = (TaxYear) other
            return this.start == otherTaxYear.start && this.end == otherTaxYear.end 
        }

    }
}
