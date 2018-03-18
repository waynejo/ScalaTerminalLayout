package com.waynejo.terminal.terminal

class CommandBuilder(val command: Vector[TerminalCommand]) {
  def clear(): CommandBuilder = {
    CommandBuilder(command :+ CSI("2J"))
  }

  def moveTo(x: Int, y: Int): CommandBuilder = {
    CommandBuilder(command :+ CSI(s"${y + 1};${x + 1}H"))
  }

  def text(s: String): CommandBuilder = {
    CommandBuilder(command :+ Text(s))
  }

  def build(): Vector[TerminalCommand] = {
    command
  }


  def hideCursor(): CommandBuilder = {
    CommandBuilder(command :+ CSI("?25l") :+ CSI("?47h"))
  }

  def getScreenSize(): CommandBuilder = {
    CommandBuilder(command :+ CSI("18t"))
  }
}

object CommandBuilder {
  def apply(command: Vector[TerminalCommand] = Vector()): CommandBuilder = {
    new CommandBuilder(command)
  }
}