package com.waynejo.terminal.terminal.manager

import java.io.{BufferedOutputStream, OutputStream}

import com.waynejo.terminal.layout.{LayoutBuilder, XAxis, YAxis}
import com.waynejo.terminal.terminal._
import com.waynejo.terminal.terminal.reading.ScreenSizeReader
import com.waynejo.terminal.unit.Size

class TerminalManager(
    layoutBuilder: LayoutBuilder,
    callback: (TerminalManager) => Vector[TerminalCommand],
    runtimeChannel: RuntimeManager.Channels,
    stdInManager: StdInManager.Channels,
    updateDurationMs: Int = 100,
    output: OutputStream = new BufferedOutputStream(System.out)
  ) {

  private var isRunningState = false
  private var isClosingNeeded = false


  private def print(commands: Vector[TerminalCommand]): Unit = {
    commands.foreach(printCommand(output))
    output.flush()
  }

  private def printCommand(output: OutputStream)(command: TerminalCommand): Unit = {
    command match {
      case CSI(text) => output.write(s"\033[$text".getBytes)
      case Text(text) => output.write(text.getBytes)
    }
  }

  private def step(state: TerminalState.Value): Vector[TerminalCommand] = {
    state match {
      case TerminalState.INIT =>
        CommandBuilder().hideCursor().getScreenSize().build()
      case TerminalState.STEP =>
        CommandBuilder().getScreenSize().clear().moveTo(0, 0).build()
      case TerminalState.CLOSE =>
        CommandBuilder().showCursor().build()
    }
  }

  def run(): Unit = {
    isClosingNeeded = false
    isRunningState = true

    new Thread {
      runtimeChannel.emit(Array("sh", "-c", "stty raw -echo < /dev/tty"))
      print(step(TerminalState.INIT))
      var screenSize = stdInManager.read.emit(new ScreenSizeReader).map(ScreenSizeReader.parse).getOrElse(Size(XAxis(1), YAxis(1)))
      println(screenSize)


      try {
        while (!stdInManager.isClosed.emit(Unit).getOrElse(true) || isClosingNeeded) {
          print(step(TerminalState.STEP))
          screenSize = stdInManager.read.emit(new ScreenSizeReader).map(ScreenSizeReader.parse).getOrElse(Size(XAxis(1), YAxis(1)))
          println(screenSize)
          print(callback(TerminalManager.this))

          Thread.sleep(updateDurationMs)
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
      }

      print(CommandBuilder().showCursor().build())
      runtimeChannel.emit(Array("sh", "-c", "stty sane < /dev/tty"))

      isRunningState = false
    }.start()
  }

  def isRunning: Boolean = {
    isRunningState
  }

  def close(): Unit = {
    isClosingNeeded = true
  }
}
