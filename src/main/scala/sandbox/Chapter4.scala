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

  // 4.4 Either
  // 4.4.1 Left and Right Bias
  /**
    * Scala 2.11以前ではEitherは、mapとflatMapを持たないので、`.right`が必要だった
    */
//  val either1: Either[String, Int] = Right(10)
//  val either2: Either[String, Int] = Right(32)
//
//  for {
//    a <- either1.right
//    b <- either2.right
//  } yield a + b // 42

  /**
    * Scala 2.12からは、Eitherが再設計され、mapとflatMapを持つようになったので、`.right`が不要になった
    */
//  val either1: Either[String, Int] = Right(10)
//  val either2: Either[String, Int] = Right(32)
//
//  for {
//    a <- either1
//    b <- either2
//  } yield a + b // Right(42)

  /**
    * Scala 2.11以前では、`cats.syntax.either._`をimportすることでmapとflatMapを使えるようになる
    * Scala 2.12以降はこのパッケージは省略可能
    */
  // 4.4.2 Creating Instances
//  import cats.syntax.either._
//
//  def main(args: Array[String]): Unit = {
//    val a = 3.asRight[String]
//    val b = 4.asRight[String]
//    val c = for {
//      x <- a
//      y <- b
//    } yield x * x + y * y
//
//    println(a) // Right(3)
//    println(b) // Right(4)
//    println(c) // Right(25)
//  }

  /**
    * Left.applyはLeftをRight.applyはRightを返すが、上記のasRightはEither型を返すので便利なときがある
    */
  // Right.applyを使った例
//  def countPositive(nums: List[Int]) =
//    nums.foldLeft(Right(0)) { (accumulator, num) =>
//      if (num > 0) {
//        // コンパイラーはaccumulatorをRight型と推測している
//        // mapはEither型を返すのでtype mismatchとなる
//        accumulator.map(_ + 1)
//      } else {
//        // こちらも同様にaccumulatorをRight型と推測しているので、type mismatchとなる
//        // さらにRight[Int](0)のように型パラメーターを指定していないので、コンパイラーは左側をNothingと推測する
//        // 以下はStringを指定しているので、type mismatchとなる
//        Left("Negative. Stopping!")
//      }
//    }

  // asRightを使った例
//  import cats.syntax.either._
//
//  def countPositive(nums: List[Int]) =
//    nums.foldLeft(0.asRight[String]) { (accumulator, num) =>
//      if (num > 0) {
//        // コンパイラーはEither[String, Int]と推測するので、エラーにならない
//        accumulator.map(_ + 1)
//      } else {
//        // こちらも同様にエラーにならない
//        Left("Negative. Stopping!")
//      }
//    }
//
//  def main(args: Array[String]): Unit = {
//    println(countPositive(List(1, 2, 3))) // Right(3)
//    println(countPositive(List(1, -2, 3))) // Left(Negative. Stopping!)
//  }

  /**
    * catchOnly関数とcatchNonFatal関数は例外をキャッチするのに最適である
    */
//  import cats.syntax.either._
//
//  def main(args: Array[String]): Unit = {
//    println(Either.catchOnly[NumberFormatException]("foo".toInt))
//    // Left(java.lang.NumberFormatException: For input string: "foo")
//    println(Either.catchNonFatal(sys.error("Badness")))
//    // Left(java.lang.RuntimeException: Badness)
//
//    // 他のデータ型からEitherを作ることも可能
//    println(Either.fromTry(scala.util.Try("foo".toInt)))
//    // Left(java.lang.NumberFormatException: For input string: "foo")
//    println(Either.fromOption[String, Int](None, "Badness"))
//    // Left(Badness)
//  }

  // 4.4.3 Transforming Eithers
//  import cats.syntax.either._
//
//  def main(args: Array[String]): Unit = {
//    // Leftなので、デフォルトの値の0を返す
//    println("Error".asLeft[Int].getOrElse(0)) // 0
//
//    // Leftなので、デフォルトのEitherのRight(2)を返す
//    println("Error".asLeft[Int].orElse(2.asRight[String])) // Right(2)
//
//    // ensure関数はRightが条件を満たすか確認できる
//    println((-1).asRight[String].ensure("Must be non-negative!")(_ > 0)) // Left(Must be non-negative!)
//
//    // recoverとrecoverWithはエラー処理を行える
//    // Left（エラー）なので、caseで指定した処理を行う
//    println("error".asLeft[Int].recover { case _: String     => -1 }) // Right(-1)
//    println("error".asLeft[Int].recoverWith { case _: String => Right(-1) }) // Right(-1)
//
//    // map系
//    println("foo".asLeft[Int].leftMap(_.reverse)) // Left(oof)
//    println(6.asRight[String].bimap(_.reverse, _ * 7)) // Right(42)
//    println("bar".asLeft[Int].bimap(_.reverse, _ * 7)) // Left(rab)
//
//    // swapはRightとLeftを入れ替える
//    println(123.asRight[String]) // Right(123)
//    println(123.asRight[String].swap) // Left(123)
//  }

  // 4.4.4 Error Handling
  /**
    * EitherのflatMapで計算をシーケンスすると、1つでも計算に失敗したら、以降の計算は行われない
    */
