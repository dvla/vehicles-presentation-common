require(['../lib/vehicles-presentation-common/javascripts/config'], function() {
    require(["page-init"], function(pageInit) {
        pageInit.initAll();

        pageInit.hideEmailOnOther('#title_titleOption_4', '.form-item #title_titleText');
    });
});
