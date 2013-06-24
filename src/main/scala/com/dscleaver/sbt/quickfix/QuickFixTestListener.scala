package com.dscleaver.sbt.quickfix

import sbt._
import sbt.TestResult.Value
import org.scalatools.testing.Result._

class QuickFixTestListener(output: File, srcFiles: Seq[File]) extends TestReportListener {

  IO.delete(output)
  IO.touch(List(output))

  def startGroup(name: String): Unit = {}

  def testEvent(event: TestEvent): Unit =
    writeFailure(event)
 
  def endGroup(name: String, t: Throwable): Unit = {}

  def endGroup(name: String, v: Value): Unit = {}

  def writeFailure(event: TestEvent): Unit = 
    for {
      detail <- event.detail
      if detail.result == Failure
      (file, line) <- find(detail.error) 
    } IO.append(output, "[error] " + file + ":" + line + ": " + detail.error.getMessage + "\n")
  

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

  def apply(output: File, srcFiles: Seq[File]): TestReportListener =
    new QuickFixTestListener(output, srcFiles)
}
