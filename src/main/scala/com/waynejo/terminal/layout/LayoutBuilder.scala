package com.waynejo.terminal.layout

case class LayoutBuilder(layouts: List[Layout] = Nil) {
  def flatMap(builder: Layout => LayoutBuilder): LayoutBuilder = {
    if (layouts.isEmpty) {
      LayoutBuilder(layouts)
    } else {
      LayoutBuilder(builder(layouts.head).layouts ::: layouts)
    }
  }

  def map(func: Layout => Layout): LayoutBuilder = {
    LayoutBuilder(func(layouts.head) :: layouts.tail)
  }

  def build(layout: Layout): List[Layout] = {
    List(layout)
  }
}

object LayoutBuilder {
  def apply(layout: Layout): LayoutBuilder = {
    LayoutBuilder(layout :: Nil)
  }
}