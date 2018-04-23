package com.waynejo.terminal.channel

import java.util.concurrent.locks.{Lock, ReentrantLock}

case class BlockingBox[T]() {
  var value: Option[T] = None
  val lock: Lock = new ReentrantLock()

  def set(value: T): Unit = synchronized {
    this.value = Some(value)

    notifyAll()
  }

  def get(): T = synchronized {
    value match {
      case Some(v) =>
        v
      case None =>
        wait()
        get()
    }
  }
}
