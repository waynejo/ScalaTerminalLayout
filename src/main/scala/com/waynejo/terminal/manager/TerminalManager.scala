package com.waynejo.terminal.manager

import java.io.{BufferedOutputStream, OutputStream}

import com.waynejo.terminal.future.Async
import com.waynejo.terminal.layout.{Layout, LayoutBuilder, XAxis, YAxis}
import com.waynejo.terminal.terminal._
import com.waynejo.terminal.unit.Size

class TerminalManager(runtimeChannel: RuntimeManager.Channel, channel: Terminal.Channel, output: OutputStream = new BufferedOutputStream(System.out)) {

  runtimeChannel.emit(Array("sh", "-c", "stty raw -echo < /dev/tty"))

  channel.onNext(commands => {
    commands.foreach(printCommand(output))
    output.flush()

    reedScreenSize() match {
      case Right((x, y)) =>
        Size(XAxis(x), YAxis(y))
      case _ =>
        Size(XAxis(0), YAxis(0))
    }
  })

  channel.onClose(() => {
    runtimeChannel.emit(Array("sh", "-c", "stty -raw echo < /dev/tty"))
  })

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

  private def printCommand(output: OutputStream)(command: TerminalCommand): Unit = {
    command match {
      case CSI(text) => output.write(s"\033[$text".getBytes)
      case Text(text) => output.write(text.getBytes)
    }
  }
}