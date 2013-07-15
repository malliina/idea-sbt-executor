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

  def isModified: Boolean =
    appSettings.commands != form.commands.toSeq || appSettings.vmOptions != form.vmOptions

  def apply() {
    appSettings.commands = form.commands
    appSettings.vmOptions = form.vmOptions
    ExecuteSbtSettings.save(appSettings)
  }

  def reset() {
    form.setModel(appSettings)
  }

  def disposeUIResources() {}

  def getDisplayName = "SBT Executor"

  def getHelpTopic: String = null
}
