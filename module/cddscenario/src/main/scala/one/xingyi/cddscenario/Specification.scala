package one.xingyi.cddscenario

import scala.language.higherKinds

trait Specification[P, R] {
  def scenarios: Seq[Scenario[P, R]]
  def useCases: Seq[UseCase[P, R]]

}
case class Specification1[P, R](scenarios: Seq[Scenario[P, R]], useCases: Seq[UseCase[P, R]]) extends Specification[P, R] {
}
case class Specification2[P1, P2, R](scenarios: Seq[Scenario[(P1, P2), R]], useCases: Seq[UseCase[(P1, P2), R]]) extends Specification[(P1, P2), R] {
}

object Specification {
  def fromUsecase1[T[_, _] : HasScenarios1 : HasUseCases1, P, R](t: T[P, R])(implicit hasUseCases: HasUseCases1[T], hasScenarios: HasScenarios1[T]): Specification[P, R] =
    Specification1[P, R](hasScenarios.allScenarios(t), hasUseCases.useCases(t))

  def fromUsecase2[T[_, _, _] : HasScenarios2 : HasUseCases2, P1, P2, R](t: T[P1, P2, R])(implicit hasUseCases: HasUseCases2[T], hasScenarios: HasScenarios2[T]): Specification2[P1, P2, R] =
    Specification2[P1, P2, R](hasScenarios.allScenarios(t), hasUseCases.useCases(t))

  implicit def hasScenarios: HasScenarios1[Specification] = new HasScenarios1[Specification] {
    override def allScenarios[P, R](t: Specification[P, R]): List[Scenario[P, R]] = t.scenarios.toList
  }
}