package chapter6

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

}
