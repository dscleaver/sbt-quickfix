package com.dscleaver.sbt.quickfix

import java.io.File
import sbt._
import std._

object VimPlugin {
  def install(baseDirectory: File, s: TaskStreams[ScopedKey[_]]): Unit = {
    val plugin = baseDirectory / "vim-sbt"
    if(plugin.exists) {
      s.log.info("Removing previous installation at " + plugin)
      IO.delete(plugin)
    }
    s.log.info("Installing to " + plugin)
    IO.createDirectory(plugin)
    val jar = IO.classLocationFile[VimPlugin.type]
    val files = IO.unzip(jar, baseDirectory, "vim-sbt/*")
  }
}
