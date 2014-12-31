import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._

/**
 * Scalariform settings.
 */
object ScalariformSettings {

  def apply() = {
    scalariformSettings ++ {

      ScalariformKeys.preferences := FormattingPreferences()
        .setPreference(AlignParameters, true)
        .setPreference(AlignSingleLineCaseStatements, true)
        .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
        .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, false)

    }
  }
}



