$.ajaxSetup({
    type: 'POST'
});

Books = {
    deleteIcon: {},
    attachments: {}
};

Books.confirmAjax = function(prompt, ajaxOptions) {
    if (confirm(prompt)) {
        $.ajax(ajaxOptions);
    }
};

Books.deleteIcon.onClick = function(e) {
    $(e.currentTarget).parents('.crudTable')[0].del(e.data.id, e.data.prompt);
    e.stopPropagation();
};

Books.attachments.toggle = function(e) {
    var icon = $(e.currentTarget);
    var iconId = icon.attr('id');
    var table = icon.parents('tbody.data').parent();
    table.toggleClass(iconId);

    if (table.hasClass(iconId)) {
        $('tr.' + iconId).show();
    } else {
        $('tr.' + iconId).hide();
    }
    e.stopPropagation();
};

Books.hideOrShowAttachments = function() {
    $('tr.heading, tr.download, tr.upload').hide();
    var showList = $('tbody.data').parent().attr('class').split(' ');
    $.each(showList, function() {
        $('tr.' + this).show();
    })
};

Books.attachments.del = function(e) {
    Books.confirmAjax(e.data.prompt, {
        url: e.data.url,
        success: function(data) {
            $('tbody.data').parents('.crudTable')[0].refresh();
        }
    });
};