var naturalLanguageExtensions;
var lastStepType = null;

Selenium.prototype.doInitialise = function() {
    return this.doOpen('/fixture/setup');
}

Selenium.prototype.doLoad = function(mappingUrls) {
    this.initialiseNaturalLanguageExtensions();
    this.loadMappings(mappingUrls);
};

Selenium.prototype.initialiseNaturalLanguageExtensions = function() {
    this.loadJavaScript([
        '/js/naturalLanguageExtensions/natural-language-extensions.js'
    ]);
    var languageMappingFactory = new SeleniumLanguageMappingFactory('$', this);
    naturalLanguageExtensions = new LanguageMappingHolder(languageMappingFactory);
};

Selenium.prototype.loadMappings = function(mappings) {
    var mappings = mappings ? mappings.replace(/\s/g, '').split(/,/) : [];
    var urls = []
    for (var i = 0; i < mappings.length; i++) {
        urls[urls.length] = '/js/naturalLanguageExtensions/mapping/' + mappings[i] + '.js';
    }
    this.loadJavaScript(urls);
};

Selenium.prototype.doGiven = function(step, name) {
    lastStepType = 'given';
    return this.invokeFixture('perform', 'Given ' + step, name);
};

Selenium.prototype.doWhen = function(text, args) {
    lastStepType = 'when';
    return naturalLanguageExtensions.resolve(text, args);
};

Selenium.prototype.doThen = function(text, args) {
    lastStepType = 'then';
    return naturalLanguageExtensions.resolve(text, args);
};

Selenium.prototype.doAnd = function(text, args) {
    switch(lastStepType) {
        case 'given':
            return this.doGiven(text, args);
        case 'when':
            return this.doWhen(text, args);
        case 'then':
            return this.doThen(text, args);
        default:
            Assert.fail("Cannot use 'and' without previously performing a 'Given', 'When' or 'Then' step.");
    }
};

Selenium.prototype.invokeFixture = function(action, step, name) {
    var request;

    if (name) {
        var options = {view: 'json', checkStatus: false};
        request = this.httpPost('/fixture/' + action, 'step=' + encodeURIComponent(step), options);
        this.checkSetupStatus(request, step);
        try {
            storedVars[name] = eval('(' + request.responseText +')');
        } catch (e) {
            Assert.fail('Invalid JSON ->' + request.responseText + '<- returned by step "' + step + '"');
        }
    } else {
        var options = {view: 'none', checkStatus: false};
        request = this.httpPost('/fixture/' + action, 'step=' + encodeURIComponent(step), options);
        this.checkSetupStatus(request, step);
    }
};

Selenium.prototype.checkSetupStatus = function(request, data) {
    var messages = {
        '404' : 'No such fixture "Given ' + data + '"',
        '500' : 'Error running fixture "Given ' + data + '"'
    }
    this.reportHttpErrors(request, messages);
};

Selenium.prototype.waitFor = function(condition, retries, interval) {
    var success;
    var keepTrying = true;
    while (keepTrying) {
        success = condition();
        if (!success && retries > 0) {
            this.httpPost('/fixture/sleep', 'millis=' + interval, {});
            retries--;
        } else {
            keepTrying = false;
        }
    }
    if (!success) {
        Assert.fail("Exceeded retries")
    }
};

Selenium.prototype.getMessageText = function(json) {
    var params = this.parseJson(json);
    var code = params.code;
    var args = params.args;

    var paramStr = "code=" + params.code;
    for (var i = 0; i < args.length; i++) {
            paramStr = paramStr + "&args=" + args[i];
    }
    var request = this.httpPost('/fixture/resolveMessage', paramStr, {view: 'json'});
    var result = this.parseJson(request.responseText);
    return result.msg;
};


