package sandbox

object Chapter7 {
  // 7 Foldable and Traverse
  /**
   * この章では、コレクションの繰り返しをキャプチャする2つの型クラスについて説明します
   * ・Foldableは、おなじみのfoldLeftとfoldRight操作を抽象化します
   * ・Traverseは、Applicativeを使用して、foldingより少ない苦労で繰り返しする高レベルの抽象化です
   *
   * まず、Foldable式を見てから、foldingが複雑になり、Traverseが便利になるケースを考察します
   */
  // 7.1 Foldable
  /**
   * Foldable型クラスは、List,Vector,Streamなどのシーケンスで使われているfoldLeftとfoldRightをキャプチャします
   * Foldableを使うと、様々なシーケンス型で機能する汎用的なfoldを書くことができます
   * また、新しいシーケンスを考え出し、それをコードに差し込むこともできます
   * Foldableは、MonoidとEvalモナドの優れたユースケースを提供します
   */
  // 7.1.1 Folds and Folding
  /**
   * foldingの一般的なコンセプトの簡単な要約から始めましょう
   * アキュムレータ値とバイナリ関数を提供して、シーケンス内の各アイテムと組み合わせます
   */
//  def main(args: Array[String]): Unit = {
//    def show[A](list: List[A]): String =
//      list.foldLeft("nil")((accum, item) => s"$item then $accum")
//
//    // バイナリ関数は呼ばれない
//    println(show(Nil)) // nil
//
//    // accum:
//    // nil
//    // 1 then nil
//    // 2 then 1 then nil
//    // 3 then 2 then 1 then nil
//    println(show(List(1, 2, 3))) // 3 then 2 then 1 then nil
//  }

  /**
   * foldLeft関数はシーケンスで再帰的に機能します
   * バイナリ関数はアイテムごとに繰り返し呼び出され、各呼び出しの結果が次のアキュムレータになります
   * シーケンスの最後に到達すると、最終的なアキュムレータが最終結果になります
   */
  /**
   * 実行する操作によって、折り畳む順序が重要になる場合があります
   * このため、foldには2つの標準的なバリエーションがあります
   * ・foldLeft: 左から右に走査します
   * ・foldRight: 右から左に走査します
   * 図11に動きを示します
   */
  /**
   * 二項演算が結合法則である場合、foldLeftとfoldRightは同等です
   * (((0 + 1) + 2) + 3) == (1 + (2 + (3 + 0)))
   * たとえば、アキュムレータとして0を使用し、演算として加算を使用して、いずれかの方向に折り畳むことにより、List[Int]を合計できます
   */
//  def main(args: Array[String]): Unit = {
//    println(List(1, 2, 3).foldLeft(0)(_ + _)) // 6
//    println(List(1, 2, 3).foldRight(0)(_ + _)) // 6
//  }

  /**
   * 非結合法則の操作を提供する場合、評価の順序が異なります
   * たとえば、減算を使用して折り畳むと、動きが異なる結果が得られます
   */
//  def main(args: Array[String]): Unit = {
//    // (((0 - 1) - 2) - 3)
//    println(List(1, 2, 3).foldLeft(0)(_ - _)) // -6
//
//    // (1 - (2 - (3 - 0)))
//    println(List(1, 2, 3).foldRight(0)(_ - _)) // 2
//  }

  // 7.1.2 Exercise: Reflecting on Folds
  /**
   * 空のリストをアキュムレータ、::を二項演算子としてfoldLeftとfoldRightを使用してみてください
   * それぞれどのような結果が得られますか？
   */
//  def main(args: Array[String]): Unit = {
//    // 1 :: Nil -> 2 :: 1 :: Nil -> 3 :: 2 :: 1 :: Nil
//    println(List(1, 2, 3).foldLeft(List(): List[Int])((x, y) => y :: x)) // List(3, 2, 1)
//
//    // 3 :: Nil -> 2 :: 3 :: Nil -> 1 :: 2 :: 3 :: Nil
//    println(List(1, 2, 3).foldRight(List(): List[Int])(_ :: _)) // List(1, 2, 3)
//  }

