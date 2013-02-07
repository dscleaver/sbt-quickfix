package org.dscleaver.sbt

import sbt._
import Keys._
import quickfix.QuickFixLogger

object SbtQuickFix extends Plugin {

  object QuickFixKeys {
    val quickFixDirectory = target in config("quickfix")
  }

  import QuickFixKeys._

  override val settings = Seq(
   quickFixDirectory <<= target / "quickfix",
   extraLoggers <<= (quickFixDirectory, extraLoggers) apply { (target, currentFunction) =>
      (key: ScopedKey[_]) => {
        val loggers = currentFunction(key)
        if(key.scope.task.toOption.get.label equals "compile")
          new QuickFixLogger(target / "sbt.quickfix") +: loggers
        else
          loggers
      }
    }
  )
        
}
