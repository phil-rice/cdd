package one.xingyi.cddscenario

import one.xingyi.core.UtilsSpec
import scala.language.reflectiveCalls

class SpecificationSpec extends UtilsSpec {

  behavior of classOf[Specification[_, _]].getSimpleName
  type SpecTestUseCase1 = UseCase1[String, String]
  type SpecTestUseCase2 = UseCase2[String, String, String]

  it should "allow use cases and scenarios to be specified for Spec1" in {
    val uc1 = new SpecTestUseCase1("UseCase 1") {
      val s1: Scenario[String, String] = scenario("input1") produces "expected one"
    }
    val s = Specification.fromUsecase1(uc1)

    val List(s1: Scenario[String, String]) = s.scenarios
    s1 shouldBe uc1.s1
    s1.situation shouldBe "input1"
    s1.result shouldBe Some("expected one")
    s1.assertions shouldBe List()
    s1.logic shouldBe a[ResultScenarioLogic[String, String]]
    s1.data.title shouldBe None
    s1.data.comment shouldBe None
    s1.data.whatsWrongWithMe shouldBe List()
    s1.data.references shouldBe List()
    s1.data.definedInSourceCodeAt.toString shouldBe "(SpecificationSpec.scala:14)"
  }

  it should "allow use cases and scenarios to be specified for Spec2" in {
    val uc2 = new SpecTestUseCase2("UseCase 2") {
      val s1: Scenario[(String, String), String] = scenario("inputa", "inputb") produces "expected one"
    }
    import UseCase1._

    val s = Specification.fromUsecase2(uc2)

    val List(s1) = s.scenarios
    s1 shouldBe uc2.s1
    s1.situation shouldBe("inputa", "inputb")
    s1.result shouldBe Some("expected one")
    s1.assertions shouldBe List()
    s1.logic shouldBe a[ResultScenarioLogic[String, String]]
    s1.data.title shouldBe None
    s1.data.comment shouldBe None
    s1.data.whatsWrongWithMe shouldBe List()
    s1.data.references shouldBe List()
    s1.data.definedInSourceCodeAt.toString shouldBe "(SpecificationSpec.scala:33)"
  }

  it should "allow for pretty when clauses - integration test" in {
    val uc1 = new SpecTestUseCase1("UseCase 1") {
      val s1: Scenario[String, String] = scenario("input1") produces "expected one" because { case inp if inp.startsWith("i") => "expected one" }
    }
    val s = uc1.s1
    val BecauseScenarioLogic(fn, defn, ifString) = s.logic
    defn.toString shouldBe "(SpecificationSpec.scala:54)"
//    ifString shouldBe ""
    fn.isDefinedAt("i") shouldBe true
    fn("i") shouldBe "expected one"
    fn.isDefinedAt("noti") shouldBe false
//    fn.toString shouldBe ""  //This is the next thing we want to do... make it nice

  }

}
