package com.mle.idea.sbtexecutor

import com.intellij.openapi.actionSystem.{ActionManager, AnAction, AnActionEvent, ActionGroup}
import com.mle.idea.sbtexecutor.settings.ExecuteSbtSettings

/**
 *
 * @author mle
 */
class SbtCommandGroup extends ActionGroup {
  var actions: Array[AnAction] = loadActions

  def getChildren(e: AnActionEvent): Array[AnAction] = actions

  def loadActions: Array[AnAction] = {
    (ExecuteSbtSettings.load map (cmd => new SbtCommandAction(cmd))).toArray
  }

  def reload() {
    actions = loadActions
  }
}

object SbtCommandGroup {
  val id = "SBT command group"

  def reload() {
    val group = ActionManager.getInstance().getAction(id).asInstanceOf[SbtCommandGroup]
    group.reload()
  }
}
