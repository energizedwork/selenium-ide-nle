<g:form name="attachment-form-${attachmentOwner.id}" controller="attachment" action="upload" enctype="multipart/form-data">
    <g:hiddenField name="ownerId" value="${attachmentOwner.id}" />
    <g:hiddenField name="ownerClass" value="${attachmentOwner.class.name}" />
    <g:hiddenField name="ownerField" value="attachments" />
    <div class="fileInputs">
        <input type="file" name="file" class="file" onchange="$('#fakeFile-${attachmentOwner.id}').val(this.value)" />
        <div class="fakeFileWrapper">
            <input id="fakeFile-${attachmentOwner.id}" class="fakeFile"/>
            <input class="fakeButton" type="submit" value="Upload"/>
        </div>
    </div>
</g:form>
<g:javascript>
    $('#attachment-form-${attachmentOwner.id}').ajaxForm({success: function() { $('tbody.data').parents('.crudTable')[0].refresh()} });
</g:javascript>