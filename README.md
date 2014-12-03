### An example of using pre-alpha scala.meta APIs

While the M1 release of scala.meta is [still not there](http://scalamacros.org/news/2014/11/30/state-of-the-meta-fall-2014.html), there are definitely some experiments that can be performed with our APIs. We've put up this project to show what can be done and what's the infrastructural work required to get things working.

#### Overview

At the moment, scala.meta trees are only available to compiler plugins that run after the `convert` phase provided by [scalahost](https://github.com/scalameta/scalahost). This project is a compiler plugin that depends on scalahost and provides a phase that runs after `convert` and calls into custom logic from [Example.scala](https://github.com/scalameta/example/blob/master/plugin/src/main/scala/org/scalameta/example/Example.scala).

```
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
```

The infrastructure of `Example` does the following:
  * `import scala.meta.internal.ast._` brings [internal representation](https://github.com/xeno-by/scalameta/blob/master/scalameta/src/main/scala/scala/meta/Trees.scala#L80) for trees underlying [core traits](https://github.com/xeno-by/scalameta/blob/master/scalameta/src/main/scala/scala/meta/Trees.scala) and [quasiquotes](https://github.com/xeno-by/scalameta/blob/master/scalameta/src/main/scala/scala/meta/package.scala). It is necessary, because our implementation of  quasiquotes is currently a stub, so manual tree construction/deconstruction might be required.
  * `import scala.meta.semantic._` brings [semantic APIs](https://github.com/xeno-by/scalameta/blob/master/scalameta/src/main/scala/scala/meta/semantic/package.scala).
  * `import scala.meta.semantic.errors.throwExceptions` makes fallible semantic APIs to throw exceptions rather than wrap them in monads.
  * `implicit val host = scala.meta.internal.hosts.scalac.Scalahost(global)` creates a host, i.e. something that can process requests to semantic APIs. An implicit value of type `Host` is required to be in scope for most semantic APIs. Read more about hosts in [our docs](https://github.com/scalameta/scalameta/blob/master/docs/hosts.md).

In the `example` function you can do the following:
  * Analyze the syntax of your entire program by looking into `sources`
  * Parse strings into trees by importing `import scala.meta.syntactic.parsers._` and saying `<java.lang.String or java.io.File>.parse[<target tree type>]`, where target tree type might be any of the [core traits](https://github.com/xeno-by/scalameta/blob/master/scalameta/src/main/scala/scala/meta/Trees.scala) (`Source`, `Term`, `Type`, etc)

#### Future

  1. Provide implementations for semantic APIs. With that in place, it'll become possible to resolve references, compute types of terms, etc (something that's currently possible with Symbols and Types in the scala.reflect API).

  2. Remember all the details of how underlying programs were written (formatting, comments, etc). After this is implemented, it will become possible to implement precise code rewritings that don't lose any formatting. Also, we will get position information, which will allow to emit targetted warning and error messages.

  3. Replace manual tree construction/deconstruction via `import scala.meta.internal.ast._` with familiar quasiquote-based API from `import scala.meta._`. The `internal` API will either be hidden and discouraged or will go into oblivion completely.

  4. Avoid the need to write compiler plugins and instantiate hosts explicitly. First, with tree persistence, it'll be possible to get trees for everything on classpath, which compiled with the scalahost compiler plugin. Second, with macro support, it'll be possible to get trees of arguments of macro applications. We can consider and expose other ways of getting trees (e.g. loading them from an SBT project).

  5. Introduce means to obtain syntactic trivia associated with abstract syntax trees. After that, one will be able to discern `class C` and `class C{}`, `return` and `return ()` and other minor variations of syntax.

#### Acknowledgements

Thanks to [@aghosn](https://github.com/aghosn) for coming up with a framework of using scalahost to operate on scala.meta trees. Also thanks to [@MasseGuillaume](https://github.com/MasseGuillaume) for helping me organize the SBT build of scalahost, which became a foundation for the build of this project (that was very convenient!).