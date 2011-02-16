Function.prototype.extendsFrom = function(Super) {
   var Self = this;
   var Func = function() {
      Super.apply(this, arguments);
      Self.apply(this, arguments);
   };
   Func.prototype = new Super();
   return Func;
};

LanguageMappingHolder = function(languageMappingFactory) {
    this.languageMappingFactory = languageMappingFactory ? languageMappingFactory : new LanguageMappingFactory();
    this.locators = {};
    this.steps = {};
    this.usage = {};

    this.addLocator = function(template, target) {
        return this.add(template, target, this.locators);
    };

    this.addStep = function(template, target) {
        if (!this.isArray(template)) {
            template = [template];
        }

        for (var i = 0; i < template.length; i++) {
            this.add(template[i], target, this.steps);
        }

        return this;
    };

    this.add = function(template, target, mappings) {
        var newMapping = this.languageMappingFactory.createMapping(template, target).init();
        var existingMapping = mappings[newMapping.templatePattern];
        if (!existingMapping) {
            mappings[newMapping.templatePattern] = newMapping;
            this.initUsage(newMapping);
        } else {
            throw new LanguageMappingException('[' + existingMapping.template + '] conflicts with [' + newMapping.template + ']');
        };
        return this;
    };

    this.findLocator = function(text) {
        return this.find(text, this.locators);
    }

    this.findAssertion = function(text) {
        return this.find(text, this.steps);
    };

    this.find = function(text, mappings) {
        var highScore = -1;
        var bestMatch;
        var equalBestMatch;

        for (var templatePattern in mappings) {
            var candidate = mappings[templatePattern];            
            var candidateScore = candidate.score(text);
            if (candidateScore > highScore) {
                highScore = candidateScore;
                bestMatch = candidate;
                equalBestMatch = undefined;
            } else if (candidateScore == highScore) {
                equalBestMatch = candidate;
            }
        }

        if (bestMatch && equalBestMatch) {
           throw new LanguageMappingException("[" + bestMatch.template + "] conflicts with [" + equalBestMatch.template + "] for [" + text + "]");
        } else if (bestMatch) {
            this.recordUsage(bestMatch);
        }

        return bestMatch;
    };

    this.resolve = function(text, args) {
        var mapping = this.findAssertion(text);
        if (mapping) {
            return mapping.resolve(text, args);
        } else {
            throw new LanguageMappingException("Cannot find mapping for [" + text + "]");
        }
    };

    this.initUsage = function(mapping) {
        this.usage[mapping.template] = 0;
    };

    this.recordUsage = function(mapping) {
        this.usage[mapping.template] = this.usage[mapping.template] + 1;
    };

    this.isArray = function(obj) {
        return (obj.constructor.toString().indexOf("Array") != -1)
    };
};

LanguageMappingFactory = function(prefix) {
    this.prefix = prefix ? prefix : '$';

    this.createMapping = function(template, target) {
        var type = typeof target;
        switch (type) {
            case "string": return this.createSubstitutionLanguageMapping(template, target);
            case "function": return this.createFunctionLanguageMapping(template, target);
            default: return this.handleUnsupportedTarget(template, target);
        }
    };

    this.createSubstitutionLanguageMapping = function(template, target) {
        return new SubstitutionLanguageMapping(template, target, this.prefix);
    };

    this.createFunctionLanguageMapping = function(template, target) {
        return new FunctionLanguageMapping(template, target, this.prefix);
    };

    this.handleUnsupportedTarget = function(template, target) {
        throw new LanguageMappingException('Unsupported target: ' + target);
    };
};

SeleniumLanguageMappingFactory = function(prefix, selenium) {
    this.selenium = selenium;

    this.createFunctionLanguageMapping = function(template, target) {
        var mapping = new FunctionLanguageMapping(template, target, this.prefix);
        mapping.selenium = this.selenium;

        mapping.getWindow = function() {
            return this.selenium.getCurrentWindow();
        };

        mapping.getDocument = function() {
            return this.getWindow().document;
        };

        return mapping;
    };
};
SeleniumLanguageMappingFactory = SeleniumLanguageMappingFactory.extendsFrom(LanguageMappingFactory);

