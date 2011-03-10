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

import uk.co.acuminous.books.bootstrap.VatRateBootStrapper
import uk.co.acuminous.books.bootstrap.TaxYearBootStrapper
import uk.co.acuminous.books.bootstrap.AccountsBootStrapper
import org.springframework.web.multipart.commons.CommonsMultipartFile

class BootstrapController {

    def index = {
        cache false        
        VatRateBootStrapper.run()
        TaxYearBootStrapper.run()
        render(view: 'index')
    }

    def upload = {
        CommonsMultipartFile file = request.getFile('file')
        AccountsBootStrapper.run(file.inputStream)
        redirect(uri:'/')
    }

}
