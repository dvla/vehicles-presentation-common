define(['jquery'], function($) {
   // Address Lookup
    var enableAddressLookup = function() {
        // Quick Access Variables Declaration
        var addresses = [],
            // Address inputs
            addressPostCodeLookup = $('#address-postcode-lookup'),
            addressToggle = $('.address-manual-toggle'),
            addressManualInput = $('.address-manual-inputs-wrapper'),
            addressLookupStatus = addressManualInput.attr('data-address-status'),
            addressFind = $('#address-find'),
            // Addresses list
            addressListWrapper = $('.address-list-wrapper'),
            addressesList = $("#address-list"),
            // Errors
            postCodeError = $('.missing-postcode'),
            serverMessage = $('.server-message');

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
        // AJAX GET
        var getAddresses = function (postcode) {
            url = '/address-lookup/postcode/' + postcode;
            addressesList.html('<option value="">Please select</option>');
            var data = [];
            $.ajax({
                type: "GET",
                url: url,
                cache: false,
                success: function (data) {
                    if (data.length) {
                        addresses = data;
                        var address = "";
                        for (var i = 0; i < data.length; i++) {
                            address = "<option value='" + i + "'>" + data[i].streetAddress1 + "," + data[i].streetAddress2 + "," + data[i].streetAddress3 + "," + data[i].postTown + "," + data[i].postCode + "</option>";
                            if (address != "") {
                                addressesList.append(address);
                            }
                        }
                        showAddresses();
                        hideAjaxErrors();
                        addressesList.attr('data-ajax', true);

                    } else {
                        hideAjaxErrors();
                        postCodeError.show();
                        addressListWrapper.hide();
                        addressManualInput.hide();
                    }
                    addressesList.focus();
                },
                error: function (data) {
                    hideAjaxErrors();
                    addressesList.attr('data-ajax', false);
                    addressListWrapper.hide();
                    addressManualInput.hide();
                    serverMessage.show();
                    $('.server-message span').html(data.responseText);
                }
            });

        };
        // Populate Form
        var updateAddressForm = function (address) {
            var selected_address = addressesList.children(":selected").val();
            $('#address-picker-1_address-line-1').val(addresses[selected_address].streetAddress1);
            $('#address-picker-1_address-line-2').val(addresses[selected_address].streetAddress2);
            $('#address-picker-1_address-line-3').val(addresses[selected_address].streetAddress3);
            $('#address-picker-1_post-town').val(addresses[selected_address].postTown);
            $('#address-picker-1_post-code').val(addresses[selected_address].postCode);
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
        // Find Address Click Event
        addressFind.on('click', function (e) {
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
