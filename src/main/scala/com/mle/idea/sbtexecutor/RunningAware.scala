package com.mle.idea.sbtexecutor

import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}

trait RunningAware extends AnAction {
  def commander: CommandRunner
  def enableWhenRunning: Boolean

  override def update(e: AnActionEvent): Unit = {
    val isRunning = commander.isRunning
    val shouldEnable = isRunning == enableWhenRunning
    e.getPresentation setEnabled shouldEnable
  }
}

/** An action that is enabled when no SBT command is running, disabled otherwise.
  */
trait EnabledWhenNotRunning extends RunningAware {
  override val enableWhenRunning = false
}

trait EnabledWhenRunning extends RunningAware {
  override val enableWhenRunning = true
}