  // 7.1.3 Exercise: Scaf-fold-ing Other Methods
  /**
   * foldLeftとfoldRightはとても一般的な関数です
   * それらを使用して、他の高レベルのシーケンス操作を実装できます
   * Listのmap,flatMap,filter,sum関数をfoldRightで実装して、これを証明してください
   */
  // 答え見た
//  def main(args: Array[String]): Unit = {
//    def map[A, B](list: List[A])(func: A => B): List[B] =
//      list.foldRight(List.empty[B]) { (item, accum) =>
//        func(item) :: accum
//      }
//
//    def flatMap[A, B](list: List[A])(func: A => List[B]): List[B] =
//      list.foldRight(List.empty[B]) { (item, accum) =>
//        func(item) ::: accum
//      }
//
//    def filter[A](list: List[A])(func: A => Boolean): List[A] =
//      list.foldRight(List.empty[A]) { (item, accum) =>
//        if (func(item)) item :: accum else accum
//      }
//
//    def sumWithNumeric[A](list: List[A])(implicit numeric: Numeric[A]): A =
//      list.foldRight(numeric.zero)(numeric.plus)
//
//    import cats.Monoid
//    def sumWithMonoid[A](list: List[A])(implicit monoid: Monoid[A]): A =
//      list.foldRight(monoid.empty)(monoid.combine)
//
//    // accum
//    // 6 :: Nil
//    // 4 :: 6 :: Nil
//    // 2 :: 4 :: 6 :: Nil
//    println(map(List(1, 2, 3))(_ * 2)) // List(2, 4, 6)
//
//    // accum
//    // List(3, 30, 300) ::: Nil
//    // List(2, 20, 200) ::: List(3, 30, 300) ::: Nil
//    // List(1, 10, 100) ::: List(2, 20, 200) ::: List(3, 30, 300) ::: Nil
//    println(flatMap(List(1, 2, 3))(a => List(a, a * 10, a * 100))) // List(1, 10, 100, 2, 20, 200, 3, 30, 300)
//
//    // accum
//    // 3 :: Nil
//    // 3 :: Nil
//    // 1 :: 3 :: Nil
//    println(filter(List(1, 2, 3))(_ % 2 == 1)) // List(1, 3)
//
//    // accum
//    // 3 + 0
//    // 2 + 3 + 0
//    // 1 + 2 + 3 + 0
//    println(sumWithNumeric(List(1, 2, 3))) // 6
//
//    // MonoidのIntのcombineは加算
//    import cats.instances.int._
//    // accum
//    // 3 + 0
//    // 2 + 3 + 0
//    // 1 + 2 + 3 + 0
//    println(sumWithMonoid(List(1, 2, 3))) // 6
//  }

  // 7.1.4 Foldable in Cats
  /**
   * CatsのFoldableは、foldLeftとfoldRightを型クラスに抽象化します
   * Foldableインスタンスは、これら2つの関数が定義されており、多くの派生関数が継承されています
   * Catsは、いくつかのScalaデータ型（List,Vector,LazyList,Option）に対して、Foldableインスタンスを提供します
   *
   * Foldable.applyを利用して、通常通りにインスタンスを呼び出し、foldLeftの実装を直接呼び出すことができます
   * Listを使用した例を次に示します
   */
//  import cats.Foldable
//  import cats.instances.list._
//
//  def main(args: Array[String]): Unit = {
//    val ints = List(1, 2, 3)
//    println(Foldable[List].foldLeft(ints, 0)(_ + _)) // 6
//  }

  /**
   * VectorやLazyListなどの他のシーケンスでも同じように機能します
   * ここでは、0または1の要素のシーケンスのように扱われるOptionを使用した例を紹介します
   */
//  import cats.Foldable
//  import cats.instances.option._
//
//  def main(args: Array[String]): Unit = {
//    val maybeInt = Option(123)
//    // 10 * 123
//    println(Foldable[Option].foldLeft(maybeInt, 10)(_ * _)) // 1230
//  }

