<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>Fixture</title>
        <meta name="layout" content="main" />
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
    </head>
    <body>
        <g:form action="perform">
            <g:textField name="step" value="" style="width:50%"/>
            <g:submitButton name="perform" value="Make it so"/>
        </g:form>            
        <ul>
            <g:each in="${candidates}" var="candidate">
                <li style="cursor:pointer">Given ${candidate.patternAsString}</li>
            </g:each>
        </ul>
        <g:javascript>
$('li').click(function() {
   $('#step').val($(this).text());
});        
        </g:javascript>
    </body>
</html>