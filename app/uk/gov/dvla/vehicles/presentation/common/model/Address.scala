package uk.gov.dvla.vehicles.presentation.common.model

case class SearchFields(showSearchFields: Boolean,
                        showAddressSelect: Boolean,
                        showAddressFields: Boolean,
                        postCode: Option[String],
                        listOption: Option[String],
                        remember: Boolean)

case class Address(searchFields: SearchFields,
                   streetAddress1: String,
                   streetAddress2: Option[String],
                   streetAddress3: Option[String],
                   postTown: String,
                   postCode: String)