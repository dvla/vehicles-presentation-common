var html5Validation = {
    doesFieldValidate: function(field) {
        // Checks if a field will pass validation using html5 methods
        // If html5 validation is not available (IE8) it will return true as well
        return (typeof field.willValidate === "undefined" || field.validity.valid);
    },
    getFieldContainer: function(field) {
        // Gets the parent container, of the field, certain items such as email are grouped and its that
        // group which should be selected
        var fieldType = $(field).attr('type'),
            formItemParents = $(field).parents('.form-item');

        return (fieldType === "email" && formItemParents.length > 1)?formItemParents[1]:formItemParents[0];
    },
    showFieldError: function(field, errorMessage) {
        var container = html5Validation.getFieldContainer(field),
            errorContainer = $(container).children('p.error');

        $(container).addClass('validation added-by-client');

        if (errorContainer.length === 0) {
           $(container).prepend('<p class="error">' + errorMessage + '</p>');
        } else {
           $(errorContainer).text(errorMessage);
        }
    },
    hideFieldError: function(field) {
        var container = html5Validation.getFieldContainer(field);
        if ($(container).hasClass('added-by-client')) {
            $(container).removeClass('validation');

            $(container).find('p.error').remove();
        }
    },
    validateField: function(field) {
        // Validates an input field, and either shows / hides the error message
        var optionalField = $(field).closest('.optional-field');
        if (optionalField && optionalField.length && $(optionalField).is(':visible') === false ) {
            // It's in an optional field container, and this container is not visible so whatever the value
            // assume the field is valid
            html5Validation.hideFieldError(field);
            return true;
        } else if (html5Validation.doesFieldValidate(field)) {
            html5Validation.hideFieldError(field);
            return true;
        } else {
            html5Validation.showFieldError(field, $(field).data('validity-message') || field.validationMessage || "Error");
            return false;
        }
    },
    validateForm: function(form) {
        // For every control within the field, attempt to validate it
        var hasInvalidField = false,
            formFields = $(form).find('input');
        for (var i = 0; i < formFields.length; i++) {
            // if any field is invalid then the form is invalid
            if (html5Validation.validateField(formFields[i]) === false) {
                if (hasInvalidField === false ) {
                    // Scroll to the First field with error
                    $('html,body').animate({
                        scrollTop: $(formFields[i]).offset().top - 60
                    });
                }
                hasInvalidField = true;
            }
        }
        return (hasInvalidField === false);
    }
};


