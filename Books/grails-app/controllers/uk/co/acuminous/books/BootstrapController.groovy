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
