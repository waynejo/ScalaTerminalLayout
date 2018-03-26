package com.waynejo.terminal.layout

sealed trait Axis[T]

case class XAxis[T](value: T) extends Axis[T]
case class YAxis[T](value: T) extends Axis[T]
