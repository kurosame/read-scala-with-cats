package chapter4

object Chapter4 {
  // 4 Monads
  /**
    * 非公式だが、モナドはコンストラクターとflatMapを持つものである
    */
  // 4.1 What is a Monad?
  /**
    * モナドは、計算を順序付けるためのメカニズムである
    */
//  def parseInt(str: String): Option[Int] =
//    scala.util.Try(str.toInt).toOption
//  def divide(a: Int, b: Int): Option[Int] =
//    if (b == 0) None else Some(a / b)

  /**
    * 上記2つの関数は、Noneを返すことで失敗する可能性がある
    * flatMap関数を使用すると、操作をシーケンスするときにこれを無視できる
    */
//  def stringDivideBy(aStr: String, bStr: String): Option[Int] =
//    parseInt(aStr).flatMap { aNum =>
//      parseInt(bStr).flatMap { bNum =>
//        divide(aNum, bNum)
//      }
//    }

  /**
    * 上記は、以下の動きをする
    * 1. 最初のparseIntを呼び出すと、NoneまたはSomeが返される
    * 2. Someを返す場合、flatMap関数は関数を呼び出し、aNumを渡す
    * 3. 2番目のparseIntもNoneまたはSomeを返す
    * 4. Someを返す場合、flatMap関数は関数を呼び出し、bNumを渡す
    * 5. divide関数を呼び出すと、NoneまたはSomeが返され、これが結果となる
    *
    * 各関数の計算の結果はすべてOptionなので、再度flatMapを呼び出し、シーケンスが続行できる
    * また、どこかでNoneになると、最終的な結果もNoneになる
    */
//  def main(args: Array[String]): Unit = {
//    println(stringDivideBy("6", "2")) // Some(3)
//    println(stringDivideBy("6", "0")) // None
//    println(stringDivideBy("6", "foo")) // None
//    println(stringDivideBy("bar", "2")) // None
//  }

  /**
    * すべてのモナドはファンクターである
    * stringDivideByをfor内包表記で書くと以下になる
    */
//  def stringDivideBy(aStr: String, bStr: String): Option[Int] =
//    for {
//      aNum <- parseInt(aStr)
//      bNum <- parseInt(bStr)
//      ans <- divide(aNum, bNum)
//    } yield ans

  /**
    * Futureは、非同期計算をシーケンスするモナドである
    */
//  import scala.concurrent.Future
//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  def doSomethingLongRunning: Future[Int] = ???
//  def doSomethingElseLongRunning: Future[Int] = ???
//
//  def doSomethingVeryLongRunning: Future[Int] = {
//    for {
//      result1 <- doSomethingLongRunning
//      result2 <- doSomethingElseLongRunning
//    } yield result1 + result2
//  }
  /**
    * 上記の計算の各ステップは、前のステップが終了した後でのみ開始できる
    * Futureは並列実行できるが、それは別の話であり、モナドについてはすべてシーケンスに関するものである
    */
  // 4.1.1 Definition of a Monad
  /**
    * モナドの動作は以下の2つの操作である
    * pure: A => F[A]
    * flatMap: (F[A], A => F[B]) => F[B]
    *
    * CatsのMonad型クラスの簡易版は以下である
    */
//  trait Monad[F[_]] {
//    def pure[A](value: A): F[A]
//    def flatMap[A, B](value: F[A])(func: A => F[B]): F[B]
//  }

  /**
    * モナド則
    *
    * pureを呼び出し、funcを呼び出すことは、ただのfuncを呼び出すことと同じ（左単位元）
    * pure(a).flatMap(func) == func(a)
    *
    * pureをflatMapに渡すことは、何もしないことと同じ（右単位元）
    * m.flatMap(pure) == m
    *
    * fのflatMapとgのflatMapは、fとgのflatMapと同じ（結合性）
    * m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))
    */
  // 4.1.2 Exercise: Getting Func-y
  /**
    * flatMapとpureを用いて、mapを定義せよ
    */
//  trait Monad[F[_]] {
//    def pure[A](a: A): F[A]
//
//    def flatMap[A, B](value: F[A])(func: A => F[B]): F[B]
//
//    def map[A, B](value: F[A])(func: A => B): F[B] = flatMap(value) { a =>
//      pure(func(a))
//    }
//  }

  // 4.2 Monads in Cats
  /**
    * Catsのモナドの型クラス、インスタンス、構文を見ていく
    */
  // 4.2.1 The Monad Type Class
  /**
    * cats.Monadは、flatMap関数を提供するFlatMap型とpure関数を提供するApplicative型を拡張している
    * ApplicativeはFunctorを拡張しているので、すべてのMonadにmap関数が提供される
    */
//  import cats.Monad
//  import cats.instances.option._
//  import cats.instances.list._
//
//  def main(args: Array[String]): Unit = {
//    val opt1 = Monad[Option].pure(3)
//    val opt2 = Monad[Option].flatMap(opt1)(a => Some(a + 2))
//    val opt3 = Monad[Option].map(opt2)(a => 100 * a)
//    println(opt1) // Some(3)
//    println(opt2) // Some(5)
//    println(opt3) // Some(500)
//
//    val list1 = Monad[List].pure(3)
//    val list2 = Monad[List].flatMap(List(1, 2, 3))(a => List(a, a * 10))
//    val list3 = Monad[List].map(list2)(a => a + 123)
//    println(list1) // List(3)
//    println(list2) // List(1, 10, 2, 20, 3, 30)
//    println(list3) // List(124, 133, 125, 143, 126, 153)
//  }

