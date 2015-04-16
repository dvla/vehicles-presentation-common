require(["jquery", "qunit"], function($, qunit){
    qunit.test( "Hide option toggles on load", function( assert ) {
        ok(!$('.optional-field').is(':visible'), "Did not hide the invisible elements");
    });

    qunit.test( "Show/hide option on clicking yes/no", function( assert ) {
        $("#optional-string-option_visible").click();
        ok($('#optional-string-option-option-field').is(':visible'), "Did not show the option on clicking yes");
        $("#optional-string-option_invisible").click();
        ok($('#optional-string-option-option-field').is(':visible'), "Did not hide the option on clicking no");
        ok(false)
    });

    $(function(){
        qunit.load();
        qunit.start();
    });

});
