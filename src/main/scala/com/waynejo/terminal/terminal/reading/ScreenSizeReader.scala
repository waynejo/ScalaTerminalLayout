package com.waynejo.terminal.terminal.reading

import com.waynejo.terminal.layout.{XAxis, YAxis}
import com.waynejo.terminal.unit.Size

class ScreenSizeReader extends ((Array[Byte], Int) => Option[String]) {
  override def apply(v1: Array[Byte], v2: Int): Option[String] = {
    val end = v1.length
    def parse(idx: Int, terminatorCount: Int, acc: String): Option[String] = {
      if (idx == end) {
        None
      } else {
        val ch = v1(idx).toChar
        if ('0' <= ch && '9' >= ch) {
          parse(idx + 1, terminatorCount, acc + ch)
        } else if (';' == ch && terminatorCount < 2) {
          parse(idx + 1, terminatorCount + 1, acc + ch)
        } else if ('t' == ch && terminatorCount == 2) {
          Some(acc + ch)
        } else {
          None
        }
      }
    }

    parse(v2, 0, "")
  }
}

object ScreenSizeReader {
  def parse(strings: String): Size = {
    println()
    println(strings)
    println()
    val Array(_, y, x) = strings.init.split(";")
    Size(XAxis(x.toInt), YAxis(y.toInt))
  }
}
