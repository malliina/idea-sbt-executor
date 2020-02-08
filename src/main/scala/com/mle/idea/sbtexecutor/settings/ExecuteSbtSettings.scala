package com.mle.idea.sbtexecutor.settings

import com.intellij.ide.util.PropertiesComponent
import com.mle.idea.sbtexecutor.SbtCommandGroup
import com.mle.idea.sbtexecutor.settings.ExecuteSbtSettings._

class ExecuteSbtSettings {
  var commands: Seq[String] = loadCommands
  var vmOptions: String = loadVmOptions
}

object ExecuteSbtSettings {
  val defaultCommands = Seq("compile", "test", "clean")
  val defaultVmOptions = "-Xmx512M -Dsbt.log.noformat=true"
  val settingsKey = "sbt-executor-commands"
  val vmOptionsKey = "sbt-executor-vmoptions"

  def save(settings: ExecuteSbtSettings): Unit = {
    val appProps = PropertiesComponent.getInstance()
    appProps.setValues(
      settingsKey,
      settings.commands.map(Option.apply).flatten.toArray
    )
    appProps.setValue(vmOptionsKey, settings.vmOptions)
    SbtCommandGroup.reload()
  }

  def loadCommands = {
    val appProps = PropertiesComponent.getInstance()
    val maybeCommands: Seq[String] = appProps getValues settingsKey
    Option(maybeCommands) getOrElse defaultCommands
  }

  def loadVmOptions = {
    val appProps = PropertiesComponent.getInstance()
    Option(appProps getValue vmOptionsKey) getOrElse defaultVmOptions
  }
}
