<%@ page import="uk.co.acuminous.books.Invoice" %>
<html>
    <head>
        <meta name="layout" content="main" />
        <title>Attachments</title>
    </head>    
    <body>
        <g:render template="/attachment/table" model="${[entity: Invoice.list()[0] ?: null, field: 'attachments']}"/>
    </body>
</html>