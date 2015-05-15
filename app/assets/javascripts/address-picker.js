define(['jquery'], function($) {
   // Address Lookup
    var enableAddressLookup = function() {
        // Quick Access Variables Declaration
        var addresses = [],
            // Address inputs
            addressPostCodeLookup = $('#address-postcode-lookup'),
            addressToggle         = $('.address-manual-toggle'),
            addressManualInput    = $('.address-manual-inputs-wrapper'),
            addressLookupStatus   = addressManualInput.attr('data-address-status'),
            addressFind           = $('#address-find'),
            // AJAX api URL
            urlApi                = "/address-lookup/postcode/",
            // Addresses list
            addressListWrapper    = $('.address-list-wrapper'),
            addressesList         = $("#address-list"),
            addressesListHint     = addressesList.attr("data-select-hint"),

            // Errors
            postCodeError         = $('.missing-postcode'),
            serverMessage         = $('.server-message');

        // Initialization
        var initAddressLookup = function () {
            // if backend returns with an error, on document ready the form will be displayed
            $('.no-js-hidden').show();
            if (addressLookupStatus == '') {
                addressListWrapper.hide();
                addressManualInput.hide();
            }
        };

        // Manual Input Mode
        var manualAddressMode = function () {
            hideAjaxErrors();
            $('#address-picker-1_address-line-1').focus();
            addressToggle.hide();
            addressManualInput.show();
            addressListWrapper.hide();
            addressPostCodeLookup.hide();
            addressFind.hide();
            $("label[for='address-postcode-lookup']").hide();
        };

        // Populate Addresses List
        var showAddresses = function () {
            addressListWrapper.show();
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
            addressListWrapper.hide();
            addressManualInput.hide();
            serverMessage.show();
            $('.server-message span').html(' ' + data.responseText);
        };

        // AJAX postcode not existing
        var postcodeNotFoundAjax = function() {
            hideAjaxErrors();
            addressFind.prop("disabled", false);
            postCodeError.show();
            addressListWrapper.hide();
            addressManualInput.hide();
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
                cache: false,
                success: function (data) {
                    if (data.length) {
                        postcodeFoundAjax(data);
                    } else {
                        postcodeNotFoundAjax();
                    }
                    addressesList.focus();
                },
                error: function (data) {
                    errorAjax(data);
                }
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
            $('.address-manual-inputs-wrapper input').each(function () {
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
            addressManualInput.show();
        });

        initAddressLookup();
    };
    return {
        enableAddressLookup: enableAddressLookup
    }
});
