package com.dscleaver.sbt.quickfix

import scala.sys.process._

object VimInteraction {
  def call(vimExec: String, command: Seq[String]): Int = Process(List(vimExec, "--remote-send") ++ command).!

  def call(vimExec: String, command: String): Int = call(vimExec, List(command))
}