//  import cats.syntax.either._
//
//  def main(args: Array[String]): Unit = {
//    val res = for {
//      a <- 1.asRight[String]
//      b <- 0.asRight[String]
//      c <- if (b == 0) "DIV0".asLeft[Int]
//      else (a / b).asRight[String]
//    } yield c * 100
//
//    println(res) // Left(DIV0)
//    // c * 100は行われない
//  }

  /**
    * エラー処理でEitherを使用する場合、エラーの型を決める必要がある
    */
//  type Result[A] = Either[Throwable, A]

  /**
    * ただし、Throwableは広すぎて具体的にどのようなエラーが起きたのか分からない
    *
    * 別のアプローチとして、プログラムで発生する可能性のあるエラーを定義する
    */
//  sealed trait LoginError extends Product with Serializable
//
//  final case class UserNotFound(username: String) extends LoginError
//
//  final case class PasswordIncorrect(username: String) extends LoginError
//
//  case object UnexpectedError extends LoginError
//
//  case class User(username: String, password: String)
//
//  type LoginResult = Either[LoginError, User]
//
//  // パターンマッチでエラーごとに処理が定義できる
//  def handleError(error: LoginError): Unit =
//    error match {
//      case UserNotFound(u)      => println(s"User not found: $u")
//      case PasswordIncorrect(u) => println(s"Password incorrect: $u")
//      case UnexpectedError      => println(s"Unexpected error")
//    }
//
//  def main(args: Array[String]): Unit = {
//    import cats.syntax.either._
//    val result1: LoginResult = User("dave", "passw0rd").asRight
//    val result2: LoginResult = UserNotFound("dave").asLeft
//
//    println(result1.fold(handleError, println)) // User(dave,passw0rd)
//    println(result2.fold(handleError, println)) // User not found: dave
//  }

  // 4.4.5 Exercise: What is Best?
  /**
    * 前述のエラー処理戦略は、すべての目的に適しているか？
    * エラー処理に他にどのような機能が必要か？
    */
  // 答え見た
  // エラーのリカバリ処理が必要
  // エラーの通知
  // 最初に発生したエラーだけでなく、すべてのエラーを収集する必要がある
  // たとえば、入力フォームを例にすると、1つずつエラーを報告するより、1回ですべてのエラーを報告した方がよいだろう

  // 4.5 Aside: Error Handling and MonadError
  /**
    * CatsはEitherのようなMonadErrorというエラー処理に使えるデータ型を提供する
    */
  // 4.5.1 The MonadError Type Class
  /**
    * MonadErrorの定義の簡易版を以下に示す
    *
    *  Fは、モナドの型
    *  Eは、Fに含まれるエラーの型
    */
//  import cats.Monad
//
//  trait MonadError[F[_], E] extends Monad[F] {
//    def raiseError[A](e: E): F[A]
//    def handleErrorWith[A](fa: F[A])(f: E => F[A]): F[A]
//    def handleError[A](fa: F[A])(f: E => A): F[A]
//    def ensure[A](fa: F[A])(e: E)(f: A => Boolean): F[A]
//  }

  /**
    * Eitherの型クラスをインスタンス化する例を以下に示す
    */
//  import cats.MonadError
//  import cats.instances.either._
//
//  type ErrorOr[A] = Either[String, A]
//  val monadError = MonadError[ErrorOr, String]

  // 4.5.2 Raising and Handling Errors
  /**
    * MonadErrorの重要な関数はraiseErrorとhandleErrorWithである
    */
//  import cats.MonadError
//  import cats.instances.either._
//
//  type ErrorOr[A] = Either[String, A]
//  val monadError = MonadError[ErrorOr, String]
//
//  def main(args: Array[String]): Unit = {
//    val success = monadError.pure(42)
//    val failure = monadError.raiseError("Badness")
//
//    println(success) // Right(42)
//    println(failure) // Left(Badness)
//
//    val res = monadError.handleErrorWith(failure) {
//      case "Badness" => monadError.pure("It's ok")
//      case _         => monadError.raiseError("It's not ok")
//    }
//    println(res) // Right(It's ok)
//
//    val res2 = monadError.ensure(success)("Number too low!")(_ > 1000)
//    println(res2) // Left(Number too low!)
//  }

  /**
    * Catsは、cats.syntax.applicativeError経由でraiseErrorとhandleErrorWithを提供する
    * また、cats.syntax.monadError経由でensureを提供する
    */
