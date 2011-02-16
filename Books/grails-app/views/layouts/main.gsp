<html>
    <head>
        <title><g:layoutTitle default="<g:message code="books.title" /></title>
        <g:render template="/fragments/css"/>
        <g:render template="/fragments/javascript"/>
        <g:layoutHead />
    </head>
    <body id="${pageProperty(name: 'body.id')}" class="${pageProperty(name: 'body.class')}">
        <g:layoutBody />
        <div id='dynamic-content-below-here'></div>
    </body>
</html>