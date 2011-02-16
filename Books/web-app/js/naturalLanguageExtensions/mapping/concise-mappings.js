var testContext = {};

naturalLanguageExtensions

    .addStep('I open the home page', function() {
        return this.selenium.doOpen('/');
    })

    .addStep('change the tax year to "$taxYear"', function() {
        var selenium = this.selenium;

        selenium.waitFor(function() {
            return selenium.isElementPresent('taxYearPicker');
        }, 10, 500);

        return selenium.doSelect('taxYearPicker', this.inlineArguments.taxYear);
    })

    .addStep('there should be $count $item in the $tableId table', function() {
        var jQuery = this.getWindow().jQuery;

        var tableId = this.inlineArguments.tableId + 'Table';

        this.selenium.waitFor(function() {
            return jQuery('#' + tableId + ' tbody.data tr.selectable').length > 0;
        }, 10, 500);

        Assert.matches(this.inlineArguments.count, jQuery('#' + tableId + ' tbody.data tr.selectable').length);

        testContext.helper = new TableHelper(jQuery('#' + tableId), jQuery);
    })

    .addStep(['the $nth $item should have a $property of $value',
              'the $nth $item should have a $property $classifier of $value'], function() {
        var jQuery = this.getWindow().jQuery;

        var tableHelper = testContext.helper;
        var nth = parseNth(this.inlineArguments.nth);
        tableHelper.setCurrentRow(nth);

        Assert.matches(this.inlineArguments.value, tableHelper.getValue(this.inlineArguments.property));
    })

    .addStep(['a $property of $value',
              'a $property $classifier of $value'], function() {
        Assert.matches(this.inlineArguments.value, testContext.helper.getValue(this.inlineArguments.property));
    });


var parseNth = function(value) {
    return parseInt(value.substr(0, value.length - 2)) - 1;
};

var TableHelper = function(table, jQuery) {
    this.table = table;
    this.jQuery = jQuery;
    this.currentRow;
    this.getColumns = function() {
        return this.jQuery('thead tr th', this.table);
    };
    this.getColumnIndex = function(columnName) {
        var columns = this.getColumns();
        var columnIndex;
        for (var i = 0; i < columns.length; i++) {
            var th = jQuery(columns[i]);
            if (th.text().toLowerCase() == columnName.toLowerCase()) {
                columnIndex = i;
                break;
            }
        };
        return columnIndex;
    };
    this.setCurrentRow = function(index) {
        this.currentRow = this.jQuery('tbody.data tr.selectable:eq(' + index + ')', this.table);
    };
    this.getValue = function(columnName) {
        var columnIndex = this.getColumnIndex(columnName);
        return this.jQuery('td:eq(' + columnIndex + ')', this.currentRow).text();
    };
    this.getHeading = function(column) {
        return this.jQuery('th:eq(' + column + ')', this.table).text()
    }
};