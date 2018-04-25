package com.waynejo.terminal.terminal.manager

import java.util.concurrent.LinkedBlockingQueue

import com.waynejo.terminal.channel.{BlockingBox, Channel}
import com.waynejo.terminal.terminal.manager.StdInManager.ReadFunc

class StdInManager() {
  private var isClosed = false
  private var inputs = Array[Byte]()
  private val requestQueue = new LinkedBlockingQueue[(ReadFunc, BlockingBox[String])]()

  def tryReadingFunction(func: ReadFunc, idx: Int = 0): Option[String] = {
    if (idx >= inputs.length) {
      None
    } else {
      func(inputs, idx) match {
        case some@Some(_) =>
          some
        case _ =>
          tryReadingFunction(func, idx + 1)
      }
    }
  }

  new Thread(() => {
    while (!isClosed) {
      val value = System.in.read()
      if (-1 == value) {
        isClosed = true
      } else if (!requestQueue.isEmpty) {
        inputs = inputs :+ value.toByte
        val (func, blockingBox) = requestQueue.peek()
        tryReadingFunction(func) match {
          case Some(parsedValue) =>
            requestQueue.poll()
            inputs = Array()

            blockingBox.set(parsedValue)
          case _ =>
        }
      }
    }
  }).start()

  val channel = StdInManager.Channels(
    new Channel[Unit, Boolean]((_) => {
      isClosed
    }),
    new Channel[ReadFunc, String]((readFunc) => {
      val value = BlockingBox[String]()
      requestQueue.add((readFunc, value))
      value.get()
    })
  )
}

object StdInManager {
  type ReadFunc = (Array[Byte], Int) => Option[String]

  case class Channels(isClosed: Channel[Unit, Boolean], read: Channel[ReadFunc, String])
}

