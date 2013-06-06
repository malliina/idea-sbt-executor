package com.mle.idea.sbtexecutor.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent
import scala.collection.JavaConversions._

/**
 *
 * @author mle
 */
class ExecuteSbtConfigurable(appSettings: ExecuteSbtSettings) extends Configurable {
  val form = new ExecuteSbtSettingsForm

  def createComponent(): JComponent = form.comp

  def isModified: Boolean = appSettings.sbtCommands != form.sbtCommands.toSeq

  def apply() {
    appSettings.sbtCommands = form.sbtCommands
    ExecuteSbtSettings.save(appSettings.sbtCommands)
  }

  def reset() {
    form.setModel(appSettings.sbtCommands)
  }

  def disposeUIResources() {}

  def getDisplayName = "SBT Executor"

  def getHelpTopic: String = null
}
