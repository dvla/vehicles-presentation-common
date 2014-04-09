package helpers.disposal_of_vehicle

import models.domain.disposal_of_vehicle.AddressViewModel
import services.fakes.FakeWebServiceImpl.traderUprnValid
import services.fakes.FakeVehicleLookupWebService._
import scala.annotation.tailrec

object Helper {
  val traderBusinessNameValid = "example trader name"
  val traderaddressValid = "1"

  val vehicleLookupKey = referenceNumberValid + "." + registrationNumberValid

  val consentValid = "true"
  val mileageValid = "20000"
  val emailValid = "viv.richards@emailprovider.co.uk"

  def countSubstring(str1:String, str2:String):Int={
    @tailrec def count(pos:Int, c:Int):Int={
      val idx=str1 indexOf(str2, pos)
      if(idx == -1) c else count(idx+str2.size, c+1)
    }
    count(0,0)
  }

}