var addressLookup = function() {
		// Address Lookup
    var enableAddressLookup = function() {
        // Quick Access Variables Declaration
        var addresses = [],
            // Address inputs
            addressPicker         = $('.address-lookup-wrapper'),
            contextPath           = addressPicker.attr('app-context-path'),
            addressPostCodeLookup = $('.js-address-postcode-lookup'),
            postCodeLookupContainer = $('.postcode-lookup-container'),
            addressToggle         = $('.address-manual-toggle'),
            addressManualInput    = $('.address-manual-inputs-wrapper'),
            showSearchField       = $('.js-hidden-show-search-fields'),
            showAddressSelect     = $('.js-hidden-show-address-select'),
            showAddressField      = $('.js-hidden-show-address-fields'),
            showSearch            = showSearchField.attr('value') != "false",
            showSelect            = showAddressSelect.attr('value') === "true",
            showAddress           = showAddressField.attr('value') === "true",
            addressFind           = $('#address-find'),
            // AJAX api URL
            urlApi                = contextPath + "/address-lookup/postcode/",
            // Addresses list
            addressListWrapper    = $('.address-list-wrapper'),
            addressesList         = $(".js-address-list"),
            addressesListHint     = addressesList.attr("data-select-hint"),

            // Errors
            postCodeError         = $('.missing-postcode'),
            serverMessage         = $('.server-message');

        var showPostCodeLookupContainer = function(show) {
            if (show) {
                showSearchField.attr("value", "true");
                postCodeLookupContainer.show();
            } else {
                showSearchField.attr("value", "false");
                postCodeLookupContainer.hide();
            }
            showSearch = show;
        }

        var showAddressListWrapper = function(show) {
            if (show) {
                showAddressSelect.attr("value", "true");
                addressListWrapper.show();
            } else {
                showAddressSelect.attr("value", "false");
                addressListWrapper.hide();
            }
            showSelect = show;
        }

        var showAddressManualInput = function(show) {
            if (show) {
                showAddressField.attr("value", "true");
                addressManualInput.show();
            } else {
                showAddressField.attr("value", "false");
                addressManualInput.hide();
            }
            showAddress = show;
        }

        // Initialization
        var initAddressLookup = function () {
            // if backend returns with an error, on document ready the form will be displayed
            showPostCodeLookupContainer(showSearch);
            showAddressListWrapper(showSelect);
            showAddressManualInput(showAddress);
            if(showSearch) {
                $('.no-js-hidden').show();
            }
        };

        // Manual Input Mode
        var manualAddressMode = function () {
            hideAjaxErrors();
            $('.address-street-first').focus();
            addressToggle.hide();
            showPostCodeLookupContainer(false);
            showAddressListWrapper(false);
            showAddressManualInput(true);
        };

        // Populate Addresses List
        var showAddresses = function () {
            showAddressListWrapper(true);
        };

        // Hides AJAX errors
        var hideAjaxErrors = function () {
            serverMessage.hide();
            postCodeError.hide();
        };

        // AJAX error response
        var errorAjax = function (data) {
            hideAjaxErrors();
            addressFind.prop("disabled", false);
            addressesList.attr('data-ajax', false);
            showAddressListWrapper(false);
            showAddressManualInput(false);
            serverMessage.show();
            $('.server-message span').html(' ' + data.responseText);
        };

        // AJAX postcode not existing
        var postcodeNotFoundAjax = function() {
            hideAjaxErrors();
            addressFind.prop("disabled", false);
            postCodeError.show();
            showAddressListWrapper(false);
            showAddressManualInput(false);
        };

        // AJAX postcode success
        var postcodeFoundAjax = function(data) {
            addressFind.prop("disabled", false);
            addresses = data;
            var address = "";
            for (var i = 0; i < data.length; i++) {
                address = "<option value='" + i + "'>" + data[i].addressLine + "</option>";
                if (address != "") {
                    addressesList.append(address);
                }
            }
            showAddresses();
            hideAjaxErrors();
            addressesList.attr('data-ajax', true);
        };

        // AJAX GET
        var getAddresses = function (postcode) {
            url = urlApi + postcode;
            addressesList.html('<option value="default">' + addressesListHint + '</option>');
            $.ajax({
                type: "GET",
                url: url,
                cache: false
            }).done(function (data) {
                if (data.length) {
                    postcodeFoundAjax(data);
                } else {
                    postcodeNotFoundAjax();
                }
                addressesList.focus();
            }).fail(function (data) {
                errorAjax(data);
            });
        };

        // Populate Form
        var updateAddressForm = function (address) {
            var selected_address = addressesList.children(":selected").val(),
                addressFirst = $('.address-street-first').attr('data-form'),
                addressSecond = $('.address-street-second').attr('data-form'),
                addressThird = $('.address-street-third').attr('data-form'),
                addressTown = $('.address-town').attr('data-form'),
                addressPostcode = $('.address-postcode').attr('data-form');
            if (selected_address == "default") {
                $('#' + addressFirst).val("");
                $('#' + addressSecond).val("");
                $('#' + addressThird).val("");
                $('#' + addressTown).val("");
                $('#' + addressPostcode).val("");
            } else {
                $('#' + addressFirst).val(addresses[selected_address].streetAddress1);
                $('#' + addressSecond).val(addresses[selected_address].streetAddress2);
                $('#' + addressThird).val(addresses[selected_address].streetAddress3);
                $('#' + addressTown).val(addresses[selected_address].postTown);
                $('#' + addressPostcode).val(addresses[selected_address].postCode);
            }
        };

        // Reset Form
        var clearAddressForm = function () {
            hideAjaxErrors();
            $('.address-manual-inputs-wrapper input[type!="checkbox"]').each(function () {
                $(this).val('');
            });
        };

        // Manual Input Click Event
        addressToggle.on('click', function (e) {
            e.preventDefault();
            clearAddressForm();
            manualAddressMode();
        });

        // Reset Input Click Event
        $('.address-reset-form').on('click', function (e) {
            e.preventDefault();
            addressPostCodeLookup.val('').focus();
            showPostCodeLookupContainer(true);
            showAddressListWrapper(false);
            showAddressManualInput(false);
            initAddressLookup();
        });

        // Find Address Enter Click Event
        addressPostCodeLookup.keypress(function(e){
            addressFind.prop("disabled", false);
            if(e.keyCode==13) {
                addressFind.prop("disabled", true);
                e.preventDefault();
                addressFind.click();
            }
        });

        // Find Address Click Event
        addressFind.on('click', function (e) {
            $(this).prop("disabled", true);
            e.preventDefault();
            var postcode = addressPostCodeLookup.val();
            if (postcode) {
                hideAjaxErrors();
                clearAddressForm();
                getAddresses(postcode);
            } else {
                addressPostCodeLookup.focus();
            }
        });

        // Select Address Change Event
        addressesList.on('change', function () {
            var address = $(this).children(":selected").val();
            updateAddressForm(address);
            showAddressManualInput(true);
            addressManualInput.show();
        });

        initAddressLookup();
    };
    return {
        enableAddressLookup: enableAddressLookup
    }
};

