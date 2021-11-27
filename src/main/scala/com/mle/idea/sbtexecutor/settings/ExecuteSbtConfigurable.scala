package com.mle.idea.sbtexecutor.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

import scala.collection.JavaConverters.asScalaBufferConverter

class ExecuteSbtConfigurable(appSettings: ExecuteSbtSettings) extends Configurable {
  val form = new ExecuteSbtSettingsForm

  def createComponent(): JComponent = form.comp

  def isModified: Boolean =
    appSettings.commands != form.commands.asScala || appSettings.vmOptions != form.vmOptions

  def apply(): Unit = {
    appSettings.commands = form.commands.asScala
    appSettings.vmOptions = form.vmOptions
    ExecuteSbtSettings.save(appSettings)
  }

  override def reset(): Unit =
    form.setModel(appSettings)

  override def disposeUIResources(): Unit = {}

  def getDisplayName = "SBT Executor"

  override def getHelpTopic: String = null
}
