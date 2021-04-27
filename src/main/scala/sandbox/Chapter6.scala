package sandbox

object Chapter6 {
  // 6 Semigroupal and Applicative
  /**
   * ファンクターとモナドでは表現できない特定の種類の処理がある
   * その1つがフォームのバリデーションである
   * フォームを検証する時、最初のエラーで停止せずにすべてのエラーをまとめてユーザーに返す必要がある
   *
   * 以下のコードでは、最初のparseIntでエラーになり、それ以上は進まない
   */
  //  import cats.syntax.either._
  //
  //  def parseInt(str: String): Either[String, Int] =
  //    Either
  //      .catchOnly[NumberFormatException](str.toInt)
  //      .leftMap(_ => s"Couldn't read $str")
  //
  //  def main(args: Array[String]): Unit = {
  //    val res = for {
  //      a <- parseInt("a")
  //      b <- parseInt("b")
  //      c <- parseInt("c")
  //    } yield (a + b + c)
  //
  //    println(res)
  //  }

  /**
   * もう1つの例は複数のFutureの並列化である
   * モナドだと並列化できず、順番に実行するしかない
   * 並列化を実現するためには、mapやflatMapと違いシーケンスを保証しないものが必要である
   * この章では、このパターンをサポートする3つの型クラスについて説明する
   * ・Semigroupal
   * ・Parallel
   * ・Applicative
   */
  // 6.1 Semigroupal
  /**
   * 以下のfaとfbは独立しており、product関数の引数へ渡す際にどちらの順序でも計算できる
   * 引数に厳密な順序を課すflatMapとは対照的である
   */
  //  trait Semigroupal[F[_]] {
  //    def product[A, B](fa: F[A], fb: F[B]): F[(A, B)]
  //  }

  // 6.1.1 Joining Two Contexts
  /**
   * Semigroupは値を結合できるが、Semigroupalはコンテキストを結合できる
   */
  //  import cats.Semigroupal
  //  import cats.instances.option._
  //
  //  def main(args: Array[String]): Unit = {
  //    println(Semigroupal[Option].product(Some(123), Some("abc"))) // Some((123,abc))
  //    println(Semigroupal[Option].product(None, Some("abc"))) // None
  //    println(Semigroupal[Option].product(Some(123), None)) // None
  //  }

  // 6.1.2 Joining Three or More Contexts
  /**
   * Semigroupalのコンパニオンオブジェクトは、product以外にも様々な関数を持っている
   * 以下のtuple、mapなどはtuple2からtuple22まである
   */
  //  import cats.Semigroupal
  //  import cats.instances.option._
  //
  //  def main(args: Array[String]): Unit = {
  //    val res1 = Semigroupal.tuple3(Option(1), Option(2), Option(3))
  //    val res2 = Semigroupal.tuple3(Option(1), Option(2), Option.empty[Int])
  //    val res3 = Semigroupal.map3(Option(1), Option(2), Option(3))(_ + _ + _)
  //    val res4 = Semigroupal.map2(Option(1), Option.empty[Int])(_ + _)
  //
  //    println(res1) // Some((1,2,3))
  //    println(res2) // None
  //    println(res3) // Some(6)
  //    println(res4) // None
  //  }

  // 6.1.3 Semigroupal Laws
  /**
   * Semigroupalの法則は以下の1つだけである
   *
   * product(a, product(b, c)) == product(product(a, b), c)
   */
  // 6.2 Apply Syntax
  /**
   * Catsはapply、tupled、mapN構文を提供する
   *
   * tupledは最大22個のタプルに対応できる
   * tupledよりもmapNを使用するのが一般的である
   */
  //  import cats.instances.option._
  //  import cats.syntax.apply._
  //
  //  def main(args: Array[String]): Unit = {
  //    println((Option(123), Option("abc")).tupled) // Some((123,abc))
  //
  //    final case class Cat(name: String, born: Int, color: String)
  //    val res = (
  //      Option("Garfield"),
  //      Option(1978),
  //      Option("Orange & black")
  //    ).mapN(Cat.apply)
  //
  //    println(res) // Some(Cat(Garfield,1978,Orange & black))
  //  }

