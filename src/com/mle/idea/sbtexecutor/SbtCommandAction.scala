package com.mle.idea.sbtexecutor

import com.intellij.openapi.actionSystem.{LangDataKeys, AnActionEvent, AnAction}
import java.nio.file.Paths
import java.io.File
import com.intellij.openapi.application.PathManager
import scala.collection.JavaConversions._

/**
 *
 * @author mle
 */
class SbtCommandAction(sbtCommand: String)
  extends AnAction(sbtCommand, s"Executes $sbtCommand", null)
  with EnabledWhenNotRunning {

  def actionPerformed(e: AnActionEvent) {
    val project = e.getProject
    val projectPathString = project.getBasePath
    val workingDir = Paths get projectPathString
    val commandParams = buildCommand(e, sbtCommand)
    val consoleComponent = project.getComponent(classOf[SbtExecuteConsoleComponent])
    // scala
    //    consoleComponent runProcess Process(commandParams, workingDir.toFile)
    // java
    val builder = new ProcessBuilder(commandParams)
    builder directory workingDir.toFile
    builder redirectErrorStream true
    consoleComponent.commander runJavaProcess builder
  }

  private def buildCommand(e: AnActionEvent, sbtCommand: String) = {
    val module = e.getData(LangDataKeys.MODULE)
    val java = "java"
    // from idea-sbt-plugin on github
    val launcherName = "sbt-launch.jar"
    val launcher = new File(new File(PathManager.getSystemPath, "sbt"), launcherName)
    // set SBT project if any is selected (helps for multi-module idea builds)
    val setProjectCommand =
      if (module != null) {
        Seq("\"project " + module.getName + "\"")
      } else {
        Seq.empty[String]
      }
    Seq(
      java,
      "-Xmx512M",
      "-XX:MaxPermSize=256M",
      "-jar",
      launcher.toPath.toAbsolutePath.toString
    ) ++ setProjectCommand :+ sbtCommand :+ "exit"
  }

  //  def executeSbtCommand(commandParams: Seq[String], workingDir: Path, logger: ProcessLogger) {
  //    val commandString = commandParams mkString " "
  //    // print commandParams prior to execution
  //    logger out commandString
  //    try {
  //      val builder = Process(commandParams, workingDir.toFile)
  //      val backgroundProcess = builder run logger
  //      //      Future(backgroundProcess.exitValue()).map(exitValue => {
  //      //        logger out s"Command: '$sbtCommand' completed with exit value: $exitValue"
  //      //        SbtCommandAction.remove(backgroundProcess)
  //      //      })
  //    } catch {
  //      case re: RuntimeException =>
  //        logger out re.getMessage
  //    }
  //  }
}