  // 7.1.4.1 Folding Right
  /**
   * Foldableは、EvalモナドについてfoldRightをfoldLeftとは異なる方法で定義します
   */
//  def foldRight[A, B](fa: F[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B]

  /**
   * Evalを使用すると、コレクションのデフォルトのfoldRightの定義でない場合でも、foldingは常にスタックセーフになります
   * （foldRightは基本的にスタックセーフではないが、EvalのfoldRightはスタックセーフである）
   * たとえば、LazyListのfoldRightのデフォルトの実装はスタックセーフではありません
   * LazyListが長いほど、foldのスタックは大きくなり、StackOverflowErrorをトリガーします
   */
//  import cats.Eval
//  import cats.Foldable
//
//  def bigData = (1 to 100000).to(LazyList)
//
//  // StackOverflowError
//  // (... + (99998 + (99999 + (100000 + 0L))))
//  bigData.foldRight(0L)(_ + _)

  /**
   * Foldableを使うと、スタックセーフ操作が強制されるため、オーバーフロー例外が修正されます
   */
//  import cats.Eval
//  import cats.Foldable
//  import cats.instances.lazyList._
//
//  def bigData = (1 to 100000).to(LazyList)
//
//  val eval: Eval[Long] =
//    Foldable[LazyList].foldRight(bigData, Eval.now(0L)) { (num, eval) =>
//      // Evalのmapはトランポリン化されるので、スタックセーフ
//      // 引数fを末尾再帰で手続き的に呼び出せる
//      eval.map(_ + num)
//    }
//
//  def main(args: Array[String]): Unit = {
//    println(eval.value) // 5000050000
//  }

  /**
   * 標準ライブラリのスタックの安全性
   *
   * 標準ライブラリを使用する場合、スタックの安全性は通常は問題にはなりません
   * ListやVectorなど最も一般的に使用されるコレクション型は、スタックセーフなfoldRightの実装を提供します
   * (1 to 100000).toList.foldRight(0L)(_ + _) // 5000050000L
   * (1 to 100000).toVector.foldRight(0L)(_ + _) // 5000050000L
   *
   * ここでStreamを取り上げたのは、このルールの例外だからです（？）
   * しかし、どのようなデータ型を使用していても、Evalが我々をサポートしていることを知っておくと便利です
   */
  // 7.1.4.2 Folding with Monoids
  /**
   * Foldableは、foldLeftで定義された多くの便利な関数を提供します
   * これらの多くは、標準ライブラリの使い慣れた関数（find,exists,forall,toList,isEmpty,nonEmptyなど）の複製です
   */
//  import cats.Foldable
//  import cats.implicits._
//
//  def main(args: Array[String]): Unit = {
//    val res1 = Foldable[Option].nonEmpty(Option(42))
//    val res2 = Foldable[List].find(List(1, 2, 3))(_ % 2 == 0)
//
//    println(res1) // true
//    println(res2) // Some(2)
//  }

  /**
   * これらの使い慣れた方法に加えて、Catsはモノイドを利用する2つの方法を提供します
   *
   * ・combineAll(foldのエイリアス)は、Monoidを使用してシーケンス内のすべての要素を結合します
   * ・foldMapはユーザー提供の関数をシーケンスにmapして、その結果をMonoidを使用して結合します
   *
   * たとえば、combineAllを使用してList[Int]を合計できます
   */
//  import cats.Foldable
//  import cats.implicits.catsStdInstancesForList
//  import cats.instances.int._
//
//  def main(args: Array[String]): Unit = {
//    println(Foldable[List].combineAll(List(1, 2, 3))) // 6
//  }

