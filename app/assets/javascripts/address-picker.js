
define(['jquery'], function($) {

   // Address Lookup
    var enableAddressLookup = function() {
        // Quick Access Variables Declaration
        var addresses = [],
            addressesList = $("#address-list"),
            addressToggle = $('.address-manual-toggle'),
            addressLookupStatus = $('.address-manual-inputs-wrapper').attr('data-address-status');
        // Initialization
        var initAddressLookup = function () {
            // if backend returns with an error, on document ready the form will be displayed
            $('.no-js-hidden').show();
            if (addressLookupStatus == '') {
                $('.address-list-wrapper, .address-manual-inputs-wrapper').hide();
            }
        };
        // Manual Input Mode
        var manualAddressMode = function () {
            $('#addressLines-1').focus();
            addressToggle.hide();
            $('.address-manual-inputs-wrapper').show();
            $('.address-list-wrapper, #address-postcode-lookup, #address-find').hide();
            $("label[for='address-postcode-lookup']").hide();
        };
        // Populate Addresses List
        var showAddresses = function () {
            $('.address-list-wrapper, .address-manual-inputs-wrapper').show();
        };
        // AJAX GET
        var getAddresses = function (postcode) {
            url = '/setup-business-details/postcode/' + postcode;
            addressesList.html('<option value="">Please select</option>');
            $.ajax({
                type: "GET",
                url: url,
                cache: false,
                success: function (data) {
                    if (data) {
                        var len = data.length,
                            address = "";
                        // Assigning addresses object to a global scope
                        addresses = data;
                        for (var i = 0; i < len; i++) {
                            address = "<option value='" + i + "'>" + data[i].street + "," + data[i].town + "," + data[i].county + "," + data[i].postcode + "</option>";
                            if (address != "") {
                                addressesList.append(address);
                            }
                        }
                        showAddresses();
                        //TODO: if postcode not found -> notify user to enter it manually
                    }
                    addressesList.focus();
                },
                error: function (data) {
                    // TODO: add an error class to the input
                }
            });

        };
        // Populate Form
        var updateAddressForm = function (address) {
            var selected_address = addressesList.children(":selected").val();
            $('#addressLines-1').val(addresses[selected_address].street);
            $('#address-town').val(addresses[selected_address].town);
            $('#address-county').val(addresses[selected_address].county);
            $('#address-postcode').val(addresses[selected_address].postcode);
        };
        // Reset Form
        var clearAddressForm = function () {
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
            $('#address-postcode-lookup').val('').focus();
            initAddressLookup();
            // TODO: reset cookies
        });

        /* TODO: if cookies have already an address -> display form prefilled  */

        // Find Address Click Event
        $('#address-find').on('click', function (e) {
            e.preventDefault();
            var postcode = $('#address-postcode-lookup').val();
            if (postcode) {
                clearAddressForm();
                getAddresses(postcode);
            } else {
                $('#address-postcode-lookup').focus();
            }
        });
        // Select Address Change Event
        addressesList.on('change', function () {
            var address = $(this).children(":selected").val();
            updateAddressForm(address);
        });
        initAddressLookup();
    };

    return {
        enableAddressLookup: enableAddressLookup
    }
//    addressLookup();
    // end of Address Lookup
});