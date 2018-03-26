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
    new Channel[Unit, Boolean]((Unit) => {
      isClosed
    }),
    new Channel[Unit, Either[Unit, Option[Byte]]]((Unit) => {
      Right(Some(0))
    })
  )
}

object StdInManager {
  case class Channels(isClosed: Channel[Unit, Boolean], read: Channel[Unit, Either[Unit, Option[Byte]]])
}