var check = function(evt, id, regex) {
    var theEvent = evt || window.event;
    if ( theEvent == null || theEvent == undefined) {
      return;
    }

    var key = theEvent.keyCode || theEvent.which;

    // Don't validate the input if below arrow, delete and backspace keys were pressed
    if(key == 37 || key == 39 || key == 8 || key == 9 || key == 46) { // Left / Right , Backspace, Tab, Delete keys
        if ( String.fromCharCode( key ) != "%" && String.fromCharCode( key ) != "."
            && String.fromCharCode( key ) != "'" ) {
            return;
        }
    }

    var keyStr = String.fromCharCode( key );

    if( !regex.test(keyStr)) {
        theEvent.returnValue = false;

        if(theEvent.preventDefault)
            theEvent.preventDefault();
    }
};

var AutoFillTodaysDate = function(day, month, year, checkboxId, dayId, monthId, yearId) {
    var dod_elm = document.getElementById(checkboxId);
    var dod_day = document.getElementById(dayId);
    var dod_month = document.getElementById(monthId);
    var dod_year = document.getElementById(yearId);

    var padded_day = ('0' + day).substr(('0' + day).length - 2, 2);
    var padded_month = ('0' + month).substr(('0' + month).length - 2, 2);

    if (dod_elm && dod_elm.checked === false) {
        dod_day.value = '';
        dod_month.value = '';
        dod_year.value = '';
    } else {
        dod_day.value = padded_day;
        dod_month.value = padded_month;
        dod_year.value = year;
    }
};

var disableSubmitOnClick = function() {
    var pleaseWaitOverlay = $('.please-wait-overlay'),
        closeWaitOverlay  = $('.please-wait-overlay a'),
        submitId, submitSelector;

    /*
    $('input').on('invalid',function(e) {
        // This event handler, will handle html5 validation, and supress the validation bubble
        e.preventDefault();
        return html5Validation.validateField(this);
    });
    */

    $(':submit').on('click', function(e) {
        var pageForm = $(this).closest('form:first');
        if (pageForm === undefined || pageForm.length === 0) {
            pageForm = $('form');
        }

        //if (html5Validation.validateForm(pageForm)) {
        submitId = $(this).attr('id') || "";
        submitSelector = $('#' + submitId + '');
        if ( submitSelector.hasClass("disabled") ) {
            return false;
        }
        submitSelector.html('Loading').addClass('loading-action disabled');
        var runTimes = 0;
        setInterval(function() {
            if ( runTimes < 3 ){
                $(':submit').append('.');
                runTimes++;
            } else {
                runTimes = 0;
                $(':submit').html('Loading');
            }
        }, 1000);
        setTimeout(function() {
            pleaseWaitOverlay.toggle();
        }, 5000);
        //}
    });
    closeWaitOverlay.on('click', function(e) {
        e.preventDefault();
        pleaseWaitOverlay.toggle();
    });
};

