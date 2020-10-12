package one.xingyi.cddReq

import one.xingyi.cddscenario.{CannotAddScenarioBecauseClashes, Scenario, Specification, UseCase1}
import one.xingyi.core.UtilsSpec
import scala.language.reflectiveCalls
class ReqEngineTest extends UtilsSpec {
  type SpecTestUseCase1 = UseCase1[String, String]

  val ucOk = new SpecTestUseCase1("threeScenariosEachSpecific") {
    scenario("1") produces "one" when (_ == "1")
    scenario("2") produces "two" when (_ == "2")
    scenario("3") produces "three" when (_ == "3")
  }
  val ucOverlaps = new SpecTestUseCase1("threeScenariosEachSpecific") {
    val s4: Scenario[String, String] = scenario("4") produces "fourToFive" when (_.toInt > 3)
    val s5: Scenario[String, String] = scenario("5") produces "five" when (_ == "5")
  }
  val s = Specification.fromUsecase1(ucOk)
  val engineOk = ReqEngine(s)
  val engineOverlaps = ReqEngine(ucOk or ucOverlaps)

  val definedAt = "(ReqEngineTest.scala:19)"

  behavior of classOf[ReqEngine[_, _]].getSimpleName

  it should "have a definedInSourceCode at" in {
    engineOk.definedInSourceCodeAt.toString shouldBe definedAt
  }

  it should "implement a  function which is the 'orElse' of the passed in scenarios" in {
    engineOk("1") shouldBe "one"
    engineOk("2") shouldBe "two"
    engineOk("3") shouldBe "three"
    the[ReqEngineException[_, _]] thrownBy (engineOk("4")) should have message (s"ReqEngine defined at $definedAt, cannot process [4]")
  }

  it should "have an 'isDefinedAt'" in {
    engineOk.isDefinedAt("0") shouldBe false
    engineOk.isDefinedAt("1") shouldBe true
    engineOk.isDefinedAt("2") shouldBe true
    engineOk.isDefinedAt("3") shouldBe true
    engineOk.isDefinedAt("4") shouldBe false
  }

  it should "validate OK if scenarios all come to correct value" in {
    engineOk.validate shouldBe List()
  }
  it should "fail validation if scenarios don't work" in {
    val List(v: CannotAddScenarioBecauseClashes[ReqEngine, String, String]) = engineOverlaps.validate
    v.scenario shouldBe ucOverlaps.s5
    v.clashesWith shouldBe List(ucOverlaps.s4)
    v.node shouldBe engineOverlaps
  }

  it should "throw exceptions if needed " in {
    engineOk.validateAsException
    try {
      engineOverlaps.validateAsException
      fail
    } catch {case e: CannotAddScenarioBecauseClashes[_, _, _] =>}
  }

}
