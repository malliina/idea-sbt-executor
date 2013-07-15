package com.mle.idea.sbtexecutor.settings

import com.intellij.ide.util.PropertiesComponent
import com.mle.idea.sbtexecutor.SbtCommandGroup
import ExecuteSbtSettings._

/**
 *
 * @author mle
 */
//case class Settings(commands: Seq[String], vmOptions: String)

class ExecuteSbtSettings {
  var commands: Seq[String] = loadCommands
  var vmOptions: String = loadVmOptions
}

object ExecuteSbtSettings {
  val defaultCommands = Seq("compile", "clean", "gen-idea")
  val defaultVmOptions = "-Xmx512M -XX:MaxPermSize=256M"
  val settingsKey = "sbt-executor-commands"
  val vmOptionsKey = "sbt-executor-vmoptions"

  def save(settings: ExecuteSbtSettings) {
    val appProps = PropertiesComponent.getInstance()
    appProps.setValues(settingsKey, settings.commands.toArray)
    appProps.setValue(vmOptionsKey, settings.vmOptions)
    // TODO: refactor
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
