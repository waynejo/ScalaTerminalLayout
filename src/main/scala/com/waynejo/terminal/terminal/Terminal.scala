package com.waynejo.terminal.terminal

import java.io.{BufferedOutputStream, OutputStream}

class Terminal(output: OutputStream = new BufferedOutputStream(System.out)) {
  def print(commands: Vector[TerminalCommand]): Unit = {
    commands.foreach(printCommand(output))
    output.flush()
  }

  private def printCommand(output: OutputStream)(command: TerminalCommand): Unit = {
    command match {
      case CSI(text) => output.write(s"\033[$text".getBytes)
      case Text(text) => output.write(text.getBytes)
    }
  }
}
