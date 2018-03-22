package com.waynejo.terminal.manager

import com.waynejo.terminal.future.Async

class RuntimeManager(channel: RuntimeManager.Channel) {
  channel.onNext(arr => {
    Runtime.getRuntime.exec(arr)
  })
}

object RuntimeManager {
  type Channel = Async[Array[String], Unit]
}