var closingWarning = function() {
    var d = new Date(),
        h = d.getHours(),
        m = d.getMinutes(),
        currentTimeInMinutes = h * 60 + m,
        serviceClosingWarning = $('.serviceClosingWarning'),
        // Default to 18:00 as closing time
        closingTimeInMinutes = 18 * 60,
        dClosing, dCurrentTimeInMinutes, minLeft, secLeft;

    // data-closing-time attribute which will be the same as configuration file
    var dataClosingTime = $('body').attr('data-closing-time');

    if ((dataClosingTime) && ($.isNumeric(dataClosingTime))) {
        closingTimeInMinutes = parseInt(dataClosingTime);
    }

    // Warning will start at T minus 15
    var warningInMinutes = closingTimeInMinutes - 15;
    // Final warning will start at T minus 5
    var finalWarningInMinutes = closingTimeInMinutes - 5;
    // Last minute available at T minus 1
    var lastMinuteInMinutes = closingTimeInMinutes - 1;

    if (currentTimeInMinutes >= warningInMinutes && currentTimeInMinutes <= lastMinuteInMinutes) {
        var refreshTimer = setInterval(function () {
            dClosing = new Date();
            dCurrentTimeInMinutes = dClosing.getHours() * 60 + dClosing.getMinutes();
            minLeft = lastMinuteInMinutes - dCurrentTimeInMinutes;
            secLeft = 60 - dClosing.getSeconds();
            function pad(d) {
                return (d < 10) ? '0' + d.toString() : d.toString();
            }
            minLeft = pad(minLeft);
            secLeft = pad(secLeft - 1);
            $('.js-minutes-left').html(minLeft);
            $('.js-seconds-left').html(secLeft);
            if (dCurrentTimeInMinutes >= finalWarningInMinutes) {
                serviceClosingWarning.removeClass('closing-warning');
                serviceClosingWarning.addClass('final-closing-warning');
                if (dCurrentTimeInMinutes >= closingTimeInMinutes) {
                    $('.serviceClosing').hide();
                    $('.serviceClosed').show();
                    clearInterval(refreshTimer);
                }
            }
        }, 1000);
    }
};

var disableClickOnDisabledButtons = function() {
    $('.button-not-implemented').click(function() {
        return false;
    });
};

var printButton = function() {
    $('.print-button').click(function() {
        window.print();
        return false;
    });
};

var enableSmoothScroll = function() {
    $('a[href^="#"]').bind('click.smoothscroll', function (e) {
        e.preventDefault();
        var target = this.hash,
            $target = $(target);
        if ($target.length) {
            $('html, body').animate({
                scrollTop: $(target).offset().top - 40
            }, 750, 'swing', function () {
                window.location.hash = target;
            });
        }
    });
};

var feedbackFormCharacterCountdown = function() {
    if ($("#feedback-form textarea").length > 0) {
        function updateCountdown() {
            // 500 is the max message length
            var remaining = 500 - $(this).val().length;
            var target = $(this).data('target');
            if (target) {
              $(target).text(remaining);
            }
        }
        $(document).ready(function($) {
            // IE 9- maxlength on input textarea
            var txts = document.getElementsByTagName('TEXTAREA')
            for(var i = 0, l = txts.length; i < l; i++) {
                if(/^[0-9]+$/.test(txts[i].getAttribute("maxlength"))) {
                    updateCountdown.call(txts[i]);
                    var func = function() {
                        var len = parseInt(this.getAttribute("maxlength"), 10);

                        if (this.value.length > len) {
                            this.value = this.value.substr(0, len);
                            return false;
                        }
                    };
                    txts[i].onkeyup = func;
                    txts[i].onblur = func;
                }
            }
            // Update Countdown on input textarea
            $('#feedback-form textarea').change(updateCountdown);
            $('#feedback-form textarea').keyup(updateCountdown);
        });
    }
};

