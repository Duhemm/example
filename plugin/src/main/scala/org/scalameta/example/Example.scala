package org.scalameta.example

import scala.meta.internal.ast._
import scala.meta.semantic._
import scala.meta.semantic.errors.throwExceptions

trait Example {
  val global: scala.tools.nsc.Global
  implicit val host = scala.meta.internal.hosts.scalac.Scalahost(global)

  def example(sources: List[Source]): Unit = {
    // ...
  }
}