package com.mle.idea.sbtexecutor

import com.intellij.openapi.actionSystem.{ActionGroup, ActionManager, AnAction, AnActionEvent}
import com.mle.idea.sbtexecutor.settings.ExecuteSbtSettings

class SbtCommandGroup extends ActionGroup {
  var actions: Array[AnAction] = loadActions

  def getChildren(e: AnActionEvent): Array[AnAction] = actions

  def loadActions: Array[AnAction] = {
    val commands = ExecuteSbtSettings.loadCommands
    val actionBuilder: String => SbtCommandAction =
      new SbtCommandAction(_, ExecuteSbtSettings.loadVmOptions)
    val actions = commands map actionBuilder
    actions.toArray
  }

  def reload(): Unit = {
    actions = loadActions
  }
}

object SbtCommandGroup {
  val id = "SBT command group"

  def reload(): Unit = {
    val group = ActionManager.getInstance().getAction(id).asInstanceOf[SbtCommandGroup]
    group.reload()
  }
}
