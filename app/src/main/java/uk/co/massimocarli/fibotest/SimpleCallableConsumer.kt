package uk.co.massimocarli.fibotest

import android.app.Activity
import java.util.concurrent.Callable
import java.util.function.Consumer
import kotlin.concurrent.thread

class SimpleCallableConsumer<V>(val context: Activity) : CallableConsumer<V> {
  override fun execute(bgCallable: Callable<V>, uiConsumer: Consumer<V>) {
    thread {
      val result = bgCallable.call()
      context.runOnUiThread {
        uiConsumer.accept(result)
      }
    }
  }
}