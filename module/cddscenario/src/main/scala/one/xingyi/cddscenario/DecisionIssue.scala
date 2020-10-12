/** Copyright (c) 2020, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.cddscenario

import one.xingyi.core.reflection.IsDefinedInSourceCodeAt

import scala.language.higherKinds

abstract class DecisionIssue[Node[_, _], P, R](msg: String) extends RuntimeException(msg) {
  def node: Node[P, R]
  def scenario: Scenario[P, R]
}

object CannotAddScenarioBecauseClashes{
  def expectedVsActualMessage[R](expected: R, actual: R)={
    s"""Expected
       |$expected
       |Actual
       |$actual
       |""".stripMargin
  }
}
case class CannotAddScenarioBecauseClashes[Node[_, _], P, R](scenario: Scenario[P, R], node: Node[P, R],  clashesWith: List[Scenario[P, R]], expectedVsActualmessage: String)(implicit isDefinedInSourceCodeAt: IsDefinedInSourceCodeAt[Node[P, R]]) extends DecisionIssue[Node, P, R](
  s"""Cannot add scenario\n${scenario.logic.definedInSourceCodeAt} $scenario
     |To node defined at ${isDefinedInSourceCodeAt(node)}}
     |Because it clashes with
     |${clashesWith.map(s => s"${s.logic.definedInSourceCodeAt} $s").mkString("\n")}
     |$expectedVsActualmessage
     |Details of node
     |$node
     | """.stripMargin)

object ScenarioThrowsException {
  def message[Node[_, _], P, R](scenario: Scenario[P, R], node: Node[P, R], e: Exception, bestMatch: List[Scenario[P, R]])(implicit isDefinedInSourceCodeAt: IsDefinedInSourceCodeAt[Node[P, R]]) = {

    val otherScenariosString = if (bestMatch.size == 0) "There was no scenario that it matches"
    else
      """The Scenario was evaluated by the code defined by
        |${bestMatch.map(s => s"${s.logic.definedInSourceCodeAt} $s").mkString("\n")}
        |""".stripMargin

    s"""Cannot add scenario\n${scenario.logic.definedInSourceCodeAt} $scenario
       |To node defined at ${isDefinedInSourceCodeAt(node)}}
       |Because it throws an exception ${e.getClass.getName}
       |${e.getMessage}
       |$otherScenariosString
       |Details of node
       |$node
       | """.stripMargin
  }
}
case class ScenarioThrowsException[Node[_, _], P, R](scenario: Scenario[P, R], node: Node[P, R], e: Exception, bestMatch: List[Scenario[P, R]])(implicit isDefinedInSourceCodeAt: IsDefinedInSourceCodeAt[Node[P, R]])
  extends DecisionIssue[Node, P, R](ScenarioThrowsException.message(scenario, node, e, bestMatch))
