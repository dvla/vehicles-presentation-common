require(["jquery", "qunit", "wait"], function($, qunit, waitFor){

    qunit.test( "Lookup container is visible on load", function( assert ) {
        var done = assert.async();
        waitFor(
            function(){return $('#address-picker-1 .postcode-lookup-container').is(':visible');},
            function(){
                ok(true);
                done();
            },
            1000,
            assert
        )
    });

    qunit.test( "Dropdown select is invisible on load", function( assert ) {
        var done = assert.async();
        waitFor(
            function(){return !$('#address-picker-1 .address-list-wrapper').is(':visible');},
            function(){
                ok(true);
                done();
            },
            1000,
            assert
        )
    });

    qunit.test( "Manual address elements are invisible on load", function( assert ) {
        var done = assert.async();
        waitFor(
            function(){return !$('#address-picker-1 .address-manual-inputs-wrapper').is(':visible');},
            function(){
                ok(true);
                done();
            },
            1000,
            assert
        )
    });

//    qunit.test( "Manual address entry visibility upon pressing manual address entry", function( assert ) {
//        var done = assert.async();
//        onReadyToTest(function() {
//            $('#address-picker-1 .address-manual-toggle').click();
//            $('#address-picker-1 .postcode-lookup-container').promise().always(function() {
//              ok(!$(this).is(':visible'));
//              $('#address-picker- .address-reset-form').click();
//              done();
//
////
//            });
//            $('#address-picker-1 .postcode-lookup-container').promise().always(function() {
//              ok(!$(this).is(':visible'));
//              done();
//
//              $('#address-picker- .address-reset-form').click();
//            });
//
//        }, assert);
//    });



    var onReadyToTest = function(callback, assert) {
        waitFor(
            function(){return $('#address-picker-1 .postcode-lookup-container').is(':visible');},
            callback,
            1000,
            assert
        );
    }


    $(function(){
        qunit.load();
        qunit.start();
    });
});
