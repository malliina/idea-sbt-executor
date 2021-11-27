package com.mle.idea.sbtexecutor

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.icons.AllIcons.Icons
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}
import com.intellij.openapi.util.IconLoader

class KillAction
  extends AnAction(
    "Cancel command",
    null,
    IconLoader.getIcon("/debugger/killProcess.png", classOf[Icons])
  )
  with EnabledWhenRunning {

  def actionPerformed(e: AnActionEvent): Unit = {
    val console = Util.runner(e)
    console.commander.cancelJavaProcess()
    console.console.print(
      "Canceled by user." + CommandRunner.newLine,
      ConsoleViewContentType.ERROR_OUTPUT
    )
  }
}
