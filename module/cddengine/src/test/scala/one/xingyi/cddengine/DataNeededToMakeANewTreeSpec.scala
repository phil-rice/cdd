/** Copyright (c) 2020, Phil Rice. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */
package one.xingyi.cddengine

import one.xingyi.cddscenario.{DecisionIssue, Scenario}
import one.xingyi.core.UtilsSpec

class DataNeededToMakeANewTreeSpec extends UtilsSpec with DecisionTreeFixture {

  behavior of "DecisionTreeFoldingData"

  case class DecisionIssueForTest[P, R](node: ConclusionNode[P, R], scenario: Scenario[P, R]) extends DecisionIssue[ConclusionNode, P, R]("someIssue")

  val issue1 = DecisionIssueForTest[String, String](conca, snormal)
  val issue2 = DecisionIssueForTest[String, String](concb, sa)

  private val treeAndScenario = TreeAndScenario(treeNormalPassport, snormal2)
  val data = DataNeededToMakeANewTree(treeAndScenario, mock[DTFolderStrategy])

  behavior of "DataNeededToMakeANewTree"

  it should "have the old tree as the tree in the 'TreeAndScenario'" in {
    data.oldTree shouldBe treeNormalPassport
  }
  it should " delegate conclusionAndScenario to the 'TreeAndScenario'" in {
    data.conclusionAndScenario shouldBe treeAndScenario.conclusionAndScenario
  }
  it should "delegate lens to the 'TreeAndScenario'" in {
    data.lens shouldBe treeAndScenario.lens
  }
}