  /**
   * または、foldMapを使用して各Intを文字列に変換し、連結することもできます
   */
//  import cats.Foldable
//  import cats.implicits.catsStdInstancesForList
//  import cats.instances.string._
//
//  def main(args: Array[String]): Unit = {
//    // 関数（_.toString）の結果を結合する
//    println(Foldable[List].foldMap(List(1, 2, 3))(_.toString)) // 123
//  }

  /**
   * 最後に、Foldableを組み合わせて、ネストされたシーケンスの深い探索をサポートすることができます
   */
//  import cats.Foldable
//  import cats.implicits.{catsKernelStdGroupForInt, catsStdInstancesForList}
//  import cats.instances.vector._
//
//  def main(args: Array[String]): Unit = {
//    val ints = List(Vector(1, 2, 3), Vector(4, 5, 6))
//
//    // ListとVectorのFoldableを合成すれば、ネストされていても数値を評価できる
//    println(Foldable[List].compose(Foldable[Vector]).combineAll(ints)) // 21
//
//    println(Foldable[List].combineAll(ints)) // Vector(1, 2, 3, 4, 5, 6)
//    // println(Foldable[Vector].combineAll(ints)) // type mismatch;
//  }

  // 7.1.4.3 Syntax for Foldable
  /**
   * Foldableのすべての関数はcats.syntax.foldable経由で利用できます
   * いずれの場合も、Foldable関数の第一引数が、関数呼び出しのレシーバーになります
   */
//  import cats.implicits.{catsKernelStdGroupForInt, catsKernelStdMonoidForString, catsStdInstancesForList}
//  import cats.syntax.foldable._
//
//  def main(args: Array[String]): Unit = {
//    println(List(1, 2, 3).combineAll) // 6
//    println(List(1, 2, 3).foldMap(_.toString)) // 123
//  }

  /**
   * 暗黙的なものに対する明示的なもの
   *
   * Scalaは、関数がレシーバーで明示的に使用できない場合にのみFoldableインスタンスを使用することに注意してください
   * たとえば、次のコードは、Listで定義されているfoldLeftを使用します
   *
   * List(1, 2, 3).foldLeft(0)(_ + _)
   *
   * 一方、次の汎用コードはFoldableを使用します
   *
   * def sum[F[_]: Foldable](values: F[Int]): Int = values.foldLeft(0)(_ + _)
   *
   * 通常、この違いについて心配する必要はありません
   * そうゆうもんだよ！
   * 必要な関数を呼び出し、コンパイラーは必要に応じてFoldableを使用して、コードが期待通りに動作するようにします
   * foldRightのスタックセーフな実装が必要な場合は、アキュムレータとしてEvalを使用するだけで、コンパイラーがCatsから関数を選択するようになります
   */

  // 7.2 Traverse
  /**
   * foldLeftとfoldRightは、柔軟なイテレーションですが、アキュムレータとコンビネータ関数を定義するために多くの作業を行う必要があります
   * Traverse型クラスは、Applicativeを活用して、より便利で正当なイテレーションパターンを提供する高レベルのツールです
   */
  // 7.2.1 Traversing with Futures
  /**
   * Scala標準ライブラリのFuture.traverse関数とFuture.sequence関数を使ってTraverseをデモンストレーションできます
   * これらの関数は、トラバースパターンのFuture固有の実装を提供します
   * 例として、サーバーのホスト名のリストとホストの稼働時間をポーリングする関数があるとします
   */
//  import scala.concurrent._
//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  val hostnames = List(
//    "alpha.example.com",
//    "beta.example.com",
//    "gamma.demo.com"
//  )
//
//  def getUptime(hostname: String): Future[Int] = Future(hostname.length * 60)

