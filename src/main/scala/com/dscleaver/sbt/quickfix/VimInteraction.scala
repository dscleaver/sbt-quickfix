package com.dscleaver.sbt.quickfix

import sbt._

object VimInteraction {

  lazy val vimServerName: String = sys.props.get("sbtquickfix.vim.servername").getOrElse("SBT_QUICKFIX")
  lazy val nvimSocketFile: String = sys.props.get("sbtquickfix.nvim.socket").getOrElse("/tmp/nvim_sbt_quickfix.sock")

  def call(vimExec: String, command: String): Int = vimExec match {
    case "nvim" => Process(List("python", "-c", pyScript(command))).! // TODO revisit when neovim #1750 is resolved
    case _      => Process(List(vimExec, "--servername", vimServerName, "--remote-send", s"<esc>:$command<cr>")).!
  }

  private def pyScript(cmd: String) = 
     s"""|from neovim import attach
         |nvim = attach('socket', path='$nvimSocketFile')
         |nvim.command('$cmd')""".stripMargin
}
