naturalLanguageExtensions

    .addLocator('the home page', '/')

    .addLocator('the tax year selector', 'taxYearPicker')

    .addLocator('the $tabName tab', '//a[@title="$tabName"]')

    .addLocator('the $nth column in the $tableId table', function() {

        var nth = parseInt(this.inlineArguments.nth);
        var tableId = this.inlineArguments.tableId;

        return "//table[@id='" + tableId + "Table']/thead/tr/th[" + nth + "]"; 

    });


var parseNth = function(value) {
    return parseInt(value.substr(0, value.length - 2)) - 1;
};