  /**
   * ここで、すべてのホストをポーリングして、そのすべての稼働時間を収集するとします
   * 結果（List[Future[Int]]）には、複数のFutureが含まれるため、ホスト名を単純にmapすることはできません
   * ブロックできるものを取得するには、結果を単一のFutureに減らす必要があります
   * foldを使用して、手動でこれを行うことから始めましょう
   */
//  import scala.concurrent._
//  import scala.concurrent.duration._
//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  val hostnames =
//    List("alpha.example.com", "beta.example.com", "gamma.demo.com")
//
//  def getUptime(hostname: String): Future[Int] = Future(hostname.length * 60)
//
//  val allUptimes: Future[List[Int]] =
//    hostnames.foldLeft(Future(List.empty[Int])) { (accum, host) =>
//      val uptime = getUptime(host)
//      for {
//        // flatMapでFutureを外す
//        accum <- accum
//        uptime <- uptime
//      } yield accum :+ uptime // accum配列の末尾にuptimeを追加
//    }
//
//  def main(args: Array[String]): Unit = {
//    println(Await.result(allUptimes, 1.second)) // List(1020, 960, 840)
//  }

  /**
   * 直感的にホスト名繰り返し処理し、アイテムごとにfuncを呼び出し、結果をリストに結合します
   * これは単純に聞こえますが、イテレーションごとにFutureを作成して結合する必要があるため、コードはかなり扱いにくいです
   * このパターンに合わせてカスタマイズされたFuture.traverseを使うと、これを大幅に改善できます
   */
//  import scala.concurrent._
//  import scala.concurrent.duration._
//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  val hostnames =
//    List("alpha.example.com", "beta.example.com", "gamma.demo.com")
//
//  def getUptime(hostname: String): Future[Int] = Future(hostname.length * 60)
//
//  // allUptimesをfoldLeftからFuture.traverseに変更
//  val allUptimes: Future[List[Int]] = Future.traverse(hostnames)(getUptime)
//
//  def main(args: Array[String]): Unit = {
//    println(Await.result(allUptimes, 1.second)) // List(1020, 960, 840)
//  }

  /**
   * これははるかに明確で簡潔です
   * どのように機能するか見てみましょう
   * CanBuildFromやExecutionContextを無視すると、標準ライブラリでのFuture.traverseの実装は次のようになります
   */
//  import scala.concurrent._
//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  def traverse[A, B](values: List[A])(func: A => Future[B]): Future[List[B]] =
//    values.foldLeft(Future(List.empty[B])) { (accum, host) =>
//      val item = func(host)
//      for {
//        accum <- accum
//        item <- item
//      } yield accum :+ item
//    }

  /**
   * これは基本的に上記のfoldLeftのサンプルコードと同じです
   * Future.traverseは、アキュムレータと結合関数をfoldingして定義することの苦痛を抽象化します
   * それは我々が望むことをするためのクリーンな高レベルのインターフェイスを与えてくれます
   * 1. List[A]から始めます
   * 2. 関数A => Future[B]を提供します
   * 3. 最終的にFuture[List[B]]になります
   *
   * 標準ライブラリは、別の関数Future.sequenceも提供します
   * これはList[Future[B]]から開始し、恒等関数を提供する必要がないことを前提としています
   */
//  object Future {
//    // funcを提供しない
//    // List[Future[B]]をFuture[List[B]]に変換する
//    def sequence[B](futures: List[Future[B]]): Future[List[B]] =
//      traverse(futures)(identity)
//  }

  /**
   * この場合、直感的な理解はさらに簡単です
   * 1. List[Future[A]]から始めます
   * 2. 最終的にFuture[List[A]]になります
   */

  /**
   * Future.traverseとFuture.sequenceは、非常に特殊な問題を解決します
   * これらを使用すると、Futureシーケンスをイテレートして、結果を蓄積できます
   * 上記の簡略化された例はリストでのみ機能しますが、実際のFuture.traverseとFuture.sequenceは標準のScalaコレクションで機能します
   *
   * CatsのTraverse型クラスは、これらのパターンを一般化して、Future,Option,Validatedなどの任意の型のApplicativeで機能します
   * 次のセクションでは、2つのステップでTraverseにアプローチします
   * 最初にApplicativeについて一般化し、次にシーケンス型について一般化します
   * 最終的には、シーケンスやその他のデータ型を含む多くの操作を簡単にする非常に価値のあるツールになります
   */

