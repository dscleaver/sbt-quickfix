package com.dscleaver.sbt

import sbt._
import Keys._
import sbt.IO._
import quickfix.QuickFixLogger
import quickfix.VimPlugin

object SbtQuickFix extends Plugin {

  object QuickFixKeys {
    val quickFixDirectory = target in config("quickfix")
    val quickFixInstall = TaskKey[Unit]("install-vim-plugin")
    val vimPluginBaseDirectory = baseDirectory in quickFixInstall
  }

  import QuickFixKeys._

  override  val settings = Seq(
    quickFixDirectory <<= target / "quickfix",
    vimPluginBaseDirectory := file(System.getenv("HOME")) / ".vim" / "bundle",
    extraLoggers <<= (quickFixDirectory, extraLoggers) apply { (target, currentFunction) =>
      (key: ScopedKey[_]) => {
        val loggers = currentFunction(key)
        if(key.scope.task.toOption.get.label equals "compile")
          new QuickFixLogger(target / "sbt.quickfix") +: loggers
        else
          loggers
      }
    },
    testListeners <+= (quickFixDirectory) apply QuickFixTestListener
    quickFixInstall <<= (vimPluginBaseDirectory, streams) map VimPlugin.install
   
  )

}
