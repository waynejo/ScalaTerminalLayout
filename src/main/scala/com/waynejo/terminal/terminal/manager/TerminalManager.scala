package com.waynejo.terminal.terminal.manager

import java.io.{BufferedOutputStream, OutputStream}

import com.waynejo.terminal.layout.{Layout, LayoutBuilder}
import com.waynejo.terminal.terminal._

class TerminalManager(layoutBuilder: LayoutBuilder, callback: () => Vector[TerminalCommand], runtimeChannel: RuntimeManager.Channels, stdInManager: StdInManager.Channels, output: OutputStream = new BufferedOutputStream(System.out)) {

  def step(state: TerminalState.Value): Vector[TerminalCommand] = {
    state match {
      case TerminalState.INIT =>
        CommandBuilder().hideCursor().build()
      case TerminalState.STEP =>
        CommandBuilder().clear().build() ++ callback()
      case TerminalState.CLOSE =>
        CommandBuilder().showCursor().build()
    }
  }

  def run(): Unit = {
    print(CommandBuilder().hideCursor().build())
    runtimeChannel.emit(Array("sh", "-c", "stty raw -echo < /dev/tty"))

    print(step(TerminalState.INIT))

    while (stdInManager.isClosed.emit(Unit).getOrElse(true)) {
      print(step(TerminalState.STEP))
    }

    print(CommandBuilder().showCursor().build())
    runtimeChannel.emit(Array("sh", "-c", "stty sane < /dev/tty"))
  }

  private def clearScreen(){
    print(CommandBuilder().clear().build())
  }

  private def printLayout(layout: Layout){
    val commands = CommandBuilder().moveTo(layout.left.value, layout.top.value).build()
    print(commands :+ Text("hello"))
  }

  private def readByte(): Either[Boolean, Int] = {
    val value = System.in.read()
    if (-1 == value) {
      Left(false)
    } else {
      Right(value)
    }
  }

  private def readByteUntil(terminator: Char): Either[Boolean, Int] = {
    readByte().flatMap(x => {
      if (x == terminator) {
        Right(0)
      } else {
        readByteUntil(terminator).map(y => x + y)
      }
    })
  }

  private def reedScreenSize(): Either[Boolean, (Int, Int)] = {
    print(CommandBuilder().getScreenSize().build())
    System.in.read()
    System.in.read()

    var ch = System.in.read()
    while (';' != ch) {
      ch = System.in.read()
    }

    ch = System.in.read()
    var width = 0
    while (';' != ch) {
      width = width + ch
      ch = System.in.read()
    }

    var height = 0
    ch = System.in.read()
    while ('t' != ch) {
      height = height + ch
      ch = System.in.read()
    }
//    println(width)
//    println(height)
    //(width, height)
    Left(true)
  }

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
