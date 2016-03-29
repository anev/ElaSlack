package ru.evseev.elaslack

import java.util.concurrent.atomic.AtomicInteger

/**
  * Created by anev on 29/03/16.
  */
trait IdGenerator {

  val num: AtomicInteger = new AtomicInteger(0)

  def generateId: Int = num.incrementAndGet()

}
