;(function($) {
    $.fn.vatify = function(options) {
        var container = this;
        var baseId = options.baseId;
        var vatPickerUrl = options.vatPickerUrl;
        var dateField = $(options.dateField, container);
        var rateId = options.rateId;
        
        var netField = initCurrencyField('net');
        var grossField = initCurrencyField('gross');
        var vatField = initCurrencyField('vat');
        var vatReclaimedField = initCurrencyField('vatReclaimed');
        var vatRateField = renderVatRateField();

        if (vatRateField.val() != 'null') {
            vatField.stun();
        }
        if (netField.val() != '') {
            grossField.stun();
        } else if (grossField.val()) {
            netField.stun();
        }

        dateField.bind('change', onVatDateChanged);
        container.bind('net-updated', onNetUpdated);
        container.bind('vat-updated', onVatUpdated);
        container.bind('gross-updated', onGrossUpdated);
        container.bind('vatRate-updated', onVatRateUpdated);

        function initCurrencyField(qualifier) {
            var selector = '#' + baseId + '-' + qualifier;
            var field = $(selector, container);

            field.addClass(qualifier);
            field.maskMoney({showSymbol: false, allowZero: true});

            field.keyup(function(event) {
                if (field.isSignificant(event.keyCode)) {
                    field.trigger('mask');
                    container.trigger(qualifier + '-updated');
                }
            });
                                                    
            field.updateValue = function(value) {
                var value = (Math.round(value * 100) / 100).toFixed(2);
                field.val(value);
                field.trigger('mask');
            };

            field.stun = function() {
                field.attr('readonly', true);
                field.addClass('readonly');
            };

            field.revive = function() {
                field.attr('readonly', false);
                field.removeClass('readonly');
            };

            field.reset = function() {
                field.revive();
                field.updateValue(0);
            }

            field.isStunned = function() {
                return field.attr('readonly');
            };

            field.isSignificant = function(keyCode) {
                if (field.isStunned()) {
                    return false;
                } else {
                    var backspaceKeyCode = 8;
                    var deleteKeyCode = 46;
                    return parseInt(keyCode) == backspaceKeyCode || parseInt(keyCode) >= deleteKeyCode;
                }
            };

            return field;
        };
        
        function renderVatRateField() {
            $.ajax({
                url: vatPickerUrl,
                data: {
                    when: dateField.val(),
                    selected: rateId
                },
                cache: true,
                async: false,
                success: function(data) {
                    $(vatField).after(data);
                }
            });

            return initVatRateField();
        }

        function initVatRateField() {
            var selector = '#' + baseId + '-vatRate';
            var field = $(selector, container);
            field.addClass('vatRate');
            field.change(onVatRateUpdated);
            return field;
        };

        function onVatDateChanged() {
            rateId = vatRateField.val();
            vatRateField.remove();
            vatRateField = renderVatRateField();
            if (vatRateField.val() != rateId) {
                vatRateField.trigger('change');
            }
        }

        function onNetUpdated() {
            grossField.stun();
            calculateVatFromNet();
            calculateGross();
            if (netField.val() == 0) {
                grossField.reset();
            }
        }

        function onGrossUpdated() {
            netField.stun();            
            calculateVatFromGross();
            calculateNet();
            if (grossField.val() == 0) {
                netField.reset();
            }
        }

        function onVatUpdated() {
            if (netField.isStunned()) {
                calculateNet();
            } else if (grossField.isStunned()) {
                calculateGross();
            }
        }

        function onVatRateUpdated() {
            rateId = vatRateField.val();
            if (vatRateField.val() == 'null') {
                vatField.reset();
                container.trigger('vat-updated');                
            } else {
                vatField.stun();
                if (netField.isStunned()) {
                    calculateVatFromGross();
                    container.trigger('vat-updated');
                } else if (grossField.isStunned()) {
                    calculateVatFromNet();
                    container.trigger('vat-updated');
                }
            }
        }

        function calculateNet() {
            var gross = parseCurrency(grossField.val());
            var vat = parseCurrency(vatField.val());
            if (isNumber(gross) && isNumber(vat)) {
                netField.updateValue(gross - vat);
            }
        };

        function calculateGross() {
            var net = parseCurrency(netField.val());
            var vat = parseCurrency(vatField.val());
            if (isNumber(net) && isNumber(vat)) {
                grossField.updateValue(net + vat);
            }
        };

        function calculateVatFromNet() {
            var net = parseCurrency(netField.val());
            var vatRate = getVatRate()

            if (isNumber(vatRate) && isNumber(net)) {
                vatField.updateValue(net * vatRate);
            }
        };

        function calculateVatFromGross() {
            var gross = parseCurrency(grossField.val());
            var vatRate = getVatRate();

            if(isNumber(vatRate) && isNumber(gross)) {
                var net = gross / (1 + vatRate);
                vatField.updateValue(gross - net);
            }
        };


        function getVatRate() {
            var rateId = vatRateField.val();
            return vatRateField[0].rates[rateId];
        }

        function parseCurrency(amount) {
            return parseFloat(amount.replace(/,/g, ''));
        }

        function isNumber(amount) {
            return !isNaN(amount)
        }
    };
}) (jQuery);