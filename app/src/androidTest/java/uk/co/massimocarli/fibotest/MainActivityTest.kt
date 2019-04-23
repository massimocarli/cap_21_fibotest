package uk.co.massimocarli.fibotest

import android.app.Activity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Semaphore
import java.util.function.Consumer
import kotlin.concurrent.thread

class MainActivityTest {

  @get:Rule
  var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

  @Before
  fun setUp() {
    //activityRule.activity.fibonacciCalculator =
  }

  @Test
  fun calculateFibo_whenInputIs42_OutputIs267914296() {
    onView(withId(R.id.numberInput)).perform(typeText("42"))
    onView(withId(R.id.calculateButton)).perform(click())
    Espresso.closeSoftKeyboard()
    onView(withId(R.id.fiboResult)).check(matches(withText(("267914296"))))
  }

  @Test
  fun calculateFiboWithSleep_whenInputIs42_OutputIs267914296() {
    onView(withId(R.id.numberInput)).perform(typeText("42"))
    onView(withId(R.id.calculateButton)).perform(click())
    Espresso.closeSoftKeyboard()
    Thread.sleep(5000)
    onView(withId(R.id.fiboResult)).check(matches(withText(("267914296"))))
  }

  @Test
  fun calculateFiboWithSemaphore_whenInputIs42_OutputIs267914296() {
    val semaphore = Semaphore(0)
    activityRule.activity.callableConsumer =
      SemaphoreCallableConsumer<Int>(activityRule.activity, semaphore)
    onView(withId(R.id.numberInput)).perform(typeText("42"))
    onView(withId(R.id.calculateButton)).perform(click())
    Espresso.closeSoftKeyboard()
    semaphore.acquire()
    onView(withId(R.id.fiboResult)).check(matches(withText(("267914296"))))
  }

  @Test
  fun calculateFiboWithCountDownLatch_whenInputIs42_OutputIs267914296() {
    val countDownLatch = CountDownLatch(1)
    activityRule.activity.callableConsumer =
      CountDownLatchCallableConsumer<Int>(activityRule.activity, countDownLatch)
    onView(withId(R.id.numberInput)).perform(typeText("42"))
    onView(withId(R.id.calculateButton)).perform(click())
    Espresso.closeSoftKeyboard()
    countDownLatch.await()
    onView(withId(R.id.fiboResult)).check(matches(withText(("267914296"))))
  }

  @Test
  fun calculateFiboWithIdleResource_whenInputIs42_OutputIs267914296() {
    val countingResource = CountingIdlingResource("FibonacciIdleResource")
    activityRule.activity.fibonacciCalculator = IdleResourceFibonacciCalculatorDecorator(
      RecFibonacciCalculatorImpl(),
      countingResource
    )
    IdlingRegistry.getInstance().register(countingResource)
    onView(withId(R.id.numberInput)).perform(typeText("42"))
    onView(withId(R.id.calculateButton)).perform(click())
    Espresso.closeSoftKeyboard()
    onView(withId(R.id.fiboResult)).check(matches(withText(("267914296"))))
    IdlingRegistry.getInstance().unregister(countingResource)
  }
}


class SemaphoreCallableConsumer<V>(
  val context: Activity,
  val semaphore: Semaphore
) : CallableConsumer<V> {
  override fun execute(bgCallable: Callable<V>, uiConsumer: Consumer<V>) {
    thread {
      val result = bgCallable.call()
      context.runOnUiThread {
        uiConsumer.accept(result)
        semaphore.release()
      }
    }
  }
}


class CountDownLatchCallableConsumer<V>(
  val context: Activity,
  val countDownLatch: CountDownLatch
) : CallableConsumer<V> {
  override fun execute(bgCallable: Callable<V>, uiConsumer: Consumer<V>) {
    thread {
      val result = bgCallable.call()
      context.runOnUiThread {
        uiConsumer.accept(result)
        countDownLatch.countDown()
      }
    }
  }
}

class IdleResourceFibonacciCalculatorDecorator(
  val fibonacciCalculator: FibonacciCalculator,
  val countingIdlingResource: CountingIdlingResource
) : FibonacciCalculator {
  override fun fib(n: Int): Int {
    countingIdlingResource.increment();
    try {
      return fibonacciCalculator.fib(n)
    } finally {
      countingIdlingResource.decrement();
    }
  }
}
