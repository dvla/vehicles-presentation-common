package uk.gov.dvla.vehicles.presentation.common.composition

import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WithDefaultApplication

trait WithTestApplication extends WithDefaultApplication with TestGlobalCreator
