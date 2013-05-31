package com.mle.idea.sbtexecutor

import com.intellij.execution.ui.{ConsoleView, ConsoleViewContentType}
import scala.concurrent.Future
import scala.io.Source
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConversions._
import CommandRunner._

/**
 *
 * @author mle
 */
class CommandRunner(console: ConsoleView) {
  //  private var backgroundProcess: Option[Process] = None
  // runs the SBT command in the background
  private var javaBackgroundProcess: Option[java.lang.Process] = None
  //  private val consoleLogger = ProcessLogger(
  //    out => console.print(out + "\n", ConsoleViewContentType.NORMAL_OUTPUT),
  //    err => console.print(err + "\n", ConsoleViewContentType.ERROR_OUTPUT)
  //  )

  def runJavaProcess(builder: java.lang.ProcessBuilder) {
    javaBackgroundProcess.foreach(_.destroy())
    console.clear()
    console.print(builder.command().mkString(" ") + newLine, ConsoleViewContentType.SYSTEM_OUTPUT)
    val process = builder.start()
    javaBackgroundProcess = Some(process)
    Future(Source.fromInputStream(process.getInputStream).getLines().foreach(line => {
      console print(line + newLine, ConsoleViewContentType.NORMAL_OUTPUT)
    }))
  }

  def cancelJavaProcess() {
    javaBackgroundProcess.foreach(_.destroy())
    javaBackgroundProcess = None
  }

  /**
   *
   * @see runJavaProcess
   */
  //  def runProcess(builder: ProcessBuilder) {
  //    backgroundProcess.foreach(_.destroy())
  //    console.clear()
  //    backgroundProcess = Some(builder run consoleLogger)
  //  }


  /**
   * Throws ThreadDeath, causing plugin to explode. Using java.lang.Process for now,
   * which does not exhibit that behavior upon destruction.
   *
   * TODO: get this working and cut the java bs
   * @see cancelJavaProcess
   */
  //  def cancelProcess() {
  //    backgroundProcess.foreach(_.destroy())
  //    backgroundProcess = None
  //  }
}

object CommandRunner {
  val newLine = sys.props("line.separator")
}
