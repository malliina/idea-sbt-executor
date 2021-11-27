package com.mle.idea.sbtexecutor

import java.io.{File, IOException}
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.util.io.{FileUtil, StreamUtil}

import scala.collection.JavaConverters.seqAsJavaListConverter

class SbtCommandAction(sbtCommand: String, vmOptions: String)
  extends AnAction(sbtCommand, s"Executes $sbtCommand", null)
  with EnabledWhenNotRunning {

  def actionPerformed(e: AnActionEvent): Unit = {
    val project = e.getProject
    val projectPathString = project.getBasePath
    val workingDir = new File(projectPathString)
    val commandParams = buildCommand(e, sbtCommand, vmOptions)
    val consoleComponent =
      project.getComponent(classOf[SbtExecuteConsoleComponent])
    consoleComponent.show()
    // java
    val builder = new ProcessBuilder(commandParams.asJava)
    builder directory workingDir
    builder redirectErrorStream true
    consoleComponent.commander runJavaProcess builder
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
