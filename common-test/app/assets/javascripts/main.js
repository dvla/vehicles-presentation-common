require.config({
    paths: {
        'jquery': '../lib/vehicles-presentation-common/javascripts',
        'jquery-migrate': '../lib/vehicles-presentation-common/javascripts',
        'header-footer-only': 'header-footer-only',
        'form-checked-selection': 'form-checked-selection',
        'page-init': '../lib/vehicles-presentation-common/javascripts/page-init',
        'global-helpers': '../lib/vehicles-presentation-common/javascripts/global-helpers'
    }
});

require(
    ["jquery", "jquery-migrate", "header-footer-only", "form-checked-selection", "page-init", "global-helpers"],
    function($, jqueryMigrate, headerFooterOnly, formCheckedSelection, pageInit) {
        pageInit.initAll()
    }
);
