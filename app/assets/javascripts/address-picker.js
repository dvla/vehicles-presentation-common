define(['jquery'], function($) {
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
});
