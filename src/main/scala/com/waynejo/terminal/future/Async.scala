package com.waynejo.terminal.future

case class Async[A, B](init: () => A) {

  var isClosed: Boolean = false
  var nextHandler: Option[A => B] = None
  var closeHandler: Option[() => Unit] = None

  def onNext(nextHandler: A => B) {
    this.nextHandler = Some(nextHandler)
  }

  def onClose(closeHandler: () => Unit) {
    this.closeHandler = Some(closeHandler)
  }

  def emit(value: A): Option[B] = {
    if (nextHandler.isDefined) {
      Some(nextHandler.get(value))
    } else {
      None
    }
  }

  def close(): Unit = {
    isClosed = true

    closeHandler.foreach(handler => handler())

    nextHandler = None
    closeHandler = None
  }
}


