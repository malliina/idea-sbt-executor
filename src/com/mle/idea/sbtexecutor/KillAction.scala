package com.mle.idea.sbtexecutor

import com.intellij.openapi.actionSystem.{AnActionEvent, AnAction}
import com.intellij.openapi.util.IconLoader
import com.intellij.execution.ui.ConsoleViewContentType

/**
 *
 * @author mle
 */
class KillAction
  extends AnAction("Cancel command", null, IconLoader.getIcon("/debugger/killProcess.png")) {
  def actionPerformed(e: AnActionEvent) {
    val console = e.getProject.getComponent(classOf[SbtExecuteConsoleComponent])
    console.commander.cancelJavaProcess()
    console.console.print("Canceled by user." + CommandRunner.newLine, ConsoleViewContentType.ERROR_OUTPUT)
  }
}