  // 6.2.1 Fancy Functors and Apply Syntax
  /**
   * contramapN、imapNもある
   */
//  import cats.Monoid
//  import cats.instances.int._
//  import cats.instances.invariant._
//  import cats.instances.list._
//  import cats.instances.string._
//  import cats.syntax.apply._
//
//  final case class Cat(name: String,
//                       yearOfBirth: Int,
//                       favoriteFoods: List[String])
//
//  val tupleToCat: (String, Int, List[String]) => Cat = Cat.apply _
//  val catToTuple: Cat => (String, Int, List[String]) =
//    cat => (cat.name, cat.yearOfBirth, cat.favoriteFoods)
//
//  implicit val catMonoid: Monoid[Cat] = (
//    Monoid[String],
//    Monoid[Int],
//    Monoid[List[String]]
//  ).imapN(tupleToCat)(catToTuple)
//
//  def main(args: Array[String]): Unit = {
//    import cats.syntax.semigroup._
//    val garfield = Cat("Garfield", 1978, List("Lasagne"))
//    val heathcliff = Cat("Heathcliff", 1988, List("Junk Food"))
//
//    // 2章で使った|+|構文で結合できる
//    println(garfield |+| heathcliff) // Cat(GarfieldHeathcliff,3966,List(Lasagne, Junk Food))
//  }

  // 6.3 Semigroupal Applied to Different Types
  /**
   * Futureは並列実行を提供する
   */
//  import cats.Semigroupal
//  import cats.instances.future._
//  import scala.concurrent._
//  import scala.concurrent.duration._
//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  def main(args: Array[String]): Unit = {
//    val futurePair = Semigroupal[Future].product(Future("Hello"), Future(123))
//    println(Await.result(futurePair, 1.second)) // (Hello,123)
//
//    import cats.syntax.apply._
//    case class Cat(name: String, yearOfBirth: Int, favoriteFoods: List[String])
//    val futureCat = (
//      Future("Garfield"),
//      Future(1978),
//      Future(List("Lasagne"))
//    ).mapN(Cat.apply)
//
//    println(Await.result(futureCat, 1.second)) // Cat(Garfield,1978,List(Lasagne))
//  }

  /**
   * List
   */
//  import cats.Semigroupal
//  import cats.instances.list._
//
//  def main(args: Array[String]): Unit = {
//    val res = Semigroupal[List].product(List(1, 2), List(3, 4))
//    println(res) // List((1,3), (1,4), (2,3), (2,4))
//  }

  /**
   * Either
   *
   * 以下の例ではproductは最初のエラーを確認して停止している
   * flatMapを使えばエラーを蓄積することもできる
   */
//  import cats.Semigroupal
//  import cats.instances.either._
//
//  type ErrorOr[A] = Either[Vector[String], A]
//
//  def main(args: Array[String]): Unit = {
//    val res = Semigroupal[ErrorOr].product(
//      Left(Vector("Error 1")),
//      Left(Vector("Error 2"))
//    )
//    println(res) // Left(Vector(Error 1))
//  }

  // 6.3.1 Semigroupal Applied to Monads
  /**
   * Semigroupal（及びApplicative）のインスタンスを持ち、Monadを持たない有用なデータ型を作成できる
   * これにより、様々な方法でproductを実装できるようになる
   */
  // 6.3.1.1 Exercise: The Product of Lists
  /**
   * Listのproductはデカルト積を生成することを確かめよ
   */
  // 答え見た
//  import cats.Monad
//  import cats.syntax.functor._
//  import cats.syntax.flatMap._
//
//  def product[F[_]: Monad, A, B](x: F[A], y: F[B]): F[(A, B)] =
//    x.flatMap(a => y.map(b => (a, b)))
//
//  def main(args: Array[String]): Unit = {
//    import cats.instances.list._
//    println(product(List(1, 2), List(3, 4))) // List((1,3), (1,4), (2,3), (2,4))
//  }

