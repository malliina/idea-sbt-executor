package com.mle.idea.sbtexecutor

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

object Util {
  // both e.getProject and project.getComponent may return null
  def runnerOpt(e: AnActionEvent) =
    Option(Option(e.getProject).map(runner)).flatten

  def runner(e: AnActionEvent): SbtExecuteConsoleComponent =
    runner(e.getProject)

  def runner(project: Project): SbtExecuteConsoleComponent =
    project.getComponent(classOf[SbtExecuteConsoleComponent])
}
