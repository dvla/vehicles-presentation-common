require.config({
    paths: {
        'jquery' : '../lib/jquery/jquery',
        'page-init': '../lib/vehicles-presentation-common/javascripts/page-init',
        'global-helpers': '../lib/vehicles-presentation-common/javascripts/global-helpers'
    }
});

require(["page-init", "global-helpers"], function(pageInit) {
    pageInit.initAll();
});
