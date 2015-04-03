import com.typesafe.sbt.pgp.PgpKeys._
import sbt.Keys._
import sbt.{State, TaskKey, _}
import sbtrelease.ReleaseStep
import sbtrelease.Utilities._
import xerial.sbt.Sonatype.SonatypeKeys._
import scala.xml.transform.{RewriteRule, RuleTransformer}

object SbtReleaseHelpers {
  def oneTaskStep = (task: TaskKey[_]) => ReleaseStep(action = (st: State) => {
    val extracted = st.extract
    val ref = extracted.get(thisProjectRef)
    extracted.runAggregated(task in Global in ref, st)
  }, enableCrossBuild = true)

  val publishArtifactsLocally = oneTaskStep(publishLocal)
  val publishArtifactsSigned = oneTaskStep(publishSigned)
  val finishReleaseAtSonatype = oneTaskStep(sonatypeReleaseAll)
}

object PackagingHelpers {
  private def testIfRemove(dep: xml.Node) =
    ((dep \ "scope").text == "test") ||
      ((dep \ "classifier").text == "sources")

  val removeTestOrSourceDependencies: (xml.Node) => xml.Node = { (node: xml.Node) =>
    val rewriteRule = new RewriteRule {
      override def transform(node: xml.Node) = node.label match {
        case "dependency" if testIfRemove(node) => xml.NodeSeq.Empty
        case _ => node
      }
    }
    val transformer = new RuleTransformer(rewriteRule)
    transformer.transform(node).head
  }
}