  // 6.4 Parallel
  /**
   * Eitherのproduct関数で複数のエラーが起きると最初のエラーで停止する
   */
//  import cats.Semigroupal
//  import cats.instances.either._
//
//  type ErrorOr[A] = Either[Vector[String], A]
//  type ErrorOrList[A] = Either[List[String], A]
//
//  def main(args: Array[String]): Unit = {
//    val error1: ErrorOr[Int] = Left(Vector("Error 1"))
//    val error2: ErrorOr[Int] = Left(Vector("Error 2"))
//
//    val res1 = Semigroupal[ErrorOr].product(error1, error2)
//    println(res1) // Left(Vector(Error 1))
//
//    // tupledを使って書くこともできる
//    import cats.syntax.apply._
//    val res2 = (error1, error2).tupled
//    println(res2) // Left(Vector(Error 1))
//
//    // すべてのエラーを収集する場合は、parTupled関数を使う
//    import cats.implicits._
//    val res3 = (error1, error2).parTupled
//    println(res3) // Left(Vector(Error 1, Error 2))
//
//    // エラーのListでも機能する
//    val errStr1: ErrorOrList[Int] = Left(List("error 1"))
//    val errStr2: ErrorOrList[Int] = Left(List("error 2"))
//
//    val res4 = (errStr1, errStr2).parTupled
//    println(res4) // Left(List(error 1, error 2))
//
//    // もっとも一般的に使われるのはparMapNである
//    val success1: ErrorOr[Int] = Right(1)
//    val success2: ErrorOr[Int] = Right(2)
//    val addTwo = (x: Int, y: Int) => x + y
//
//    val res5e = (error1, error2).parMapN(addTwo)
//    val res5s = (success1, success2).parMapN(addTwo)
//    println(res5e) // Left(Vector(Error 1, Error 2))
//    println(res5s) // Right(3)
//  }

  /**
   * 以下はParallelの定義である
   *
   * ・Applicativeインスタンスを持つ関連型コンストラクターFがある
   * ・Mはモナドインスタンスが必要
   * ・~>はFunctionKの型エイリアスでMをFに変換できる
   *  MとFは型コンストラクターなので、~>[M, F]はM[A]からF[A]への変換である
   */
//  import cats._
//
//  trait Parallel[M[_]] {
//    type F[_]
//
//    def applicative: Applicative[F]
//    def monad: Monad[M]
//    def parallel: ~>[M, F]
//  }

  /**
   * OptionをListに変換するFunctionKを定義して、簡単な例を見てみる
   */
//  import cats.arrow.FunctionK
//
//  object optionToList extends FunctionK[Option, List] {
//    def apply[A](fa: Option[A]): List[A] =
//      fa match {
//        case None    => List.empty[A]
//        case Some(a) => List(a)
//      }
//  }
//
//  def main(args: Array[String]): Unit = {
//    println(optionToList(Some(1))) // List(1)
//    println(optionToList(None)) // List()
//  }

  /**
   * FunctionKは汎用型Aの検査はできない、変換はMとFの構造に関して実行する必要がある
   * 要約するとParallelを使うことで、モナドインスタンスを持つ型をアプリカティブインスタンスを持つ型に変換できる
   */
  // 6.4.0.1 Exercise: Parallel List
  /**
   * ListのParallelインスタンスを定義せよ
   */
  // 答え見た
//  import cats.implicits._
//
//  def main(args: Array[String]): Unit = {
//    val res1 = (List(1, 2), List(3, 4)).tupled
//    val res2 = (List(1, 2), List(3, 4)).parTupled
//    println(res1) // List((1,3), (1,4), (2,3), (2,4))
//    println(res2) // List((1,3), (2,4))
//  }

  // 6.5 Apply and Applicative
  /**
   * Catsは、以下の2つの型クラスを使用して、Applicativeをモデル化する
   * ・cats.Applyは、SemigroupalとFunctorを拡張し、コンテキスト内の関数にパラメーターを適用するap関数を追加する
   * ・cats.Applicativeは、Applyを拡張し、pure関数を追加する
   */
//  import cats.Semigroupal
//  import cats.Functor
//
//  trait Apply[F[_]] extends Semigroupal[F] with Functor[F] {
//    def ap[A, B](ff: F[A => B])(fa: F[A]): F[B]
//
//    def product[A, B](fa: F[A], fb: F[B]): F[(A, B)] =
//      ap(map(fa)(a => (b: B) => (a, b)))(fb)
//  }
//
//  trait Applicative[F[_]] extends Apply[F] {
//    def pure[A](a: A): F[A]
//  }

  /**
   * ap関数は、faをコンテキストF[_]内の関数ffに適用する
   * product関数は、apとmapから定義されている
   * productとapとmapには密接な関係がある
   */

  // 6.5.1 The Hierarchy of Sequencing Type Classes
  /**
   * 型クラスの関係階層は以下である
   * 1. Semigroupal(product), Functor(map)
   * ↑2. Apply(ap)
   * ↑3. Applicative(pure), FlatMap(flatMap)
   * ↑4. Monad
   *
   * ・すべてのMonadは、Applicativeである ⇒ pureとflatMapの両方を継承してることが前提
   * ・すべてのApplicativeは、Semigroupalである
   * など
   */

  // 6.6 Summary
}
