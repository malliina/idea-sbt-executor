package com.mle.idea.sbtexecutor

import com.intellij.openapi.project.Project
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 *
 * @author mle
 */
object Util {
  // both e.getProject and project.getComponent may return null
  def runnerOpt(e: AnActionEvent) =
    Option(Option(e.getProject).map(runner)).flatten

  def runner(e: AnActionEvent): SbtExecuteConsoleComponent =
    runner(e.getProject)

  def runner(project: Project): SbtExecuteConsoleComponent =
    project.getComponent(classOf[SbtExecuteConsoleComponent])
}
