package com.mle.idea.sbtexecutor.settings

import ExecuteSbtSettings._
import com.intellij.ide.util.PropertiesComponent
import com.mle.idea.sbtexecutor.SbtCommandGroup

/**
 *
 * @author mle
 */
class ExecuteSbtSettings {
  var sbtCommands: Seq[String] = load
}

object ExecuteSbtSettings {
  val defaultCommands = Seq("compile", "clean")
  val settingsKey = "sbt-executor-commands"

  def save(commands: Seq[String]) {
    val appProps = PropertiesComponent.getInstance()
    appProps.setValues(settingsKey, commands.toArray)
    // refactor
    SbtCommandGroup.reload()
  }

  def load: Seq[String] = {
    val appProps = PropertiesComponent.getInstance()
    val maybeSaved: Seq[String] = appProps getValues settingsKey
    Option(maybeSaved) getOrElse defaultCommands
  }
}
