package com.mle.idea.sbtexecutor

import com.intellij.execution.ui.{ConsoleView, ConsoleViewContentType}
import com.mle.idea.sbtexecutor.CommandRunner._

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

/** TODO concurrent programming
  */
class CommandRunner(console: ConsoleView) {
  // runs the SBT command in the background
  private var javaBackgroundProcess: Option[java.lang.Process] = None

  /**
    * @return the exit value wrapped in an Option or None if the process is running, was canceled or has not been started
    */
  def exitValue: Option[Int] =
    javaBackgroundProcess.flatMap(CommandRunner.exitValue)

  def isRunning: Boolean =
    javaBackgroundProcess.exists(p => CommandRunner.exitValue(p).isEmpty)

  def runJavaProcess(builder: java.lang.ProcessBuilder): Unit = {
    javaBackgroundProcess.foreach(_.destroy())
    console.clear()
    val commandString = builder.command().mkString(" ") + newLine
    console.print(commandString, ConsoleViewContentType.SYSTEM_OUTPUT)
    val process = builder.start()
    javaBackgroundProcess = Some(process)
    Future {
      val is = Source.fromInputStream(process.getInputStream)
      try {
        is.getLines().foreach { line =>
          console.print(line + newLine, ConsoleViewContentType.NORMAL_OUTPUT)
        }
      } finally {
        is.close()
      }
    }
  }

  def cancelJavaProcess(): Unit = {
    javaBackgroundProcess.foreach(_.destroy())
    javaBackgroundProcess = None
  }
}

object CommandRunner {
  def exitValue(process: Process): Option[Int] =
    try {
      Some(process.exitValue())
    } catch {
      case itse: IllegalThreadStateException =>
        None
    }

  val newLine = sys.props("line.separator")
}
