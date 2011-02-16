import uk.co.acuminous.books.BooksService

class TaxYearFilters {

    BooksService booksService

    def filters = {
        setTaxYear(controller:'*', action:'*') {
            before = {
                synchronized(session) {
                    if (!session.taxYear && controllerName != 'fixture') {
                        session.taxYear = booksService.currentTaxYear
                    }
                }
            }
        }
    }

}