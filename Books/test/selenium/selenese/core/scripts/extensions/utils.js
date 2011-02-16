Selenium.prototype.replaceVariables = function(str) {
    var result = str;
    result = this.resolveStoredVars(result);
    result = this.resolveMessageCodes(result);
    result = this.resolveLanguageMapping(result);
    return result;
};

Selenium.prototype.resolveStoredVars = function(str) {
    var result = str;
    var storedVarPattern = /\$\{[\w\[\]\.]+\}/g;
    var match = str.match(storedVarPattern);
    if (match) {
        for (var i = 0; match && i < match.length; i++) {
            var variableNameIncludingDelimiters = match[i];
            var variableName = variableNameIncludingDelimiters.substring(2, variableNameIncludingDelimiters.length - 1);
            var replacement = storedVars[variableName];
            if (replacement == undefined) {
                try {
                     replacement = eval('storedVars.' + variableName);
                } catch (e) {
                    // No stored var existed
                }
            }
            if (replacement != undefined) {
                result = result.replace(variableNameIncludingDelimiters, replacement);
            }
        }
    };
    return result;
};

Selenium.prototype.resolveMessageCodes = function(str) {
    var result = str;
    var copyTextPattern = /^<(.*)>(?: with \[(.*)\])?$/;
    var match = copyTextPattern.exec(result);
    if (match) {
        var code = match[1];
        var jsonArgs = '';

        if ((match.length == 3) && (match[2] != undefined)) {
            var args = match[2].split(',');
            for (var i=0; i<args.length; i++) {
                jsonArgs += "'" + args[i] + "'";
                if (i != args.length-1) {
                    jsonArgs += ", ";
                }
            }
        }
        result = this.getMessageText("{code: '" + code + "', args: [" + jsonArgs + "]}");        
    };
    return result;
};

Selenium.prototype.resolveLanguageMapping = function(str) {
    var result = str;
    if (naturalLanguageExtensions) {
        var mapping = naturalLanguageExtensions.findLocator(str);
        if (mapping) {
            result = mapping.resolve(str);
        };
    }
    return result;
};

Selenium.prototype.getCurrentWindow = function() {
    var window = selenium.browserbot.getCurrentWindow();
    if (window.wrappedJSObject) {
        window = window.wrappedJSObject;
    }
    return window;
};

Selenium.prototype.httpGet = function(url, options) {
    var location = this.getCurrentWindow().location;
    var fullUrl = location.protocol + '//' + location.host + url
    var view = options.view ? options.view : 'html';
    var checkStatus = options.checkStatus ? options.checkStatus : true;
    var status = options.status ? options.status.toString() : '200'

    var request = new XMLHttpRequest();
    request.open('GET', fullUrl, false);
    request.setRequestHeader('view', view);
    request.send(null);

    if (checkStatus && status != request.status) {
        Assert.fail('Actual Value ' + request.status + ' did not match ' + status + ' for ' + uril);
    }

    return request;
};

Selenium.prototype.httpPost = function(url, params, options) {
    var location = this.getCurrentWindow().location;
    var fullUrl = location.protocol + '//' + location.host + url
    var view = options.view ? options.view : 'html';
    var checkStatus = options.checkStatus != undefined ? options.checkStatus : true;
    var status = options.status ? options.status : '200'

    var request = new XMLHttpRequest();
    request.open('POST', fullUrl, false);
    request.setRequestHeader('view', view);
    request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    request.send(params);

    if (checkStatus && status != request.status) {
        Assert.fail('Actual Value ' + request.status + ' did not match ' + status + ' for ' + url);
    }

    return request;
};

Selenium.prototype.reportHttpErrors = function(request, messages) {
    var rc = request.status;
    if (rc != 200) {
        var failure = messages[rc] ? messages[rc] : "Error " + rc
        this.getCurrentWindow().document.write("<h1 style='color: red'>" + failure + "</h1>");
        Assert.fail(failure);
    }
};

Selenium.prototype.loadJavaScript = function(urls) {
    for (var i = 0; i < urls.length; i++) {
        var url = urls[i];
        var request = this.httpPost(url, '', {});
        eval(request.responseText);
    }
}

Selenium.prototype.parseJson = function(json) {
    var x;
    eval('x = ' + json);
    return x;
};