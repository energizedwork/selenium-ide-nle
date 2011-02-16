<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <meta name="layout" content="main" />
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />        
    </head>
    <body>

        <div id="taxYearPickerContainer">
            <booksui:taxYearPicker selected="${session.taxYear}" />                
        </div>
        <div id="tabs" class="ui-tabs">            
            <ul>
                <booksui:tab title="tabs.invoices" controller="invoice" />
                <booksui:tab title="tabs.expenses" controller="expense" />
                <booksui:tab title="tabs.vatReturns" controller="vatReturn" />
                <booksui:tab title="tabs.rates" controller="vatRate" />
            </ul>
            <booksui:tabContents title="tabs.invoices" />
            <booksui:tabContents title="tabs.expenses" />
            <booksui:tabContents title="tabs.vatReturns" />            
            <booksui:tabContents title="tabs.rates" />
        </div>

        <g:javascript>
            $(document).ready(function() {
                var tabsElement = $('#tabs')
                tabsElement.tabs({
                    select: function() {
                        $('.tabContent').html('');
                        // ui-dialog needs to be removed twice
                        $('#dynamic-content-below-here ~ .ui-dialog').each(function() {
                            jQuery(this).remove();
                        });
                        $('#dynamic-content-below-here ~ :not(.ui-datepicker)').each(function() {
                            jQuery(this).remove();
                        });
                    }
                });
                $('#taxYearPicker').bind('tax-year-change', function() {
                    var currentTabIndex = tabsElement.tabs('option', 'selected');
                    tabsElement.tabs("load", currentTabIndex);
                });
            });
        </g:javascript>


    </body>
</html>