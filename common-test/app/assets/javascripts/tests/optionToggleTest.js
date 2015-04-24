require(["jquery", "qunit"], function($, qunit){

    qunit.test( "Hide option toggles on load", function( assert ) {
        ok(!$('.optional-field').is(':visible'), "Did not hide the invisible elements on load");
    });

    qunit.test( "Show/hide option on clicking yes/no", function( assert ) {
        var done = assert.async();

        var stringOptionDiv = $("#optional-string-option")

        stringOptionDiv.find(".option-visible").click();

        stringOptionDiv.find(".optional-field").promise().always(function() {
            ok($(this).is(":visible"));

            stringOptionDiv.find(".option-invisible").click();
            stringOptionDiv.find(".optional-field").promise().always(function() {
                ok(!$(this).is(":visible"));
                done();
            });
        });
    });

    $(function(){
        qunit.load();
        qunit.start();
    });
});
