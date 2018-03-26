package com.waynejo.terminal.channel

case class Channel[A, B](nextHandler: A => B) {

  private var isClosed: Boolean = false

  def emit(value: A): Option[B] = {
    if (isClosed) {
      None
    } else {
      Some(nextHandler(value))
    }
  }

  def close(): Unit = {
    isClosed = true
  }
}


