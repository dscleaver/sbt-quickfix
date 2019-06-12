package com.dscleaver.sbt.quickfix

import sbt.{Level => _, State => _, _}
import org.apache.logging.log4j.core.{ Appender, LogEvent }
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.ErrorHandler
import org.apache.logging.log4j.core.Layout
import org.apache.logging.log4j.core.LifeCycle

object QuickFixAppender {
  def append(output: File, prefix: String, message: String): Unit =
    IO.append(output, "[%s] %s\n".format(prefix, message))

  def append(output: File, prefix: String, file: File, line: Int, message: String): Unit =
    append(output, prefix, "%s:%d: %s".format(file, line, message))
}

class QuickFixAppender(val output: File, vimExec: String, ignoreExceptions: Boolean, enableServer: Boolean, errorAppender: Appender) extends Appender {
  import VimInteraction._
  
  var handler: ErrorHandler = errorAppender.getHandler()

  override def getHandler(): ErrorHandler = handler
  override def getLayout(): Layout[_] = null
  override def getName(): String = "sbt-vim-quickfix"
  override def ignoreExceptions(): Boolean = ignoreExceptions
  override def setHandler(eh: ErrorHandler): Unit = handler = eh
  override def getState(): LifeCycle.State = LifeCycle.State.STARTED
  override def initialize(): Unit = {}
  override def isStarted(): Boolean = true
  override def isStopped(): Boolean = false
  override def start(): Unit = {}
  override def stop(): Unit = {}

  override def append(event: LogEvent): Unit = log(
    event.getLevel(), 
    event.getMessage().getFormattedMessage()
  )

  // New messages have a weird format:
  // [error] ObjectEvent(error, [Error] /Users/jacobbarber/projects/sbt-quickfix/src/main/scala/com/dscleaver/sbt/SbtQuickFix.scala:37: not found: value CasonsoleAppender, Some(console0), Some(a0cfcd86-16e3-4b94-997d-3561d8e399d4), xsbti.Problem, JObject([Lsjsonnew.shaded.scalajson.ast.unsafe.JField;@74df44fc))
  // [error] ObjectEvent(error, [Error] /Users/jacobbarber/projects/sbt-quickfix/src/main/scala/com/dscleaver/sbt/SbtQuickFix.scala:6: Unused import, Some(console0), Some(a0cfcd86-16e3-4b94-997d-3561d8e399d4), xsbti.Problem, JObject([Lsjsonnew.shaded.scalajson.ast.unsafe.JField;@2f7b4be1))
  // [error] StringEvent(error, two errors found, Some(console0), Some(a0cfcd86-16e3-4b94-997d-3561d8e399d4))
  // [error] StringEvent(error, (Compile / compileIncremental) Compilation failed, Some(console0), Some(a0cfcd86-16e3-4b94-997d-3561d8e399d4))
  //
  // so we need to parse them first, and remove the stuff we don't care about.
  // The parsing I'm doing below should be sufficient for most cases.

  def log(level: Level, message: String): Unit = {
    val parsedMessage = 
      if(message.startsWith("StringEvent") || message.startsWith("ObjectEvent")) 
        message
          .split(",") 
          .drop(1).head
          .replaceAll("\\[(Error|Info|Warn|Debug)\\]", "")
          .trim()
      else 
        message

    level match {
      case Level.INFO => handleInfoMessage(parsedMessage)
      case Level.ERROR  => handleErrorMessage(parsedMessage)
      case Level.WARN => handleWarnMessage(parsedMessage)
      case _ => handleDebugMessage(parsedMessage)
    }
  }

  def handleDebugMessage(message: String): Unit =
    if (enableServer && message.toLowerCase.contains("compilation failed")) {
      val _ = call(vimExec, "<esc>:cfile %s<cr>".format(output.toString))
    }

  def handleInfoMessage(message: String): Unit = {
    if(message startsWith "Compiling") {
      IO.delete(output)
      IO.touch(List(output))
    } else ()
  }


  def handleErrorMessage(message: String): Unit = QuickFixAppender.append(output, "error", message)

  def handleWarnMessage(message: String): Unit = QuickFixAppender.append(output, "warn", message)
}
