package com.mle.idea.sbtexecutor

import com.intellij.openapi.actionSystem.{LangDataKeys, AnActionEvent, AnAction}
import java.nio.file.{Files, Path, Paths}
import com.intellij.openapi.application.PathManager
import scala.collection.JavaConversions._
import com.intellij.openapi.util.io.{FileUtil, StreamUtil}

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
    consoleComponent.show()
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
    val sbtJar = ensureSbtJarExists
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
      sbtJar.toAbsolutePath.toString
    ) ++ setProjectCommand :+ sbtCommand :+ "exit"
  }

  // adapted from idea-sbt-plugin
  private def ensureSbtJarExists: Path = {
    val jarName = "sbt-launch.jar"
    val maybeSbtJar = Paths.get(PathManager.getSystemPath, "sbtexe", jarName)
    if (!(Files exists maybeSbtJar)) {
      val is = classOf[SbtCommandAction].getClassLoader.getResourceAsStream(jarName)
      val bytes =
        try {
          StreamUtil loadFromStream is
        } finally {
          StreamUtil closeStream is
        }
      FileUtil.writeToFile(maybeSbtJar.toFile, bytes)
    }
    maybeSbtJar
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