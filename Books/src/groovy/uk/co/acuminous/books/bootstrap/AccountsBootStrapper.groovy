package uk.co.acuminous.books.bootstrap

import org.apache.poi.hssf.usermodel.*
import org.apache.poi.ss.usermodel.*
import org.joda.time.LocalDate
import uk.co.acuminous.books.utils.BooksUtils
import uk.co.acuminous.books.Invoice
import uk.co.acuminous.books.Amount
import uk.co.acuminous.books.VatRate
import uk.co.acuminous.books.Expense
import uk.co.acuminous.books.VatReturn
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class AccountsBootStrapper {

    DateTimeFormatter DTF = DateTimeFormat.forPattern('dd-MMM-yyyy')

    static Map INVOICES_CFG = [
        name: 'Invoices',
        mapping: [Raised:'raised', Settled:'settled', Ref:'reference', NET:'net', 'VAT Charged':'vat', GROSS:'gross', Description:'narrative'],
        defaults: [:],
        filter: { Map record -> record.raised },
        persist: { Map record ->
            Invoice invoice = new Invoice(record)
            Amount amount = new Amount(record)
            VatRate vatRate = VatRate.withCriteria(uniqueResult:true) {
                le('start', invoice.raised)
                ge('end', invoice.raised)
            }
            amount.vatRate = vatRate
            invoice.amount = amount
            assert invoice.save(flush:true), invoice.errors
        }
    ]

    static Map EXPENSES_CFG = [
            name: 'Purchases',
            mapping: ['Stmt Date':'incurred', Description:'narrative', category:'Category', NET:'net', 'VAT':'vat', Gross:'gross', Reclaimed:'vatReclaimed'],
            defaults: [vatReclaimed: 0.00G],
            filter: { Map record -> record.incurred },
            persist: { Map record ->
                Expense expense = new Expense(record)
                Amount amount = new Amount(record)
                VatRate vatRate = VatRate.withCriteria(uniqueResult:true) {
                    le('start', expense.incurred)
                    ge('end', expense.incurred)
                }
                amount.vatRate = vatRate
                expense.amount = amount
                assert expense.save(flush:true), expense.errors
            }
    ]

    static List VAT_RETURN_PERIODS = [
        [start:'01-Mar-2010', end:'31-May-2010'],
        [start:'01-Jun-2010', end:'31-Aug-2010'],
        [start:'01-Sep-2010', end:'30-Nov-2010']
    ]    

    Workbook workbook
    Sheet sheet
    Map mapping
    Map defaults
    List columnNames = []
    List data = []    

    static void run(InputStream inputStream) {
        decorateCellWithGetValueMethod()

       AccountsBootStrapper bootStrapper = new AccountsBootStrapper(inputStream)
        if (Invoice.count() == 0) {
            bootStrapper.importSheet(INVOICES_CFG)
        }
        if (Expense.count() == 0) {
            bootStrapper.importSheet(EXPENSES_CFG)
        }
        if (VatReturn.count() == 0) {
            bootStrapper.createVatReturns()
        }
    }

    AccountsBootStrapper(InputStream inputStream) {
        workbook = new HSSFWorkbook(inputStream);
    }

    void importSheet(Map config) {
        init(config)
        readColumnNames()
        importRows()
        data.findAll { Map record -> config.filter(record) }.each { Map record -> config.persist(record) }
    }    

    void init(Map config) {
        sheet = workbook.getSheet(config.name)
        columnNames.clear()
        data.clear()
        mapping = config.mapping
        defaults = config.defaults
    }

    void readColumnNames() {
        Row row = sheet.getRow(0)
        Iterator it = row.iterator()
        while (it.hasNext()) {
            Cell cell = it.next()
            columnNames << cell.getValue()
        }
    }    

    void importRows() {
        int rows = sheet.getPhysicalNumberOfRows()
        (1..rows).each { int rowIndex ->
            Row row = sheet.getRow(rowIndex)
            if (row) {
                Map record = new HashMap(defaults) 
                columnNames.eachWithIndex { String columnName, int cellIndex ->
                    Cell cell = row.getCell(cellIndex)
                    if (cell && cell.value != null) {
                        String fieldName = mapping[columnName] ?: columnName                        
                        record[fieldName] = cell.value
                    }
                }
                data << record
            }
        }        
    }

    void createVatReturns() {
        VAT_RETURN_PERIODS.each { Map dates ->
            LocalDate start = DTF.parseDateTime(dates.start).withZone(BooksUtils.timeZone).toLocalDate()
            LocalDate end = DTF.parseDateTime(dates.end).withZone(BooksUtils.timeZone).toLocalDate()
            VatReturn vatReturn = new VatReturn().forPeriod(start, end)
            assert vatReturn.save(flush:true), vatReturn.errors
        }
    }

    static void decorateCellWithGetValueMethod() {

        HSSFCell.metaClass.getValue = {
            switch (delegate.cellType) {
                case HSSFCell.CELL_TYPE_FORMULA:
                    FormulaEvaluator evaluator = delegate.sheet.workbook.creationHelper.createFormulaEvaluator();
                    CellValue value = evaluator.evaluate(delegate)                                        
                    return value.getNumberValue()
                    break;                
                case HSSFCell.CELL_TYPE_NUMERIC:
                     if (HSSFDateUtil.isCellDateFormatted(delegate)) {
                         double rawValue = delegate.getNumericCellValue()
                         Date dateValue = HSSFDateUtil.getJavaDate(rawValue)
                         LocalDate localDate = new LocalDate(dateValue, BooksUtils.timeZone)
                         return localDate
                     } else {
                         return delegate.numericCellValue
                     }
                     break;
                case HSSFCell.CELL_TYPE_STRING:
                    return delegate.stringCellValue
                    break;                
            }
        }
    }

}
