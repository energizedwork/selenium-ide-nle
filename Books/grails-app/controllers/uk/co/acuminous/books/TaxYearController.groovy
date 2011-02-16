package uk.co.acuminous.books

class TaxYearController {

    def change = { TaxYearCmd cmd ->
        session.taxYear = cmd.taxYear
        render "OK"
    }
}

class TaxYearCmd {
    TaxYear taxYear
}