  // 7.2.2 Traversing with Applicatives
  /**
   * 目を細めると、Applicativeの観点からtraverseを書き換えることができることが分かります
   * 上記の例のアキュムレータ: Future(List.empty[Int])
   * これは、以下のApplicative.pureと同等です
   */
//  import scala.concurrent._
//  import scala.concurrent.ExecutionContext.Implicits.global
//  import cats.Applicative
//  import cats.instances.future._
//  import cats.syntax.applicative._
//
//  List.empty[Int].pure[Future]

  /**
   * 我々のコンビネーターは、以前は以下でした
   */
//  def oldCombine(accum: Future[List[Int]], host: String): Future[List[Int]] = {
//    val uptime = getUptime(host)
//    for {
//      accum <- accum
//      uptime <- uptime
//    } yield accum :+ uptime
//  }

  /**
   * 上記のoldCombineは、Semigroupal.combineと同等になりました
   * （以下のmapNがSemigroupal）
   */
//  import scala.concurrent._
//  import scala.concurrent.ExecutionContext.Implicits.global
//  import cats.syntax.apply._
//
//  def getUptime(hostname: String): Future[Int] = Future(hostname.length * 60)
//
//  // Applicativeを利用して、アキュムレータとホスト名を結合する
//  def newCombine(accum: Future[List[Int]], host: String): Future[List[Int]] =
//    (accum, getUptime(host)).mapN(_ :+ _)

  /**
   * これらのスニペットをtraverseの定義に置き換えることで、任意のApplicativeで機能するように一般化できます
   * （上記のFutureをFに一般化できる）
   */
//  import cats.Applicative
//  import cats.syntax.applicative._
//  import cats.syntax.apply._
//  import scala.concurrent._
//  import scala.concurrent.ExecutionContext.Implicits.global
//  import cats.instances.future._
//
//  def listTraverse[F[_]: Applicative, A, B](list: List[A])(func: A => F[B]): F[List[B]] =
//    list.foldLeft(List.empty[B].pure[F]) { (accum, item) =>
//      (accum, func(item)).mapN(_ :+ _)
//    }
//
//  def listSequence[F[_]: Applicative, B](list: List[F[B]]): F[List[B]] =
//    listTraverse(list)(identity)
//
//  val hostnames = List("alpha.example.com", "beta.example.com", "gamma.demo.com")
//  def getUptime(hostname: String): Future[Int] = Future(hostname.length * 60)
//
//  // listTraverseを使用して、稼働時間の例を再実装できます
//  // hostnameを左から順番にgetUptimeに渡して、結果をaccumに蓄積したリストを取得できる
//  // getUptimeはそれぞれFuture[Int]を返すが、traverseなのでFuture[List[Int]]にできる
//  val totalUptime = listTraverse(hostnames)(getUptime)
//
//  def main(args: Array[String]): Unit = {
//    import scala.concurrent.duration._
//    // FはFutureなので、以下で外す
//    println(Await.result(totalUptime, 1.second)) // List(1020, 960, 840)
//  }

  /**
   * 次のExerciseに示すように、他のApplicativeデータ型で使用することもできます
   */

