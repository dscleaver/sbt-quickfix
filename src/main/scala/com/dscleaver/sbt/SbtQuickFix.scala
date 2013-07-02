package com.dscleaver.sbt

import sbt._
import Keys._
import sbt.IO._
import quickfix.{QuickFixLogger, VimPlugin, QuickFixTestListener}

object SbtQuickFix {

  object QuickFixKeys {
    val quickFixDirectory = target in config("quickfix")
    val quickFixInstall = TaskKey[Unit]("install-vim-plugin")
    val vimExecutable = SettingKey[String]("vim-executable", "The path to the vim executable, or just 'vim' if it's in the PATH already")
    val vimPluginBaseDirectory = baseDirectory in quickFixInstall
  }

  import QuickFixKeys._

  val settings = Seq(
    quickFixDirectory <<= target / "quickfix",
    vimPluginBaseDirectory := file(System.getenv("HOME")) / ".vim" / "bundle",
    extraLoggers <<= (quickFixDirectory, extraLoggers, vimExecutable) apply { (target, currentFunction, vimExec) =>
      (key: ScopedKey[_]) => {
        val loggers = currentFunction(key)
        if(key.scope.task.toOption.get.label equals "compile")
          new QuickFixLogger(target / "sbt.quickfix", vimExec) +: loggers
        else
          loggers
      }
    },
    testListeners <+= (quickFixDirectory, sources in Test, vimExecutable) map { (target, testSources, vimExec) => 
      QuickFixTestListener(target / "sbt.quickfix", testSources, vimExec)
    },
    quickFixInstall <<= (vimPluginBaseDirectory, streams) map VimPlugin.install,
    vimExecutable := "vim"
  )

}