//  import cats.syntax.applicative._
//  import cats.syntax.applicativeError._
//  import cats.syntax.monadError._
//  import cats.instances.either._
//
//  type ErrorOr[A] = Either[String, A]
//
//  def main(args: Array[String]): Unit = {
//    val success = 42.pure[ErrorOr]
//    val failure = "Badness".raiseError[ErrorOr, Int]
//    val successWith = failure.handleErrorWith {
//      case "Badness" => 256.pure
//      case _         => ("It's not ok").raiseError
//    }
//    val ensure = success.ensure("Number to low!")(_ > 1000)
//
//    println(success) // Right(42)
//    println(failure) // Left(Badness)
//    println(successWith) // Right(256)
//    println(ensure) // Left(Number to low!)
//  }

  // 4.5.3 Instances of MonadError
  /**
    * Catsは、Either、Future、Tryなど多数のデータ型に対してMonadErrorインスタンスを提供する
    * Eitherは任意のエラー型にカスタマイズ可能だが、FutureとTryは常にエラーをThrowablesとして表す
    */
//  import cats.syntax.applicativeError._
//  import scala.util.Try
//  import cats.instances.try_._
//
//  val exn: Throwable = new RuntimeException("It's all gone wrong")
//  exn.raiseError[Try, Int]

  // 4.5.4 Exercise: Abstracting
  /**
    * 以下のシグネチャーを持つvalidateAdultを実装せよ
    *
    * def validateAdult[F[_]](age: Int)(implicit me: MonadError[F, Throwable]): F[Int]
    *
    * 18歳以上だと成功、それ以外の場合はIllegalArgumentExceptionを返す
    */
//  // 答え見た
//  import cats.MonadError
//  import cats.syntax.applicative._
//  import cats.syntax.applicativeError._
//
//  def validateAdult[F[_]](age: Int)(
//      implicit me: MonadError[F, Throwable]): F[Int] =
//    if (age >= 18) age.pure[F]
//    else
//      new IllegalArgumentException("Age must be greater than or equal to 18")
//        .raiseError[F, Int]
//
//  def main(args: Array[String]): Unit = {
//    import scala.util.Try
//    import cats.instances.try_._
//
//    println(validateAdult[Try](18)) // Success(18)
//    println(validateAdult[Try](8))
//    // Failure(java.lang.IllegalArgumentException: Age must be greater than or equal to 18)
//
//    // type ExceptionOr[A] = Either[Throwable, A]
//    // implicitが足りなくて実行できなかった
//    // println(validateAdult[ExceptionOr](-1))
//  }

  // 4.6 The Eval Monad
  /**
    * cats.Evalは、様々な評価モデルを抽象化できるモナドである
    * Evalは結果をメモ化（キャッシュ化）することもできる
    * Evalはスタックセーフである
    */
  // 4.6.1 Eager, Lazy, Memoized, Oh My!
  /**
    * Scalaのvalの評価モデルを見る
    * 副作用のある計算を使用して評価モデルを見る
    */
//  def main(args: Array[String]): Unit = {
//    val x = {
//      println("Computing X")
//      math.random
//    }
//    // xの計算は上記で定義した所で行われているので、以下は計算を再実行しないので同じ結果となる
//    // つまり計算は1回だけ評価されてメモ化されている
//    println(x)
//    println(x)
//    // Computing X
//    // 0.8437424364898989
//    // 0.8437424364898989
//  }

  /**
    * 次にdefの例を見てみる
    */
//  def main(args: Array[String]): Unit = {
//    def y = {
//      println("Computing Y")
//      math.random
//    }
//    // defの場合は使用されるまで評価されず、実行のたびに計算される
//    // つまり、lazyな評価でメモ化もされていない
//    println(y)
//    println(y)
//    // Computing Y
//    // 0.883016946481187
//    // Computing Y
//    // 0.8485539986911335
//  }

  /**
    * 次にlazy valの例を見てみる
    */
//  def main(args: Array[String]): Unit = {
//    lazy val z = {
//      println("Computing Z")
//      math.random
//    }
//    // lazy valは、使用されるまで評価されず、最初に実行された時に結果をメモ化する
//    println(z)
//    println(z)
//    // Computing Z
//    // 0.9851647012959723
//    // 0.9851647012959723
//  }

  // 4.6.2 Eval’s Models of Evaluation
  /**
    * Evalは、Now、Always、Laterの3つのサブタイプがある
    */
