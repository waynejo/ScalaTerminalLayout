package com.waynejo.terminal.terminal.manager

import com.waynejo.terminal.channel.Channel

class RuntimeManager() {
  val channel = new RuntimeManager.Channels(arr => {
    Runtime.getRuntime.exec(arr)
  })
}

object RuntimeManager {
  type Channels = Channel[Array[String], Unit]
}