package com.dscleaver.sbt.quickfix

import sbt._
import sbt.TestResult.Value
import sbt.testing.Status._
import sbt.testing.Event

class QuickFixTestListener(output: File, srcFiles: => Seq[File], vimExec: String, enableServer: Boolean) extends TestReportListener {
  import QuickFixLogger._
  import VimInteraction._

  type TFE = Exception {
    def failedCodeFileName: Option[String]
    def failedCodeLineNumber: Option[Int]
  }

  IO.delete(output)
  IO.touch(List(output))

  def startGroup(name: String): Unit = {}

  def testEvent(event: TestEvent): Unit = {
    writeFailure(event)
    if (enableServer && event.detail.exists(e => e.status == Failure)) {
      call(vimExec, "cfile %s".format(output.toString))
    }
  }
 
  def endGroup(name: String, t: Throwable): Unit = {}

  def endGroup(name: String, v: Value): Unit = {}

  def writeFailure(event: TestEvent): Unit =
    for {
      detail <- event.detail
      if writeable(detail)
      (file, line) <- find(detail.throwable.get) 
    } append(output, "error", file, line, detail.throwable.get.getMessage)

  def writeable(detail: Event): Boolean =
    detail.status == Failure && detail.throwable.isDefined

  def find(error: Throwable): Option[(File, Int)] = error match {
    case e: { def failedCodeStackDepth: Int } => 
      try {
        val stackTrace = error.getStackTrace()(e.failedCodeStackDepth)
        for { 
          file <- findSource(stackTrace.getFileName) 
        } yield (file, stackTrace.getLineNumber)
      } catch {
        case _ => 
          findInStackTrace(error.getStackTrace)
      }
  }

  def findInStackTrace(trace: Array[StackTraceElement]): Option[(File, Int)] = 
    { for {
      elem <- trace
      file <- findSource(elem.getFileName)
    } yield (file, elem.getLineNumber) }.headOption

  def findSource(name: String): Option[File] =
    srcFiles find { file => file.getName endsWith name }
}

object QuickFixTestListener {
  def apply(output: File, srcFiles: Seq[File], vimExec: String, enableServer: Boolean): TestReportListener =
    new QuickFixTestListener(output, srcFiles, vimExec, enableServer)
}
