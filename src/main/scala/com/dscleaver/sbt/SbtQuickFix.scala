package com.dscleaver.sbt

import sbt._
import Keys._
import sbt.IO._
import quickfix.{QuickFixLogger, VimPlugin, QuickFixTestListener}

object SbtQuickFix extends Plugin {

  object QuickFixKeys {
    val quickFixDirectory = target in config("quickfix")
    val quickFixInstall = TaskKey[Unit]("install-vim-plugin")
    val vimEnableServer = SettingKey[Boolean]("vim-enable-server", "Enables communication with the Vim server - requires that Vim has been compiled with +clientserver")
    val vimExecutable = SettingKey[String]("vim-executable", "The path to the vim executable, or just 'vim' if it's in the PATH already")
    val vimPluginBaseDirectory = baseDirectory in quickFixInstall
  }

  import QuickFixKeys._

  override val projectSettings = Seq(
    quickFixDirectory in ThisBuild <<= target / "quickfix",
    vimPluginBaseDirectory in ThisBuild := file(System.getenv("HOME")) / ".vim" / "bundle",
    vimEnableServer in ThisBuild := true,
    extraLoggers <<= (quickFixDirectory, extraLoggers, vimExecutable, vimEnableServer) apply { (target, currentFunction, vimExec, enableServer) =>
      (key: ScopedKey[_]) => {
        val loggers = currentFunction(key)
        if(key.scope.task.toOption.get.label equals "compile")
          new QuickFixLogger(target / "sbt.quickfix", vimExec, enableServer) +: loggers
        else
          loggers
      }
    },
    testListeners <+= (quickFixDirectory, sources in Test, vimExecutable, vimEnableServer) map { (target, testSources, vimExec, enableServer) => 
      QuickFixTestListener(target / "sbt.quickfix", testSources, vimExec, enableServer)
    },
    quickFixInstall in ThisBuild <<= (vimPluginBaseDirectory, streams) map VimPlugin.install,
    vimExecutable in ThisBuild := "gvim"
  )
}
