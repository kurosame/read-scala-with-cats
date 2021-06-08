package sandbox

object Chapter10 {
  // 10 Case Study: Data Validation
  /**
   * このケーススタディでは、バリデーション用のライブラリを構築する
   */

  // 10.1 Sketching the Library Structure
  /**
   * まずは、個々のデータを確認する
   *
   * ■ エラーメッセージの提供
   * 結果は、チェックに合格した場合はチェックした値、エラーの場合はエラーメッセージになる
   * この結果をF[A]として抽象化する
   * また、チェック自体はA => F[A]という関数になる
   *
   * ■ チェックの結合
   * 必要なチェックは以下ができるApplicativeやSemigroupalか
   * (A => F[A], A => F[A]).tupled ---> A => F[A, A]
   *
   * それとも、以下ができるMonoidか
   * A => F[A] |+| A => F[A] ---> A => F[A]
   *
   * Monoidの方がいい（この本では）
   * ⇒ cabiやcapはSemigroupalとMonoidの両方を使っている
   * （mapNやTuple3がSemigroupalで、andThenがMonoid）
   *
   * ■ チェックしながら、エラーを蓄積する
   * エラーをListかNonEmptyListに蓄積する
   *
   * ■ チェックしながら、データを変換する
   * 変換が失敗するかどうかに応じて、mapまたはflatMapする必要があるように思えるため、チェックもモナドにする必要がある
   * A => F[B].map(B => C) ---> A => F[C]
   * A => F[B].flatMap(B => (A => F[C]) ---> A => F[C]
   * ※ チェックがA => F[B]の場合
   */

  // 10.2 The Check Datatype
  /**
   * Check型エイリアスを定義する
   *
   * type Check[A] = A => Either[String, A]
   *
   * ただし、上記ではエラーメッセージがStringで固定されてしまっている
   * ユーザーが必要なエラーの型を指定できるように以下にする
   *
   * type Check[E, A] = A => Either[E, A]
   *
   * さらに、カスタム関数を追加したい場合があるので、型エイリアスではなく、traitにする
   */
//  trait Check[E, A] {
//    def apply(value: A): Either[E, A]
//
//    // other methods...
//  }

  // 10.3 Basic Combinators
  /**
   * and関数をCheckに追加する
   * この関数は2つのチェックを1つに結合し、両方のチェックが成功した場合にのみsucceedになる
   */
//  trait Check[E, A] {
//    def and(that: Check[E, A]): Check[E, A] = ???
//  }

  /**
   * ここで問題なのは、両方のチェックが失敗した時にどうするか
   * この場合は両方のエラーを返すことが正しいが、現状Eを結合する方法はない
   * 累積の概念の抽象化する型クラスが必要になる
   */
//  import cats.Semigroup
//  import cats.instances.list._
//  import cats.syntax.semigroup._
//
//  def main(args: Array[String]): Unit = {
//    val semigroup = Semigroup[List[String]]
//
//    val res1 = semigroup.combine(List("Badness"), List("More badness"))
//    val res2 = List("Oh noes") |+| List("Fail happened")
//
//    println(res1) // List(Badness, More badness)
//    println(res2) // List(Oh noes, Fail happened)
//  }

  /**
   * 上記のようにEのSemigroupが必要
   * そして、combine関数や|+|を使用してEの値を結合できる
   * 単位元は不要なので、Monoidである必要はない
   */

  /**
   * 最初のチェックに失敗した場合、ショートさせるべきかというセマンティック的な問題もある
   * 最も有用な行動は何だろうか
   *
   * 答えは、
   * 可能な限りすべてのエラーを報告したいので、ショートさせずに最後までチェックした方がよい
   * and関数の場合、互いのチェックは独立しているので、発生したエラーを結合できる
   */

  /**
   * 上記のことを踏まえて、andを実装する
   */
//  import cats.Semigroup
//  import cats.syntax.either._
//  import cats.syntax.semigroup._
//
//  final case class CheckF[E, A](func: A => Either[E, A]) {
//    def apply(a: A): Either[E, A] = func(a)
//
//    def and(that: CheckF[E, A])(implicit s: Semigroup[E]): CheckF[E, A] =
//      CheckF { a =>
//        (this(a), that(a)) match {
//          case (Left(e1), Left(e2)) => (e1 |+| e2).asLeft
//          case (Left(e), Right(_))  => e.asLeft
//          case (Right(_), Left(e))  => e.asLeft
//          case (Right(_), Right(_)) => a.asRight
//        }
//      }
//  }
//
//  def main(args: Array[String]): Unit = {
//    val a: CheckF[List[String], Int] = CheckF { v =>
//      if (v > 2) v.asRight else List("Must be > 2").asLeft
//    }
//
//    val b: CheckF[List[String], Int] = CheckF { v =>
//      if (v < -2) v.asRight else List("Must be < -2").asLeft
//    }
//
//    import cats.instances.list._
//    val check: CheckF[List[String], Int] = a.and(b)
//
//    println(check(5)) // Left(List(Must be < -2))
//    println(check(0)) // Left(List(Must be > 2, Must be < -2))
//  }

  /**
   * Either[E, A]ではなく、Validated[E, A]を使った方がよい
   * 以下の実装はcabiやcapでも使っているやつ
   *
   * また、orコンビネーターも追加する
   * orはセマンティック的に途中でショートさせても問題ない
   */
//  import cats.Semigroup
//  import cats.data.Validated
//  import cats.syntax.semigroup._
//  import cats.syntax.apply._
//  import cats.data.Validated._
//
//  sealed trait Check[E, A] {
//    import Check._
//
//    def and(that: Check[E, A]): Check[E, A] = And(this, that)
//
//    def or(that: Check[E, A]): Check[E, A] = Or(this, that)
//
//    def apply(a: A)(implicit s: Semigroup[E]): Validated[E, A] =
//      this match {
//        case Pure(func)       => func(a)
//
//        case And(left, right) => (left(a), right(a)).mapN((_, _) => a)
//
//        case Or(left, right) =>
//          left(a) match {
//            case Valid(a) => Valid(a)
//            case Invalid(e1) =>
//              right(a) match {
//                case Valid(a)    => Valid(a)
//                case Invalid(e2) => Invalid(e1 |+| e2)
//              }
//          }
//      }
//  }
//  object Check {
//    final case class And[E, A](left: Check[E, A], right: Check[E, A]) extends Check[E, A]
//    final case class Or[E, A](left: Check[E, A], right: Check[E, A]) extends Check[E, A]
//    final case class Pure[E, A](func: A => Validated[E, A]) extends Check[E, A]
//  }

  /**
   * andやorによってエラーの蓄積ができるようになった
   * 次はデータを変換するmap関数を実装する
   */

}
