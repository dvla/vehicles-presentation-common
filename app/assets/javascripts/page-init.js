define(function(require) {
    var $ = require('jquery'),
        addressLookup = require('address-picker'),
        autofillTodaysDate = require('autofill-todays-date');

    var disableSubmitOnClick = function() {
        var pleaseWaitOverlay = $('.please-wait-overlay'),
            closeWaitOverlay  = $('.please-wait-overlay a'),
            submitId, submitSelector;

        $(':submit').on('click', function(e) {
            //e.preventDefault();
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

        if (currentTimeInMinutes >= warningInMinutes && currentTimeInMinutes <= lastMinuteInMinutes) {            var refreshTimer = setInterval(function () {
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

    var openFeedback = function(inputId, event) {
        var element = document.getElementById(inputId);
        if (element) {
            if (element.addEventListener) {
                // addEventListener is a W3 standard that is implemented in the majority of other browsers (FF, Webkit, Opera, IE9+)
                element.addEventListener(event, function (e) {
                    console.log("openFeedback addEventListener id: " + inputId + ", event " + event);
                    //window.open(url,'_blank');
                    window.open(this.href, '_blank');
                    e.preventDefault();
                });
            } else if (element.attachEvent) {
                // attachEvent can only be used on older trident rendering engines ( IE5+ IE5-8*)
                element.attachEvent(event, function (e) {
                    // console.log("openFeedback addEventListener id: " + inputId + ", event " + event);
                    //window.open(url,'_blank');
                    window.open(this.href, '_blank');
                    e.preventDefault();
                });
            } else {
                console.error("element does not support addEventListener or attachEvent");
                return false;
            }
        } else {
            console.error("element id: " + inputId + " not found on page");
            return false;
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
            $('html, body').animate({
                scrollTop: $(target).offset().top - 40
            }, 750, 'swing', function () {
                window.location.hash = target;
            });
        });
    };

    var feedbackFormCharacterCountdown = function() {
        if ($("#feedback-form textarea").length > 0) {
            function updateCountdown() {
                // 500 is the max message length
                var remaining = 500 - $('#feedback-form textarea').val().length;
                $('.character-countdown').text(remaining);
            }
            $(document).ready(function($) {
                // IE 9- maxlength on input textarea
                var txts = document.getElementsByTagName('TEXTAREA')
                for(var i = 0, l = txts.length; i < l; i++) {
                    if(/^[0-9]+$/.test(txts[i].getAttribute("maxlength"))) {
                        var func = function() {
                            var len = parseInt(this.getAttribute("maxlength"), 10);

                            if(this.value.length > len) {
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

    var tooltipToggle = function() {
        $('.field-help').addClass('enable');
        $('.field-help').on('click', function(e) {
            var helpContent = $('.field-help-content[data-tooltip="' + $(this).attr('data-tooltip') +'"]');
            // Since its an anchor whose href is a fragment to the field, we prevent browser from scrolling to it
            e.preventDefault();
            if (helpContent) {
                if ($(helpContent).is(':visible')) {
                    $(helpContent).hide(100);
                    $(this).find('.field-help-close').hide();
                } else {
                    var me = this;
                    $(helpContent).show(100, function() {
                        $(me).find('.field-help-close').show();
                    });
                }
            }
        });
    };

    var enableOptionToggle = function() {
        $('.optional-field').hide();

        $('.expandable-optional .option-visible').on('click', function() {
            $(this).closest('.expandable-optional').find('.optional-field').show(100);
        });
        $('.expandable-optional .option-invisible').on('click', function() {
            $(this).closest('.expandable-optional').find('.optional-field').hide(100);
        });

        $('.expandable-optional .option-visible:checked').click();
    };

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
        });
    };

    var hideEmailOnOther = function(radioOtherId, emailId) {
        if (!radioOtherId.length || !emailId.length) {
            return;
        }

        var checkStateOfRadio = function(radioOtherId, emailId) {
            if(!$(radioOtherId).attr('checked')) {
                $(emailId).parent().hide().removeClass('item-visible');
                $(emailId).val('');
            } else {
                $(emailId).parent().show().addClass('item-visible');
            }
        };

        checkStateOfRadio(radioOtherId, emailId);

        $("input:radio" ).click(function() {
            checkStateOfRadio(radioOtherId, emailId);
        });
    };

    var imageHintToggles = function() {
        $('.hint-image-wrap > .panel-indent-wrapper').hide();

        $('.hint-image-wrap > p').on('click', function() {
            $(this).siblings().toggle();
        });
    };

    var preventPasteOnEmailConfirm = function() {
        $('.js-email-confirm').bind("paste",function(e) {
            e.preventDefault();
        });
    };

    var gaTrackEvent = function(category, action, label, value) {
        // Helper method to support GA asynchronous and analytics.js - should always report an event depending on the
        // version of GA used in a project
        if (typeof ga !== 'undefined') {
            ga('send', 'event', category, action, label, value)
        } else if (typeof _gaq !== 'undefined') {
            _gaq.push(['_trackEvent', category, action, label, value]);
        } else {
            console.log("GA event tracking not available");
        }
    }

    var gaTrackClickOnce = function() {
        var gaTrackClickEvent = 'ga-track-click-event-once';
        $('.' + gaTrackClickEvent).on('click', function(e) {
            var category = $(this).attr('ga-event-category') || document.location.href;
            var action = $(this).attr('ga-event-action');
            if ($(this).hasClass(gaTrackClickEvent) && category && action) {
                gaTrackEvent(category, action, 'click', 1);
                $(this).removeClass(gaTrackClickEvent);
            }
        });
    };

    // tracks an event based on a field that has a value. e.g. a textfield.
    var gaTrackOptionalFields = function() {
        $('button[type="submit"]').on('click', function(e) {
            $(this).closest('form').find('.ga-track-optional-text').map(function() {
                var value = $(this).attr('ga-value');
                if (!value) value = 1;
                var actionName = $(this).attr('ga-action');
                if(!$(this).val()) {
                    gaTrackEvent("optional_field", actionName, 'absent', value);
                } else {
                    gaTrackEvent("optional_field", actionName, 'provided', value);
                }
            });
        });
    };

    return {
        disableSubmitOnClick: disableSubmitOnClick,
        closingWarning: closingWarning,
        openFeedback: openFeedback,
        imageHintToggles: imageHintToggles,
        disableClickOnDisabledButtons: disableClickOnDisabledButtons,
        printButton: printButton,
        tooltipToggle: tooltipToggle,
        enableSmoothScroll: enableSmoothScroll,
        feedbackFormCharacterCountdown: feedbackFormCharacterCountdown,
        enableOptionToggle: enableOptionToggle,
        formCheckedSelection: formCheckedSelection,
        hideEmailOnOther: hideEmailOnOther, // Do not call this from initAll because only some exemplars need it
        preventPasteOnEmailConfirm: preventPasteOnEmailConfirm,
        gaTrackClickOnce: gaTrackClickOnce,
        gaTrackOptionalFields: gaTrackOptionalFields,
        initAll: function() {
            $(function() {
                disableSubmitOnClick();
                closingWarning();
                imageHintToggles();
                disableClickOnDisabledButtons();
                printButton();
                tooltipToggle();
                enableSmoothScroll();
                feedbackFormCharacterCountdown();
                enableOptionToggle();
                formCheckedSelection();
                preventPasteOnEmailConfirm();
                gaTrackClickOnce();
                gaTrackOptionalFields();

                if ($('#feedback-open').length) {
                    openFeedback('feedback-open', 'click');
                }

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
                addressLookup.enableAddressLookup()
            });
        }
    };
});
