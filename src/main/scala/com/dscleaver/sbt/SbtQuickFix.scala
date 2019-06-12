package com.dscleaver.sbt

import sbt._
import Keys._
import quickfix.{ QuickFixAppender, VimPlugin, QuickFixTestListener }
import sbt.internal.util.ConsoleAppender

object SbtQuickFix extends AutoPlugin {

  object QuickFixKeys {
    val quickFixDirectory = SettingKey[File]("quickfix-directory", "The directory that the sbt.quickfix file will be placed in.")
    val quickFixInstall = TaskKey[Unit]("install-vim-plugin")
    val quickFixIgnoreErrors = SettingKey[Boolean]("quickfix-ignore-errors", "If exceptions are thrown in the quickfix file writer, ignore them when this is true. (defaults to true)")
    val vimEnableServer = SettingKey[Boolean]("vim-enable-server", "Enables communication with the Vim server - requires that Vim has been compiled with +clientserver")
    val vimExecutable = SettingKey[String]("vim-executable", "The path to the vim executable, or just 'vim' if it's in the PATH already")
    val vimPluginBaseDirectory = SettingKey[File]("vim-plugin-directory", "The path where vim plugins should be installed")
  }

  import QuickFixKeys._

  override def trigger = allRequirements

  override val projectSettings = Seq(
    quickFixDirectory := (target (_ / "quickfix")).value,
    vimPluginBaseDirectory in ThisBuild := file(System.getProperty("user.home")) / ".vim" / "bundle",
    vimEnableServer in ThisBuild := true,
    quickFixIgnoreErrors in ThisBuild := true,
    extraLoggers := { 
      (key: ScopedKey[_]) => {
        val loggers = extraLoggers.value(key)
        val taskOption = key.scope.task.toOption
        if (taskOption.exists(_.label.startsWith("compile"))) {
          val appender = new QuickFixAppender(
            quickFixDirectory.value / "sbt.quickfix", 
            vimExecutable.value, vimEnableServer.value, 
            quickFixIgnoreErrors.value, 
            ConsoleAppender("sbt-vim-quickfix-error")
          ) 
        
          appender +: loggers
        }
        else
          loggers
      }
    },
    testListeners += QuickFixTestListener(quickFixDirectory.value / "sbt.quickfix", (sources in Test).value, vimExecutable.value, vimEnableServer.value),
    quickFixInstall in ThisBuild := VimPlugin.install(vimPluginBaseDirectory.value, streams.value),
    vimExecutable in ThisBuild := (if (System.getProperty("os.name").startsWith("Win")) "gvim.bat" else "gvim")
  )
}
