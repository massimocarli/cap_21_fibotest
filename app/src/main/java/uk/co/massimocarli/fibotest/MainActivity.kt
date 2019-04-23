package uk.co.massimocarli.fibotest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Callable
import java.util.function.Consumer

class MainActivity : AppCompatActivity() {

  var callableConsumer: CallableConsumer<Int> = SimpleCallableConsumer<Int>(this)
  var fibonacciCalculator: FibonacciCalculator = RecFibonacciCalculatorImpl()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    calculateButton.setOnClickListener {
      callableConsumer.execute(
        Callable {
          val input = Integer.parseInt(numberInput.text.toString())
          fibonacciCalculator.fib(input)
        },
        Consumer<Int> { fiboResult.text = "${it}" }
      )
    }
  }
}
