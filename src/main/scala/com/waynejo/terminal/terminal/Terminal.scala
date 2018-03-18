package com.waynejo.terminal.terminal

import java.io.{BufferedOutputStream, OutputStream}

import com.waynejo.terminal.layout.{Layout, LayoutBuilder, XAxis, YAxis}

class Terminal(layoutBuilder: LayoutBuilder, output: OutputStream = new BufferedOutputStream(System.out)) {

  print(CommandBuilder().hideCursor().build())
  Runtime.getRuntime.exec(Array("sh", "-c", "stty raw -echo < /dev/tty"))

  def step(): Unit = {
    clearScreen()

    layoutBuilder.build(Layout(XAxis(0), YAxis(0), XAxis(100), YAxis(100), Nil)).foreach(printLayout)

    val (width, height) = reedScreenSize()
  }

  private def clearScreen(){
    print(CommandBuilder().clear().build())
  }

  private def printLayout(layout: Layout){
    val commands = CommandBuilder().moveTo(layout.left.value, layout.top.value).build()
    print(commands :+ Text("hello"))

  }

  private def reedScreenSize(): (Int, Int) = {
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
    (width, height)
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
