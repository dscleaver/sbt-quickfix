package com.dscleaver.sbt.quickfix

import sbt._

object VimInteraction {
  def call(vimExec: String, command: Seq[String]): Int = Process(List(vimExec, "--remote-send") ++ command).!

  def call(vimExec: String, command: String): Int = call(vimExec, List(command))
}