  // 4.2.2 Default Instances
  /**
    * Catsは、cats.instances経由ですべてのモナドのインスタンスを提供する
    */
//  import cats.Monad
//  import cats.instances.option._
//  import cats.instances.list._
//  import cats.instances.vector._
//
//  def main(args: Array[String]): Unit = {
//    println(Monad[Option].flatMap(Option(1))(a => Option(a * 2))) // Some(2)
//    println(Monad[List].flatMap(List(1, 2, 3))(a => List(a, a * 10))) // List(1, 10, 2, 20, 3, 30)
//    println(Monad[Vector].flatMap(Vector(1, 2, 3))(a => Vector(a, a * 10))) // Vector(1, 10, 2, 20, 3, 30)
//  }
  /**
    * Catsは、Futureモナドも提供する
    */
//  import cats.Monad
//  import cats.instances.future._
//  import scala.concurrent._
//  import scala.concurrent.duration._
//
//  // Futureは、前述のOptionなどと違い、以下のExecutionContextをスコープに入れないとimplicitの解決ができない
//  // ExecutionContextはスレッドを割り当てて、非同期処理を行えるようにする
//  import scala.concurrent.ExecutionContext.Implicits.global
//  val fm = Monad[Future]
//
//  def main(args: Array[String]): Unit = {
//    val future = fm.flatMap(fm.pure(1))(x => fm.pure(x + 2))
//    println(Await.result(future, 1.second)) // 3
//  }

  // 4.2.3 Monad Syntax
  /**
    * モナド構文は、以下の3つがある
    * cats.syntax.flatMapは、flatMap構文を提供する
    * cats.syntax.functorは、map構文を提供する
    * cats.syntax.applicativeは、pure構文を提供する
    * cats.implicitsを使えば、上記も含めてすべてのまとめてimportできる
    *
    * モナドのインスタンスを構築するためにpureを使用できる
    */
//  import cats.instances.option._
//  import cats.instances.list._
//  import cats.syntax.applicative._
//
//  def main(args: Array[String]): Unit = {
//    println(1.pure[Option]) // Some(1)
//    println(1.pure[List]) // List(1)
//  }

  /**
    * flatMapとmapは汎用関数を定義して確認する
    */
//  import cats.Monad
//  import cats.syntax.functor._
//  import cats.syntax.flatMap._
//
//  // 汎用関数
//  def sumSquare[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] =
//    a.flatMap(x => b.map(y => x * x + y * y))
//
//  import cats.instances.option._
//  import cats.instances.list._
//
//  def main(args: Array[String]): Unit = {
//    println(sumSquare(Option(3), Option(4))) // Some(25)
//    println(sumSquare(List(1, 2, 3), List(4, 5))) // List(17, 26, 20, 29, 25, 34)
//  }

  /**
    * flatMapとmapはfor内包表記で書き直せる
    */
//  import cats.Monad
//  import cats.syntax.functor._
//  import cats.syntax.flatMap._
//  def sumSquare[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] =
//    for {
//      x <- a
//      y <- b
//    } yield x * x + y * y
//
//  import cats.instances.option._
//  import cats.instances.list._
//
//  def main(args: Array[String]): Unit = {
//    println(sumSquare(Option(3), Option(4))) // Some(25)
//    println(sumSquare(List(1, 2, 3), List(4, 5))) // List(17, 26, 20, 29, 25, 34)
//  }

  // 4.3 The Identity Monad
//  import cats.Monad
//  import cats.syntax.functor._
//  import cats.syntax.flatMap._
//
//  def sumSquare[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] =
//    for {
//      x <- a
//      y <- b
//    } yield x * x + y * y
//
//  def main(args: Array[String]): Unit = {
//    // 以下のようにプレーンは値を渡すとエラーになる
//    // sumSquare(3, 4)
//
//    import cats.Id
//    // Idを使うとプレーンな値を使用して、モナド関数を呼び出せる
//    println(sumSquare(3: Id[Int], 4: Id[Int])) // 25
//  }

  /**
    * Idの定義は以下です
    *
    * type Id[A] = A
    */
//  import cats.Id
//
//  "Dave": Id[String]
//  123: Id[Int]
//  List(1, 2, 3): Id[List[Int]]
//
//  import cats.Monad
//
//  val a = Monad[Id].pure(3) // 3
//  val b = Monad[Id].flatMap(a)(_ + 1) // 4
//
//  import cats.syntax.functor._
//  import cats.syntax.flatMap._
//
//  for {
//    x <- a
//    y <- b
//  } yield x + y // 7

  // 4.3.1 Exercise: Monadic Secret Identities
  /**
    * Idのpure, map, flatMapを実装せよ
    */
//  // 答え見た
//  import cats.Id
//
//  def pure[A](value: A): Id[A] = value
//
//  def map[A, B](initial: Id[A])(func: A => B): Id[B] = func(initial)
//
//  def flatMap[A, B](initial: Id[A])(func: A => Id[B]): Id[B] = func(initial)
//
//  def main(args: Array[String]): Unit = {
//    println(pure(123)) // 123
//    println(map(123)(_ * 2)) // 246
//    println(flatMap(123)(_ * 2)) // 246
//  }

}
