package csrfprevention.filters

import com.google.inject.Inject
import play.api.mvc.{EssentialAction, EssentialFilter}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory

class CsrfPreventionFilter @Inject()
                           (implicit clientSideSessionFactory: ClientSideSessionFactory) extends EssentialFilter {

  def apply(next: EssentialAction): EssentialAction = new CsrfPreventionAction(next)
}