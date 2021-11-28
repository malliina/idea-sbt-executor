package com.mle.idea.sbtexecutor

import com.intellij.execution.ui.{ConsoleView, ConsoleViewContentType}
import com.intellij.icons.AllIcons.Icons
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}
import com.intellij.openapi.util.IconLoader

class KillAction(val console: ConsoleView, val commander: CommandRunner)
  extends AnAction(
    "Cancel command",
    null,
    IconLoader.getIcon("/debugger/killProcess.png", classOf[Icons])
  )
  with EnabledWhenRunning {

  def actionPerformed(e: AnActionEvent): Unit = {
    commander.cancelJavaProcess()
    console.print(
      "Canceled by user." + CommandRunner.newLine,
      ConsoleViewContentType.ERROR_OUTPUT
    )
  }
}
