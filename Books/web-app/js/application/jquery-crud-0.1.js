;(function($) {
    $.fn.crudify = function(options) {
        this.addClass('crudTable')
        var target = this[0];
        var crudTable = new CrudTable(target.id, options);

        target.refresh = function() {
            crudTable.refresh();
        };
        target.del = function(id, message) {
            crudTable.del(id, message);
        };
        target.dialog = function(id) {
            crudTable.dialog(id);
        };
        target.save = function() {
            crudTable.save();
        };        

        initialiseDialog(crudTable, options.dialogOptions);


        function initialiseDialog(crudTable, dialogOptions) {
            dialogOptions.buttons = {
                "OK": function() { onOk(crudTable) },
                "Cancel": function() { onCancel(crudTable) }
            };
            $(crudTable.dialogSelector).dialog(dialogOptions);

        }

        function onOk(crudTable) {
            if (crudTable.save()) {
                $(crudTable.dialogSelector).dialog("close");
            };
        };

        function onCancel(crudTable) {
            $(crudTable.dialogSelector).dialog("close");
        };


        return target;
    };
}) (jQuery);

CrudTable = function(id, options) {
    this.tableSelector = '#' + id;
    this.dialogSelector = this.tableSelector + 'Dialog';
    this.refreshUrl = options.refreshUrl;
    this.dialogUrl = options.dialogUrl;
    this.deleteUrl = options.deleteUrl;
    this.saveUrl = options.saveUrl;
    this.hasErrors = options.hasErrors;

    this.refresh = function() {
        var target = $(this.tableSelector);
        $.ajax({
            url: this.refreshUrl,
            cache: false,
            success: function(data) {
                $('tbody.data', target).replaceWith(data);
            }
        });
    };

    this.dialog = function(id) {
        var target = $(this.dialogSelector);
        $.ajax({
            url: this.dialogUrl,
            type: 'POST',
            data: {id: id},
            success: function(data) {
                $(target).html(data);
                target.dialog('open');
            }
        });
    };

    this.del = function(id, message) {
        if (confirm(message)) {
            var target = $(this.tableSelector);
            $.ajax({
                url: this.deleteUrl,
                type: 'POST',
                data: {id: id},
                success: function(data) {
                    $('tbody.data', target).replaceWith(data);
                }
            })
        };
    };

    this.save = function() {
        var crudTable = this;
        var target = $(this.dialogSelector);
        var form = $('form', target);
        $(form).ajaxSubmit({
            url: this.saveUrl,            
            success: function(html) {
                if (crudTable.hasErrors(html)) {
                    target.html(html);
                } else {
                    crudTable.refresh();                    
                    target.dialog("close");
                }
            }
        });
    }
};