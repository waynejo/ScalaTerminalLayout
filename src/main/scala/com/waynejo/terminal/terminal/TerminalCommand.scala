package com.waynejo.terminal.terminal

trait TerminalCommand
case class CSI(command: String) extends TerminalCommand
case class Text(text: String) extends TerminalCommand
