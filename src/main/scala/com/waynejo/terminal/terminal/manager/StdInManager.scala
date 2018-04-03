package com.waynejo.terminal.terminal.manager

import com.waynejo.terminal.channel.Channel

class StdInManager() {
  private var isClosed = false
  private var inputs = Array[Byte]()

  new Thread(() => {
    while (!isClosed) {
      val value = System.in.read()
      if (-1 != value) {
        isClosed = true
      } else {
        this.synchronized {
          inputs = inputs :+ value.toByte
        }
        isClosed = true
      }
    }
  }).start()

  val channel = StdInManager.Channels(
    new Channel[Unit, Boolean]((_) => {
      isClosed
    }),
    new Channel[(Array[Byte]) => Option[Array[Byte]], Either[Unit, Option[Array[Byte]]]]((readFunc) => {
      readFunc(inputs) match {
        case Some(x) =>
          inputs = inputs.drop(x.length)
          Right(Some(x))
        case None =>
          Right(None)
      }
    })
  )
}

object StdInManager {
  case class Channels(isClosed: Channel[Unit, Boolean], read: Channel[(Array[Byte]) => Option[Array[Byte]], Either[Unit, Option[Array[Byte]]]])
}

