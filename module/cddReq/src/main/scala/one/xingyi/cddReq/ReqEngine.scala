package one.xingyi.cddReq

import one.xingyi.cddscenario.{CannotAddScenarioBecauseClashes, DecisionIssue, HasScenarios1, Scenario, ScenarioThrowsException}
import one.xingyi.core.reflection.{DefinedInSourceCodeAt, IsDefinedInSourceCodeAt}

import scala.language.higherKinds
import scala.util.{Success, Try}

class ReqEngineException[P, R](r: ReqEngine[P, R], s: String, cause: Throwable) extends RuntimeException(s, cause) {
}
object ReqEngine {
  def apply[T[_, _], P, R](t: T[P, R])(implicit has: HasScenarios1[T]): ReqEngine[P, R] = ReqEngine(has.allScenarios(t))
  implicit def reqEngineDefinedInSourceCode[P, R]: IsDefinedInSourceCodeAt[ReqEngine[P, R]] = _.definedInSourceCodeAt
}


case class ReqEngine[P, R](scenarios: List[Scenario[P, R]]) extends PartialFunction[P, R] {

  val definedInSourceCodeAt = DefinedInSourceCodeAt.definedInSourceCodeAt(3)
  val pFn = scenarios.map(_.logic.fn).reduce(_ orElse _)
  override def isDefinedAt(x: P): Boolean = pFn.isDefinedAt(x)
  override def apply(p: P): R = try {pFn(p) } catch {case e: MatchError => throw new ReqEngineException(this, s"ReqEngine defined at $definedInSourceCodeAt, cannot process [$p]", e)}
  //  def validate(): Unit = {

  def findScenarioForSituation(p: P): Option[Scenario[P, R]] = scenarios.find(_.logic.fn.isDefinedAt(p))
  def validateAsException = {
    validate.size match {
      case 0 =>
      case 1 => throw validate.head
      case 2 => throw new RuntimeException(validate.mkString("\n\n"))
    }
  }
  lazy val validate: List[DecisionIssue[ReqEngine, P, R]] = {
    scenarios.flatMap { s =>
      val optGuardian = findScenarioForSituation(s.situation)
      try {
        val actual = pFn(s.situation)
        (s.result, optGuardian) match {
          case (Some(r), Some(g)) if r != actual =>
            List(CannotAddScenarioBecauseClashes(s, this, List(g), CannotAddScenarioBecauseClashes.expectedVsActualMessage(r, actual)))
          case _ => Nil
        }
      } catch {case e: Exception => List(ScenarioThrowsException(s, this, e, optGuardian.toList))}
    }
  }
  //    scenarios.map(s)
  //  }
}
