package uk.gov.dvla.vehicles.presentation.common.views.constraints

import play.api.data.validation.{ValidationError, Invalid, Valid}
import uk.gov.dvla.vehicles.presentation.common.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.views.constraints.Email.emailAddress

final class EmailUnitSpec extends UnitSpec {

  /**
   * Test valid email formats
   */
  val validEmails = Seq("test@io", "test@iana.org", "test@nominet.org.uk", "ttest@about.museum", "a@iana.org", "test@e.com",
    "test@iana.a", "test.test@iana.org", "!#$%&`*+/=?^`{|}~@iana.org", "123@iana.org", "test@123.com", "test@iana.123",
    "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghiklm@iana.org",
    "test@abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghikl.com", "test@mason-dixon.com", "test@c--n.com",
    "test@iana.co-uk",
    "a@a.b.c.d.e.f.g.h.i.j.k.l.m.n.o.p.q.r.s.t.u.v.w.x.y.z.a.b.c.d.e.f.g.h.i.j.k.l.m.n.o.p.q.r.s.t.u.v.w.x.y.z.a.b.c.d.e.f.g.h.i.j.k.l.m.n.o.p.q.r.s.t.u.v.w.x.y.z.a.b.c.d.e.f.g.h.i.j.k.l.m.n.o.p.q.r.s.t.u.v.w.x.y.z.a.b.c.d.e.f.g.h.i.j.k.l.m.n.o.p.q.r.s.t.u.v",
    "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghiklm@abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghikl.abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghikl.abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghi",
    "\"test\"@iana.org", "\"\\a\"@iana.org", "\"\\\"\"@iana.org", "xn--test@iana.org")
  validEmails.map(name => "indicate the email is valid: " + name in {
    val result = emailAddress(name)
    result should equal(Valid)
  })  
  
  /**
   * Test invalid email formats
   */
  val invalidEmails = Seq("test", "@", "test@", "@io", "@iana.org", ".test@iana.org", "test.@iana.org", "test..iana.org",
    "test_exa-mple.com", "test\\@test@iana.org", "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghiklmn@iana.org",
    "test@abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghiklm.com", "test@-iana.org", "test@iana-.com",
    "test@.iana.org", "test@iana.org.", "test@iana..com",
    "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghiklm@abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghikl.abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghikl.abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghij",
    "a@abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghikl.abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghikl.abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghikl.abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefg.hij",
    "a@abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghikl.abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghikl.abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghikl.abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefg.hijk",
    "\"\"\"@iana.org", "test\"@iana.org", "\"test@iana.org", "\"test\"test@iana.org", "test\"text\"@iana.org",
    "\"test\"\"test\"@iana.org", "\"test\".\"test\"@iana.org", "\"test\".test@iana.org", "test@iana.org-", "(test@iana.org",
    "test@(iana.org", "\"test\\\"@iana.org", "test@.org", "test@iana/icann.org")
  invalidEmails.map(name => "indicate the email is invalid: " + name in {
    val result = emailAddress(name)
    result should equal(Invalid(ValidationError("error.email")))
  })
}