  // 7.2.2.1 Exercise: Traversing with Vectors
  /**
   * 以下の結果は何ですか？
   * listSequence(List(Vector(1, 2), Vector(3, 4)))
   *
   * 3つのパラメーターのリストはどうですか？
   * listSequence(List(Vector(1, 2), Vector(3, 4), Vector(5, 6)))
   */
//  import cats.Applicative
//  import cats.syntax.applicative._
//  import cats.syntax.apply._
//  import cats.instances.vector._
//
//  def listTraverse[F[_]: Applicative, A, B](list: List[A])(func: A => F[B]): F[List[B]] =
//    list.foldLeft(List.empty[B].pure[F]) { (accum, item) =>
//      {
//        println(accum)
//        println(func(item))
//        (accum, func(item)).mapN(_ :+ _)
//      }
//    }
//
//  def listSequence[F[_]: Applicative, B](list: List[F[B]]): F[List[B]] =
//    listTraverse(list)(identity)
//
//  def main(args: Array[String]): Unit = {
//    println(listSequence(List(Vector(1, 2), Vector(3, 4))))
//    // 間違えた
//    // Vector(List(1,2,3,4)))
//
//    // 正解
//    // Vectorはモナドなので、Semigroupalのcombine関数はflatMapに基づいている
//    // よって、すべての組み合わせのリストが作成される
//    // accum: Vector(List())
//    // func(item): Vector(1, 2)
//    // accum: Vector(List(1), List(2))
//    // func(item): Vector(3, 4)
//    // accum: Vector(List(1, 3), List(1, 4), List(2, 3), List(2, 4))
//
//    println(listSequence(List(Vector(1, 2), Vector(3, 4), Vector(5, 6))))
//    // こっちは正解
//    // Vector(List(1,3,5),List(1,3,6),List(1,4,5),List(1,4,6)...)
//
//    // Vector(List(1, 3, 5), List(1, 3, 6), List(1, 4, 5), List(1, 4, 6), List(2, 3, 5), List(2, 3, 6), List(2, 4, 5), List(2, 4, 6))
//  }

  // 7.2.2.2 Exercise: Traversing with Options
  /**
   * Optionを使用する例を次に示します
   *
   * この関数の戻り値の型と次の入力に対して何を生成しますか？
   * process(List(2, 4, 6))
   * process(List(1, 2, 3))
   */
//  import cats.Applicative
//  import cats.syntax.applicative._
//  import cats.syntax.apply._
//  import cats.instances.option._
//
//  def listTraverse[F[_]: Applicative, A, B](list: List[A])(func: A => F[B]): F[List[B]] =
//    list.foldLeft(List.empty[B].pure[F]) { (accum, item) =>
//      (accum, func(item)).mapN(_ :+ _)
//    }
//
//  // 戻り値の型はOption[List[Int]]
//  // FにあたるOption型を返す関数を渡している
//  def process(inputs: List[Int]) =
//    listTraverse(inputs)(n => if (n % 2 == 0) Some(n) else None)
//
//  def main(args: Array[String]): Unit = {
//    // 正解した
//    println(process(List(2, 4, 6))) // Some(List(2, 4, 6))
//    println(process(List(1, 2, 3))) // None
//
//    // mapNのタプルのいずれかがNoneだと結果はNoneになる
//  }

  // 7.2.2.3 Exercise: Traversing with Validated
  /**
   * 最後にValidatedを使用する例を次に示します
   *
   * この関数は次の入力に対して何を生成しますか？
   * process(List(2, 4, 6))
   * process(List(1, 2, 3))
   */
//  import cats.Applicative
//  import cats.syntax.applicative._
//  import cats.syntax.apply._
//  import cats.data.Validated
//  import cats.instances.list._
//
//  def listTraverse[F[_]: Applicative, A, B](list: List[A])(func: A => F[B]): F[List[B]] =
//    list.foldLeft(List.empty[B].pure[F]) { (accum, item) =>
//      (accum, func(item)).mapN(_ :+ _)
//    }
//
//  type ErrorsOr[A] = Validated[List[String], A]
//
//  def process(inputs: List[Int]): ErrorsOr[List[Int]] =
//    listTraverse(inputs) { n =>
//      if (n % 2 == 0) {
//        Validated.valid(n)
//      } else {
//        Validated.invalid(List(s"$n is not even"))
//      }
//    }
//
//  def main(args: Array[String]): Unit = {
//    println(process(List(2, 4, 6))) // Valid(List(2, 4, 6))
//    println(process(List(1, 2, 3))) // Invalid(List(1 is not even, 3 is not even))
//
//    // (accum, func(item))
//    // (Valid(List(2)), Valid(List(4))) -> Valid(List(2, 4))
//    // (Invalid(List(1 is not even)), Invalid(List(3 is not even))) -> Invalid(List(1 is not even, 3 is not even))
//    // (Valid(List()), Invalid(List(1 is not even))) -> Invalid(List(1 is not even))
//    // (Invalid(List(1 is not even)), Valid(List(2))) -> Invalid(List(1 is not even))
//
//    // すべて成功の場合は、入力のListを返す
//    // 失敗した場合は、失敗した結果を蓄積して返す
//  }

