package uk.co.massimocarli.fibotest

import java.util.concurrent.Callable
import java.util.function.Consumer

interface CallableConsumer<V> {

  /**
   * Executes a Callable
   */
  fun execute(bgCallable: Callable<V>, uiConsumer: Consumer<V>)
}