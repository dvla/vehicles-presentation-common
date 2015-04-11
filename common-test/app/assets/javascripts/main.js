require.config({
    paths: {
        'jquery' : '../lib/jquery/jquery',
        'jquery-migrate': '../lib/jquery-migrate/jquery-migrate',
        'global-helpers': '../lib/vehicles-presentation-common/javascripts/global-helpers',
        'header-footer-only': '../lib/vehicles-presentation-common/javascripts/header-footer-only',
        'page-init': '../lib/vehicles-presentation-common/javascripts/page-init'
    }
});

require(["page-init"], function(pageInit) {
    pageInit.initAll();
});