  // 7.2.3 Traverse in Cats
  /**
   * listTraverse関数とlistSequence関数は、任意の型のApplicativeで機能しますが、1つの型のシーケンス（List）でのみ機能します
   * 型クラスを使用して、様々なシーケンス型を一般化できますが、これができるのがCatsのTraverseです
   * 簡略化された定義は次のとおりです
   */
//  trait Traverse[F[_]] {
//    def traverse[G[_]: Applicative, A, B](inputs: F[A])(func: A => G[B]): G[F[B]]
//
//    def sequence[G[_]: Applicative, B](inputs: F[G[B]]): G[F[B]] =
//      traverse(inputs)(identity)
//  }

  /**
   * Catsは、List,Vector,Stream,Option,Either及びその他の様々な型のTraverseインスタンスを提供します
   * Traverse.applyを使用して、いつもどおりインスタンスを呼び出し、前のセクションで説明したようにtraverse関数とsequence関数を使用できます
   */
//  import scala.concurrent._
//  import scala.concurrent.ExecutionContext.Implicits.global
//  import scala.concurrent.duration._
//  import cats.Traverse
//  import cats.instances.future._
//  import cats.instances.list._
//
//  val hostnames = List("alpha.example.com", "beta.example.com", "gamma.demo.com")
//  def getUptime(hostname: String): Future[Int] = Future(hostname.length * 60)
//
//  def main(args: Array[String]): Unit = {
//    val totalUptime: Future[List[Int]] = Traverse[List].traverse(hostnames)(getUptime)
//    println(Await.result(totalUptime, 1.second)) // List(1020, 960, 840)
//
//    val numbers = List(Future(1), Future(2), Future(3))
//    val numbers2: Future[List[Int]] = Traverse[List].sequence(numbers)
//    println(Await.result(numbers2, 1.second)) // List(1, 2, 3)
//
//    // cats.syntax.traverse経由でインポートされた関数の構文バージョンもあります
//    import cats.syntax.traverse._
//    println(Await.result(hostnames.traverse(getUptime), 1.second)) // List(1020, 960, 840)
//    val res = Await.result(numbers.sequence, 1.second)
//    println(res) // List(1, 2, 3)
//  }

  /**
   * ご覧のとおり、この章の前半で見たfoldLeftよりもはるかにコンパクトで読みやすいものです
   */

  // 7.3 Summary
  /**
   * この章では、シーケンスをイテレート処理するための2つの型クラスであるFoldableとTraverseを紹介しました
   *
   * Foldableは、標準ライブラリのコレクションから知っているfoldLeft関数とfoldRight関数を抽象化します
   * これらの関数のスタックセーフな実装をいくつかのデータ型に追加し、状況に応じて便利な追加機能のホストを定義しています
   * とはいえ、Foldableのたくさんのまだ知らないことを紹介できていません
   *
   * 本当の力を発揮するのはTraverseで、Futureでおなじみのtraverse関数とsequence関数を抽象化し、一般化しています
   * これらの関数を使用して、Traverseインスタンスを持つ任意のFとApplicativeインスタンスを持つ任意のGのF[G[A]]をG[F[A]]に変換できます
   * コード行の削減という点では、Traverseはこの本で最も強力なパターンの1つです
   * 何行にもわたるfoldを1つのfoo.traverseにまで減らすことができます
   */

  /**
   * ...ということで、この本の理論はすべて終了しました
   * PartⅡでは、学んだことを実践するために、様々なケーススタディを行います
   */
}
