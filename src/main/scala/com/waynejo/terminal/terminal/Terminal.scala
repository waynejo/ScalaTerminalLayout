package com.waynejo.terminal.terminal

import com.waynejo.terminal.future.Async
import com.waynejo.terminal.layout.{Layout, LayoutBuilder, XAxis, YAxis}
import com.waynejo.terminal.unit.Size

class Terminal(layoutBuilder: LayoutBuilder) {

  val channel: Terminal.Channel = new Terminal.Channel(() => {
    CommandBuilder().hideCursor().build()
  })

  def step(size: Size): Vector[TerminalCommand] = {
    clearScreen()

    layoutBuilder.build(Layout(XAxis(0), YAxis(0), size.width, size.height, Nil)).flatMap(printLayout).toVector
  }

  def run(): Unit = {
    channel.emit(step(Size(XAxis(0), YAxis(0))))
  }

  private def clearScreen(){
    print(CommandBuilder().clear().build())
  }

  private def printLayout(layout: Layout): Vector[TerminalCommand] = {
    CommandBuilder().moveTo(layout.left.value, layout.top.value).build()
  }
}

object Terminal {
  type Channel = Async[Vector[TerminalCommand], Size]
}