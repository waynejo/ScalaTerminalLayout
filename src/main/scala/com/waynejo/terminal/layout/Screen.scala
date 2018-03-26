package com.waynejo.terminal.layout

import com.waynejo.terminal.unit.Size

case class Screen(layouts: List[Layout], layoutBuilder: (XAxis[Int], YAxis[Int], XAxis[Int], YAxis[Int]) => List[Layout]) {
  def update(size: Size): Screen = {
    copy(layouts = layoutBuilder(XAxis(0), YAxis(0), size.width, size.height))
  }
}

object Screen {
  def apply(layoutBuilder: (XAxis[Int], YAxis[Int], XAxis[Int], YAxis[Int]) => List[Layout]): Screen = {
    Screen(Nil, layoutBuilder)
  }
}
