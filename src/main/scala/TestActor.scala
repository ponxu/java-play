package com.ponxu

import scala.actors.Actor._
import scala.actors.Channel

/**
  * @author xuwenzhao on 2017-08-23
  */
object TestActor {
  def main(args: Array[String]): Unit = {
    // testActor()
    // testChannel()
  }

  def testChannel(): Unit = {
    val channel = new Channel[Int]()

    val actor1 = actor {
      while (true) {
        receive {
          case x: Int => channel ! x * x
          case x => println("??: " + x)
        }
      }
    }.start()

    actor1 ! 2
    actor1 ! 4
    actor1 ! "xx"

    while (true) {
      channel.receive {
        case x => println("result: " + x)
      }
    }
  }

  def testActor(): Unit = {
    val actor1 = actor {
      while (true) {
        receive {
          case "Test" => println(Thread.currentThread().getName + " Test.")
          case _ => println(Thread.currentThread().getName + "???.")
        }
      }
    }.start()

    actor1 ! "Test"
    actor1 ! "Hello"
  }
}