var elementToggle = function() {
    $('.toggle-element').each(function() {
        // Trigger is the item that is usually clicked, while target is the element to show / hide
        var trigger = this;
        var target = $(trigger).data('target') || $(this).siblings('.toggle-target');
        var animationSpeed = 100;
        if (target) {
            if (trigger.tagName.toLowerCase() === 'input' && $(trigger).attr('type') === 'radio') {
                $(trigger).on('click', function(e) {
                    // Do we show / hide the target
                    // This prevents unwanted toggle behaviour i.e. if a user double-clicks the trigger
                    if ($(trigger).hasClass('option-visible') !== $(target).is(':visible')) {
                        $(target).toggle(animationSpeed);
                        // Disable / Enable the optional fields, needed to managed HTML5 validation
                        $(target).find('input').attr('disabled', $(trigger).hasClass('option-visible') === false);
                    }
                });
                // If the option-visible is selected on-load then we should show the optional-areas
                if ($(trigger).hasClass('option-visible') && $(trigger).is(':checked')) {
                    $(target).find('input').attr('disabled', false); // Enable the fields for HTML5 validation
                    $(target).show();
                }
            } else {
                $(trigger).on('click', function(e){
                    e.preventDefault();
                    $(trigger).toggleClass('active');
                    $(target).toggle(animationSpeed);
                });
            }
        }
    });
}

// TODO: remove it if unused
var areCookiesEnabled = function() {
    var cookieEnabled = (navigator.cookieEnabled) ? true : false;

    if (typeof navigator.cookieEnabled == "undefined" && !cookieEnabled)
    {
        document.cookie="testcookie";
        cookieEnabled = (document.cookie.indexOf("testcookie") != -1) ? true : false;
    }
    return (cookieEnabled);
};

var formCheckedSelection = function() {
    var label = $('label.form-radio.selectable, label.form-checkbox.selectable');

    label.each(function() {
        var input = $(this).children('input:radio, input:checkbox');
        input.on('change', function() {
            if(input.is(':checked')) {
                $('label').removeClass('selected');
                $(this).parent('label').addClass('selected');
                input.parent(label).addClass('selected');
            }
            if(!input.is(':checked')) {
                input.parent(label).removeClass('selected');
            }
        });
        if ($(input).is(':checked')) {
            var wrapper = $(this).closest('.title-radio-wrapper');
            var e = jQuery.Event("click");
            e.target = input;
            toggleRadioContent.call(wrapper, e);
        }
    });
};

var preventPasteOnEmailConfirm = function() {
    $('.js-email-confirm').bind("paste",function(e) {
        e.preventDefault();
    });
};

var toggleRadioContent = function(e) {
    if ($(e.target).attr('type') && $(e.target).attr('type') === "radio") {
        var otherGroup = $(this).siblings('.associated-input-wrapper');
        var otherTriggerItem = $(this).find('input[type=radio]').last().attr('id');

        if ($(e.target).attr('id') === otherTriggerItem){
            $(otherGroup).show(100);
            $(otherGroup).find('.form-item').addClass('item-visible');
        } else {
            $(otherGroup).hide(100);
            $(otherGroup).find('.form-item').removeClass('item-visible');
            $(otherGroup).find('input').val('');
        }
    }
};

var showRadioContentOnSelected = function() {
    $('.title-radio-wrapper').on('click', toggleRadioContent);
};

var showHelpContentOnSelected = function() {
    $('.show-on-selected').each(function(i) {
        var parentGroup = $(this).closest('.form-item'),
            parentAssociatedContent = $(parentGroup).find('.show-on-selected');

        $(parentGroup).on('click',function(e) {
            if ($(e.target).attr('type') && $(e.target).attr('type') === 'radio') {
                var associatedHelpContent = $(e.target).parent().next('.show-on-selected');

                if ($(e.target).is(':checked') && associatedHelpContent.length) {
                    $(parentAssociatedContent).not(associatedHelpContent).hide(100);
                    $(associatedHelpContent).show(100);
                } else {
                    // Hide everything
                    $(parentAssociatedContent).hide(100);
                }
            }
        });
    });
}


