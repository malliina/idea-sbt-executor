package com.mle.idea.sbtexecutor

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.actionSystem.{ActionManager, DefaultActionGroup}
import com.intellij.openapi.components.AbstractProjectComponent
import com.intellij.openapi.project.{DumbAware, Project}
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.{ToolWindowAnchor, ToolWindowManager}
import com.intellij.ui.content.ContentFactory
import com.mle.idea.sbtexecutor.SbtExecuteConsoleComponent._

import java.awt.GridLayout
import javax.swing.{JComponent, JPanel}

class SbtExecuteConsoleComponent(project: Project)
  extends AbstractProjectComponent(project)
  with DumbAware {
  private val builder =
    TextConsoleBuilderFactory.getInstance().createBuilder(project)
  val console = builder.getConsole
  val commander = new CommandRunner(console)
  val killAction = new KillAction
  Disposer.register(project, console)

  /** Attaches the specified console `view` with the given `tabTitle` to the bottom.
    *
    * @param view     console to register
    * @param tabTitle title of console tab
    */
  def registerConsole(view: ConsoleView, tabTitle: String): Unit = {
    val toolWindowManager = ToolWindowManager.getInstance(project)
    Option(toolWindowManager).foreach { manager =>
      val window = manager.registerToolWindow(
        TOOL_WINDOW_ID,
        false,
        ToolWindowAnchor.BOTTOM,
        true
      )
      val windowPanel = new SimpleToolWindowPanel(false, true)
      windowPanel.setContent(view.getComponent)
      // toolbar with kill button
      windowPanel.setToolbar(newConsoleToolbarPanel(view.getComponent))
      val content = ContentFactory.SERVICE
        .getInstance()
        .createContent(windowPanel, "Output", true)
      window.getContentManager.addContent(content)
    }
  }

  def newConsoleToolbarPanel(target: JComponent): JPanel = {
    val panel = new JPanel(new GridLayout())
    val group = new DefaultActionGroup()
    group.add(killAction)
    val toolbar = ActionManager
      .getInstance()
      .createActionToolbar(
        SbtExecuteConsoleComponent.ACTION_TOOLBAR_ID,
        group,
        false
      )

    toolbar.setTargetComponent(target)
    panel.add(toolbar.getComponent)
    panel
  }

  /**
    * Attaches the "SBT Execute" console to the bottom.
    */
  def registerConsole(): Unit = registerConsole(console, TOOL_WINDOW_ID)

  def unregisterConsole(): Unit = try {
    val toolWindowManager = ToolWindowManager.getInstance(project)
    if (toolWindowManager != null && toolWindowManager.getToolWindow(TOOL_WINDOW_ID) != null) {
      toolWindowManager.unregisterToolWindow(TOOL_WINDOW_ID)
    }
  } catch {
    case e: Exception =>
  }

  private def setVisible(visible: Boolean): Unit = {
    val toolWindowManager = ToolWindowManager.getInstance(project)
    val noop = new Runnable {
      override def run(): Unit = {}
    }
    Option(toolWindowManager)
      .flatMap(twm => Option(twm.getToolWindow(TOOL_WINDOW_ID)))
      .foreach(tw => if (visible) tw.show(noop) else tw.hide(noop))
  }

  def show(): Unit = {
    setVisible(true)
  }

  def hide(): Unit = {
    setVisible(false)
  }

  override def projectOpened(): Unit = {
    val manager = StartupManager.getInstance(project)
    manager.registerPostStartupActivity(new Runnable {
      def run(): Unit = {
        val consoleComp =
          Option(project.getComponent(classOf[SbtExecuteConsoleComponent]))
            .getOrElse(new SbtExecuteConsoleComponent(project))
        consoleComp.registerConsole()
      }
    })
  }

  override def disposeComponent(): Unit = {
    unregisterConsole()
    commander.cancelJavaProcess()
    super.disposeComponent()
  }
}

object SbtExecuteConsoleComponent {
  val ACTION_TOOLBAR_ID = "SbtExecuteActionToolbar"
  val TOOL_WINDOW_ID = "SBT Execute"
}
