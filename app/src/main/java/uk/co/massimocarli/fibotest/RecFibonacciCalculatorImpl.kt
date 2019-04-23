package uk.co.massimocarli.fibotest


@OpenForTest
class RecFibonacciCalculatorImpl : FibonacciCalculator {
  override fun fib(n: Int): Int = when (n) {
    0, 1 -> n
    else -> fib(n - 1) + fib(n - 2)
  }
}


fun main() {
  val fc = RecFibonacciCalculatorImpl();
  (1 until 20).forEach {
    println(" $it -> ${fc.fib(it)}")
  }
}