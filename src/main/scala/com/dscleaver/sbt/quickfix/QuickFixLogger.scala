package org.dscleaver.sbt.quickfix

import sbt._

class QuickFixLogger(val output: File) extends BasicLogger {

  def log(level: Level.Value, message: => String): Unit = level match {
      case Level.Info => handleInfoMessage(message)
      case Level.Error => handleErrorMessage(message)
      case Level.Warn => handleWarnMessage(message)
      case _ => ()
  }

  def handleInfoMessage(message: String) =
    if(message startsWith "Compiling") {
      IO.delete(output)
      IO.touch(List(output))
    } else ()

  def handleErrorMessage(message: String) = 
    IO.append(output, "[error] " + message + "\n")

  def handleWarnMessage(message: String) =
    IO.append(output, "[warn] " + message + "\n")

  def control(event: ControlEvent.Value, message: => String): Unit = ()

  def logAll(events: Seq[LogEvent]): Unit = ()

  def success(message: => String): Unit = ()

  def trace(t: => Throwable): Unit = ()

}