LanguageMapping = function(template, target, prefix) {

    this.prefix = prefix;
    this.allWordsBeginningWithThePrefix = new RegExp('(\\' + this.prefix + '\\w+)', 'g');
    this.template = template;
    this.target = target;
    this.templatePattern;
    this.scoringTemplate;

    this.createTemplatePattern = function() {
        var escapedTemplate = this.escapeRegEx(this.template);
        var pattern = escapedTemplate.replace(this.allWordsBeginningWithThePrefix, '(.+)');
        this.templatePattern = '^' + pattern + '$';
    };

    this.createScoringTemplate = function() {
        this.scoringTemplate = this.template.replace(this.allWordsBeginningWithThePrefix, '');
    }

    this.escapeRegEx = function(regex) {
        return regex.replace(/([\[\]\{\}\?\^\.\*\(\)\+\\])/g, '\\$1');
    };

    this.score = function(text) {
        var score = -1;
        var match = text.match(this.templatePattern);
        if (match) {
            score = 1000 - new LevenshteinDistance(text, this.scoringTemplate).calculate();
        }
        return score;
    };
};

SubstitutionLanguageMapping = function(template, target, prefix) {
    this.substitutionPattern;

    this.init = function() {
        this.createTemplatePattern();
        this.createScoringTemplate();
        this.createSubstitutionPattern();
        return this;
    };

    this.createSubstitutionPattern = function() {
        var result = this.target;
        var match = this.template.match(this.allWordsBeginningWithThePrefix);
        if (match) {
            for (var i = 0; i < match.length; i++) {
                var prefixedWord = match[i];
                var placeHolder = this.prefix + (i+1);
                while (result.indexOf(prefixedWord) >= 0) {
                    result = result.replace(prefixedWord, placeHolder);
                }
            }
        }
        this.substitutionPattern = result;
    };

    this.resolve = function(text) {
        var result = this.substitutionPattern;
        var match = text.match(this.templatePattern);
        for (var i = 1; i < match.length; i++) {
            while (result.indexOf(this.prefix + i) >= 0) {
                result = result.replace(this.prefix + i, match[i]);
            }
        }
        return result;
    };
}
SubstitutionLanguageMapping = SubstitutionLanguageMapping.extendsFrom(LanguageMapping);

FunctionLanguageMapping = function(template, target, prefix) {
    this.inlineArgumentNames = [];
    this.inlineArguments = {};

    this.init = function() {
        this.createTemplatePattern();
        this.createScoringTemplate();
        this.parseInlineArgumentNames();
        return this;
    };

    this.parseInlineArgumentNames = function() {
        var argNamesWithPrefix = this.template.match(this.templatePattern);
        if (argNamesWithPrefix) {
            this.inlineArgumentNames = new Array(argNamesWithPrefix.length - 1);
            for (var i = 1; i < argNamesWithPrefix.length; i++) {
                this.inlineArgumentNames[i-1] = argNamesWithPrefix[i].substr(1);
            }
        }
    };

    this.resolve = function(text, explicitArgument) {
        this.parseInlineArguments(text);
        return this.target(explicitArgument);
    };

    this.parseInlineArguments = function(text) {
        this.inlineArguments = {};
        var match = text.match(this.templatePattern);
        for (var i = 1; i < match.length; i++) {
            var argName = this.inlineArgumentNames[i-1];
            var argValue = match[i];
            this.inlineArguments[argName] = argValue;
        }
    };
};
FunctionLanguageMapping = FunctionLanguageMapping.extendsFrom(LanguageMapping);

LanguageMappingException = function(description) {
    this.description = description;
};

LevenshteinDistance = function(s1, s2) {
    this.s1 = s1;
    this.s2 = s2;
    this.distanceTable;

    this.initDistanceTable = function() {

        var x = this.s1.length;
        var y = this.s2.length;

        this.distanceTable = new Array(x + 1);

        for (i = 0; i <= x; i++) {
            this.distanceTable[i] = new Array(y + 1);
        }

        for (var i = 0; i <= x; i++) {
            for (var j = 0; j <= y; j++) {
                this.distanceTable[i][j] = 0;
            }
        }

        for (var i = 0; i <= x; i++) {
            this.distanceTable[i][0] = i;
        }

        for (var j = 0; j <= y; j++) {
            this.distanceTable[0][j] = j;
        }
    };

    this.calculate = function() {

        this.initDistanceTable();

        if (this.s1 == this.s2) {
            return 0;
        }

        var s1Length = this.s1.length;
        var s2Length = this.s2.length;

        for (var j = 0; j < s2Length; j++) {
            for (var i = 0; i < s1Length; i++) {
                if (this.s1[i] == this.s2[j]) {
                    this.distanceTable[i+1][j+1] = this.distanceTable[i][j];
                } else {
                    var deletion = this.distanceTable[i][j+1] + 1;
                    var insertion = this.distanceTable[i+1][j] + 1;
                    var substitution = this.distanceTable[i][j] + 1;

                    this.distanceTable[i+1][j+1] = Math.min(substitution, deletion, insertion)
                }
            }
        }

        return this.distanceTable[s1Length][s2Length];
    };
};