var gaTrackEvent = function(category, action, label, value) {
    // Helper method to support GA asynchronous and analytics.js - should always report an event depending on the
    // version of GA used in a project
    if (typeof ga !== 'undefined') {
        ga('send', 'event', category, action, label, value)
    } else if (typeof console !== 'undefined') {
        console.log("GA event tracking not available");
    }
}

var gaTrackClickOnce = function() {
    var gaTrackClickEvent = 'ga-track-click-event-once';
    $('.' + gaTrackClickEvent).on('click', function(e) {
        var category = $(this).data('ga-event-category') || document.location.href;
        var action = $(this).data('ga-event-action');
        if ($(this).hasClass(gaTrackClickEvent) && category && action) {
            gaTrackEvent(category, action, 'click', 1);
            $(this).removeClass(gaTrackClickEvent);
        }
    });
};

// tracks an event based on a field that has a value. e.g. a textfield.
var gaTrackOptionalFields = function() {
    $('button[type="submit"]').on('click', function(e) {
        var form = $(this).closest('form');

        $(form).find('.ga-track-value').map(function() {
            var inputType = $(this).attr('type'),
                actionName = $(this).data('ga-action') || 'Not set',
                category = $(this).data('ga-category') || 'optional_field'
                label = $(this).data('ga-label') || $(this).attr('value'),
                value = parseInt($(this).data('ga-value')) || 1;

            if (inputType ==='checkbox' || inputType === 'radio') {
                // we only want to track these input types if they have been checked/selected
                if ($(this).is(':checked')) {
                    gaTrackEvent(category, actionName, label, value);
                }
            } else if ($(this).hasClass('ga-track-region')) {
                // Special case to track the geo region from a particular selected address
                var areaCode = $(this).val().split(', ').splice(-1)[0].substr(0,2);
                if (areaCode) {
                    var regionLookup = {"BT": "Northern Ireland"};
                    gaTrackEvent(category, actionName, ((regionLookup[areaCode])?regionLookup[areaCode]:'Other'), value)
                }
            } else {
                gaTrackEvent(category, actionName, label, value);
            }
        });

        $(form).find('.ga-track-optional-text').map(function() {
            var actionName = $(this).data('ga-action'),
                value = parseInt($(this).data('ga-value')) || 1.
                isProvided = false;

            if (this.tagName.toLowerCase() !== "input") {
                // All input fields within container must have a value for us to report it as present
                isProvided = true;
                $(this).find('input').map(function() {
                    return ($(this).val().length > 0);
                }).each(function() {
                    if (this.valueOf() === false) {
                        isProvided = false;
                    }
                });
            } else {
                isProvided = ($(this).val().length !== 0);
            }

            gaTrackEvent("optional_field", actionName, (isProvided)?'provided':'absent', value);
        });
    });
};

disableSubmitOnClick();
closingWarning();
disableClickOnDisabledButtons();
var lookup = addressLookup();
lookup.enableAddressLookup();
printButton();
elementToggle();
enableSmoothScroll();
feedbackFormCharacterCountdown();
formCheckedSelection();
showRadioContentOnSelected();
showHelpContentOnSelected();
preventPasteOnEmailConfirm();
gaTrackClickOnce();
gaTrackOptionalFields();
preventPasteOnEmailConfirm();

//html5 autofocus fallback for browsers that do not support it natively
//if form element autofocus is not active, autofocus
$('[autofocus]:not(:focus)').eq(0).focus();

// Disabled clicking on disabled buttons
$('.button-not-implemented').click(function() {
    return false;
});

$("#tryagain").click(function() {
    if($(this).hasClass("disabled")) return false;
    $(this).addClass("disabled");
    return true;
});

//Set an empty function to be called on window.onunload so that Javascript runs when user returns to this page
//using the back button. This just prevents Firefox from caching the page in the Back-Forward Cache
//https://stackoverflow.com/questions/2638292/after-travelling-back-in-firefox-history-javascript-wont-run
window.onunload = function(){};

// Last command adds a class, so we have a condition to wait for when unit-testing
// Also used in the web-apps as a check that all javascript executed
$('html').addClass('js-ready');