//  import cats.Eval
//
//  def main(args: Array[String]): Unit = {
//    val now = Eval.now(math.random + 1000)
//    val always = Eval.always(math.random + 3000)
//    val later = Eval.later(math.random + 2000)
//
//    println(now) // Now(1000.8662074491152)
//    println(always) // cats.Always@edf4efb
//    println(later) // cats.Later@2f7a2457
//
//    println(now.value) // 1000.8662074491152
//    println(always.value) // 3000.159004971797
//    println(later.value) // 2000.0828479897061
//  }

  /**
    * Eval.nowは、valと同様（定義した時に評価され、メモ化される）
    * Eval.alwaysは、defと同様（実行時に評価され、メモ化されない）
    * Eval.laterは、lazy valと同様（実行時に評価され、メモ化される）
    */
  // 4.6.3 Eval as a Monad
  /**
    * 他のすべてのモナドと同様に、Evalのmap関数とflatMap関数はチェーンに計算を追加する
    */
//  import cats.Eval
//
//  def main(args: Array[String]): Unit = {
//    val greeting = Eval
//      .always { println("Step 1"); "Hello" }
//      .map { str =>
//        println("Step 2"); s"$str world"
//      }
//
//    // Eval.alwaysなので、上記のgreetingの宣言時には評価せず、以下で呼び出した時に評価する
//    println(greeting.value)
//    // Step 1
//    // Step 2
//    // Hello world
//  }

  /**
    *
    */
//  import cats.Eval
//
//  def main(args: Array[String]): Unit = {
//    val ans = for {
//      a <- Eval.now { println("Calculating A"); 40 }
//      b <- Eval.always { println("Calculating B"); 2 }
//    } yield {
//      println("Adding A and B")
//      a + b
//    }
//
//    // Eval.nowは宣言時に評価され、Eval.alwaysは呼び出し時に評価される
//
//    // Calculating A
//    println(ans.value)
//    // Calculating B
//    // Adding A and B
//    // 42
//    println(ans.value)
//    // Calculating B
//    // Adding A and B
//    // 42
//  }

  /**
    * Evalはmemoize関数を持つ
    * memoize関数を呼び出すと、それまでの計算結果をキャッシュする
    */
//  import cats.Eval
//
//  def main(args: Array[String]): Unit = {
//    val saying = Eval
//      .always { println("Step 1"); "The cat" }
//      .map { str =>
//        println("Step 2"); s"$str sat on"
//      }
//      .memoize
//      .map { str =>
//        println("Step 3"); s"$str the mat"
//      }
//
//    println(saying.value)
//    // Step 1
//    // Step 2
//    // Step 3
//    // The cat sat on the mat
//    println(saying.value)
//    // Step 3
//    // The cat sat on the mat
//
//    // memoizeによって、`println("Step 2"); s"$str sat on"`までの計算結果をキャッシュしている
//  }

  // 4.6.4 Trampolining and Eval.defer
  /**
    * Evalはmap関数とflatMap関数がトランポリンされる
    * これは、スタックフレームを消費することなく、map関数とflatMap関数の呼び出しを任意にネストできる
    * これを「スタックの安全性」と呼ぶ
    */
//  def factorial(n: BigInt): BigInt = if (n == 1) n else n * factorial(n - 1)
//  factorial(50000) // StackOverflowError

  /**
    * factorial関数をスタックセーフに実装し直す
    */
//  import cats.Eval
//
//  def factorial(n: BigInt): Eval[BigInt] =
//    if (n == 1) Eval.now(n)
//    else factorial(n - 1).map(_ * n)
//
//  factorial(50000).value // StackOverflowError

  /**
    * 上記だとまだStackOverflowErrorになってしまう
    * これは、map関数を呼び出す前にfactorialの再帰呼び出しを行っているからである
    *
    * Eval.deferを使って書き直す
    */
//  import cats.Eval
//
//  def factorial(n: BigInt): Eval[BigInt] =
//    if (n == 1) Eval.now(n)
//    else Eval.defer(factorial(n - 1).map(_ * n))
//
//  factorial(50000).value // StackOverflowErrorにならない

  /**
    * Eval.deferはヒープ上にオブジェクトのチェーンを作成することで、スタックの消費を回避する
    * よって、無制限に使えるわけではなく、スタックの代わりにヒープのサイズによって制限される
    */
  // 4.6.5 Exercise: Safer Folding using Eval
  /**
    * foldRightをEvalを使ってスタックセーフに実装せよ
    */
  // 答え見た
//  import cats.Eval
//
//  def foldRightEval[A, B](as: List[A], acc: Eval[B])(
//      fn: (A, Eval[B]) => Eval[B]): Eval[B] =
//    as match {
//      case head :: tail => Eval.defer(fn(head, foldRightEval(tail, acc)(fn)))
//      case Nil          => acc
//    }
//
//  def foldRight[A, B](as: List[A], acc: B)(fn: (A, B) => B): B =
//    foldRightEval(as, Eval.now(acc)) { (a, b) =>
//      b.map(fn(a, _))
//    }.value
//
//  foldRight((1 to 100000).toList, 0L)(_ + _) // StackOverflowErrorにならない

}
