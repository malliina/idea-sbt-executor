package com.mle.idea.sbtexecutor

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.openapi.actionSystem.{
  ActionManager,
  AnAction,
  AnActionEvent,
  DefaultActionGroup
}
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.util.io.{FileUtil, StreamUtil}
import com.intellij.openapi.wm.{
  RegisterToolWindowTask,
  ToolWindow,
  ToolWindowAnchor,
  ToolWindowManager
}
import com.intellij.ui.content.ContentFactory
import com.mle.idea.sbtexecutor.SbtCommandAction.{ACTION_TOOLBAR_ID, TOOL_WINDOW_ID}

import java.awt.GridLayout
import java.io.{File, IOException}
import javax.swing.{JComponent, JPanel}
import scala.collection.JavaConverters.seqAsJavaListConverter

object SbtCommandAction {
  val ACTION_TOOLBAR_ID = "SbtExecuteActionToolbar"
  val TOOL_WINDOW_ID = "SBT Execute"

  var commander: Option[CommandRunner] = None
}

class SbtCommandAction(sbtCommand: String, vmOptions: String)
  extends AnAction(sbtCommand, s"Executes $sbtCommand", null) {

  override def update(e: AnActionEvent): Unit = {
    e.getPresentation.setEnabled(SbtCommandAction.commander.forall(c => !c.isRunning))
  }

  def actionPerformed(e: AnActionEvent): Unit = {
    val project = e.getProject
    val projectPathString = project.getBasePath
    val workingDir = new File(projectPathString)
    val commandParams = buildCommand(e, sbtCommand, vmOptions)
    showToolWindow(project)
    // java
    val builder = new ProcessBuilder(commandParams.asJava)
    builder.directory(workingDir)
    builder.redirectErrorStream(true)
    SbtCommandAction.commander.foreach { c =>
      c.runJavaProcess(builder)
    }
  }

  private def showToolWindow(project: Project): ToolWindow = {
    val manager = ToolWindowManager.getInstance(project)
    val toolWindow = Option(manager.getToolWindow(TOOL_WINDOW_ID)).getOrElse {
      val window = manager.registerToolWindow(
        RegisterToolWindowTask.notClosable(TOOL_WINDOW_ID, ToolWindowAnchor.BOTTOM)
      )
      val windowPanel = new SimpleToolWindowPanel(false, true)
      val builder = TextConsoleBuilderFactory.getInstance().createBuilder(project)
      val console = builder.getConsole
      val commander = new CommandRunner(console)
      SbtCommandAction.commander = Option(commander)
      windowPanel.setContent(console.getComponent)
      // toolbar with kill button
      windowPanel.setToolbar(
        newConsoleToolbarPanel(console.getComponent, new KillAction(console, commander))
      )
      val content = ContentFactory.SERVICE
        .getInstance()
        .createContent(windowPanel, "Output", true)
      content.setDisposer(() => {
        commander.cancelJavaProcess()
      })
      window.getContentManager.addContent(content)
      window
    }
    toolWindow.show(null)
    toolWindow
  }

  def newConsoleToolbarPanel(target: JComponent, killAction: KillAction): JPanel = {
    val panel = new JPanel(new GridLayout())
    val group = new DefaultActionGroup()
    group.add(killAction)
    val toolbar = ActionManager
      .getInstance()
      .createActionToolbar(
        ACTION_TOOLBAR_ID,
        group,
        false
      )
    toolbar.setTargetComponent(target)
    panel.add(toolbar.getComponent)
    panel
  }

  private def buildCommand(e: AnActionEvent, sbtCommand: String, vmOptions: String): Seq[String] = {
    val java = "java"
    val vmOptionsSeq = vmOptions split " "
    val sbtJar = ensureSbtJarExists()
    // set SBT project if any is selected (helps for multi-module idea builds)
    val setProjectCommand = Seq.empty[String]
    Seq(java) ++
      vmOptionsSeq ++
      Seq("-jar", sbtJar.getAbsolutePath) ++
      setProjectCommand ++
      Seq(sbtCommand, "exit")
  }

  // adapted from idea-sbt-plugin
  private def ensureSbtJarExists(): File = {
    val jarName = "sbt-launch-1.5.5.jar"
    val maybeSbtJar =
      new File(new File(PathManager.getSystemPath, "sbtexe"), jarName)
    if (!maybeSbtJar.exists()) {
      val is =
        Option(classOf[SbtCommandAction].getClassLoader.getResourceAsStream(jarName))
      is.map { stream =>
          val bytes =
            try StreamUtil.readBytes(stream)
            finally stream.close()
          FileUtil.writeToFile(maybeSbtJar, bytes)
        }
        .getOrElse {
          throw new IOException(s"Not found: '$jarName'.")
        }
    }
    maybeSbtJar
  }
}
