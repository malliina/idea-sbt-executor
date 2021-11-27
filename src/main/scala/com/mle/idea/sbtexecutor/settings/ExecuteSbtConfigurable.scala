package com.mle.idea.sbtexecutor.settings

import com.intellij.openapi.options.Configurable

import javax.swing.JComponent
import scala.collection.JavaConverters.asScalaBufferConverter

class ExecuteSbtConfigurable extends Configurable {
  val form = new ExecuteSbtSettingsForm
  val settings = new ExecuteSbtSettings

  override def createComponent(): JComponent = form.comp

  override def isModified(): Boolean =
    settings.commands != form.commands.asScala || settings.vmOptions != form.vmOptions

  override def apply(): Unit = {
    settings.commands = form.commands.asScala
    settings.vmOptions = form.vmOptions
    ExecuteSbtSettings.save(settings)
  }

  override def reset(): Unit =
    form.setModel(settings)

  override def disposeUIResources(): Unit = ()
  override def getDisplayName: String = "SBT Executor"
  override def getHelpTopic: String = null
}
