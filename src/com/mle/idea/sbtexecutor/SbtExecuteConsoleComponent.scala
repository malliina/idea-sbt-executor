package com.mle.idea.sbtexecutor

import com.intellij.openapi.project.Project
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.openapi.wm.{ToolWindowAnchor, ToolWindowManager}
import SbtExecuteConsoleComponent._
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.content.ContentFactory
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.components.AbstractProjectComponent
import com.intellij.openapi.startup.StartupManager
import java.awt.GridLayout
import javax.swing.JPanel
import com.intellij.openapi.actionSystem.{DefaultActionGroup, ActionManager}

/**
 *
 * @author mle
 */
class SbtExecuteConsoleComponent(project: Project) extends AbstractProjectComponent(project) {
  private val builder = TextConsoleBuilderFactory.getInstance().createBuilder(project)
  val console = builder.getConsole
  val commander = new CommandRunner(console)

  /**
   * Attaches the specified console view with the given title to the bottom.
   *
   * @param view console to register
   * @param tabTitle title of console tab
   */
  def registerConsole(view: ConsoleView, tabTitle: String) {
    val toolWindowManager = ToolWindowManager.getInstance(project)
    Option(toolWindowManager).foreach(manager => {
      val window = manager.registerToolWindow(TOOL_WINDOW_ID, false, ToolWindowAnchor.BOTTOM, true)
      val windowPanel = new SimpleToolWindowPanel(false, true)
      windowPanel setContent view.getComponent
      // toolbar with kill button
      windowPanel setToolbar newConsoleToolbarPanel
      val content = ContentFactory.SERVICE.getInstance().createContent(windowPanel, "Output", true)
      window.getContentManager.addContent(content)
    })
  }

  def newConsoleToolbarPanel = {
    val panel = new JPanel(new GridLayout())
    val action = new KillAction
    val group = new DefaultActionGroup()
    group add action
    val toolbar = ActionManager.getInstance()
      .createActionToolbar(SbtExecuteConsoleComponent.ACTION_TOOLBAR_ID, group, false)
    panel add toolbar.getComponent
    panel
  }

  /**
   * Attaches the "SBT Execute" console to the bottom.
   */
  def registerConsole() {
    registerConsole(console, TOOL_WINDOW_ID)
  }

  def unregisterConsole() {
    val toolWindowManager = ToolWindowManager.getInstance(project)
    if (toolWindowManager != null && toolWindowManager.getToolWindow(TOOL_WINDOW_ID) != null) {
      toolWindowManager unregisterToolWindow TOOL_WINDOW_ID
    }
  }

  private def setVisible(visible: Boolean) {
    val toolWindowManager = ToolWindowManager.getInstance(project)
    val noop = new Runnable {
      def run() {}
    }
    Option(toolWindowManager)
      .map(twm => Option(twm.getToolWindow(TOOL_WINDOW_ID))).flatten
      .map(tw => if (visible) tw.show(noop) else tw.hide(noop))
  }

  def show() {
    setVisible(true)
  }

  def hide() {
    setVisible(false)
  }

  override def projectOpened() {
    val manager = StartupManager.getInstance(project)
    manager.registerPostStartupActivity(new Runnable {
      def run() {
        val consoleComp = Option(project.getComponent(classOf[SbtExecuteConsoleComponent]))
          .getOrElse(new SbtExecuteConsoleComponent(project))
        consoleComp.registerConsole()
      }
    })
  }

  override def disposeComponent() {
    unregisterConsole()
    commander.cancelJavaProcess()
    super.disposeComponent()
  }
}

object SbtExecuteConsoleComponent {
  val TOOL_WINDOW_ID = "SBT Execute"
  val ACTION_TOOLBAR_ID = "SbtExecuteActionToolbar"
}
