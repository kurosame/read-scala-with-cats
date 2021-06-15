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

  // 10.4 Transforming Data
  /**
   * 現状のCheckの型エイリアスだとmapは使えない
   *
   * type Check[E, A] = A => Either[E, A]
   * def map(check: Check[E, A])(func: A => B): Check[E, ???]
   * ⇒ Check型にBが必要
   *
   * Checkの型エイリアスを以下に変更する
   */
//  type Check[E, A, B] = A => Either[E, B]
//
//  // この新しいCheck型により、parseIntのようにStringをIntとして解析するケースが実現できる
//  val parseInt: Check[List[String], String, Int] = ???

  /**
   * しかし、入力型と出力型を分割すると、別の問題が発生する
   * これまではチェックが成功すると常に入力を返してきた
   */
//  (this(a), that(a)) match {
//    case And(left, right) =>
//      (left(a), right(a)).mapN((result1, result2) => Right(a))
//  }

  /**
   * しかし、新しいCheck型では、Either[E, B]なので、Right(a)を返すことはできない
   * Right(result1)とRight(result2)のどちらを返すかを任意に選択する必要がある
   */

  // 10.4.1 Predicates
  /**
   * 今までCheckと呼んでいたものをPredicateに変更する
   * PredicateはPredicateが成功した場合、常に入力を返す（以前のCheckと同じ）
   */
//  import cats.Semigroup
//  import cats.data.Validated
//  import cats.syntax.semigroup._
//  import cats.syntax.apply._
//  import cats.data.Validated._
//
//  sealed trait Predicate[E, A] {
//    def and(that: Predicate[E, A]): Predicate[E, A] = And(this, that)
//
//    def or(that: Predicate[E, A]): Predicate[E, A] = Or(this, that)
//
//    def apply(a: A)(implicit s: Semigroup[E]): Validated[E, A] =
//      this match {
//        case Pure(func) => func(a)
//
//        case And(left, right) => (left(a), right(a)).mapN((_, _) => a)
//
//        case Or(left, right) =>
//          left(a) match {
//            case Valid(_) => Valid(a)
//            case Invalid(e1) =>
//              right(a) match {
//                case Valid(_)    => Valid(a)
//                case Invalid(e2) => Invalid(e1 |+| e2)
//              }
//          }
//      }
//  }
//  final case class And[E, A](left: Predicate[E, A], right: Predicate[E, A]) extends Predicate[E, A]
//  final case class Or[E, A](left: Predicate[E, A], right: Predicate[E, A]) extends Predicate[E, A]
//  final case class Pure[E, A](func: A => Validated[E, A]) extends Predicate[E, A]

  // 10.4.2 Checks
  /**
   * 入力の変換も可能にするCheckを実装する
   */
//  import cats.Semigroup
//  import cats.data.Validated
//
//  sealed trait Check[E, A, B] {
//    import Check._
//
//    def apply(in: A)(implicit s: Semigroup[E]): Validated[E, B]
//
//    def map[C](f: B => C): Check[E, A, C] = Map[E, A, B, C](this, f)
//  }
//
//  object Check {
//    final case class Map[E, A, B, C](check: Check[E, A, B], func: B => C) extends Check[E, A, C] {
//      def apply(in: A)(implicit s: Semigroup[E]): Validated[E, C] =
//        check(in).map(func)
//    }
//
//    final case class Pure[E, A](pred: Predicate[E, A]) extends Check[E, A, A] {
//      def apply(in: A)(implicit s: Semigroup[E]): Validated[E, A] =
//        pred(in)
//    }
//
//    def apply[E, A](pred: Predicate[E, A]): Check[E, A, A] = Pure(pred)
//  }

  /**
   * CheckのflatMapはどうなるか
   */
//  import cats.Semigroup
//  import cats.data.Validated
//
//  sealed trait Check[E, A, B] {
//    def apply(in: A)(implicit s: Semigroup[E]): Validated[E, B]
//
//    def flatMap[C](f: B => Check[E, A, C]) =
//      FlatMap[E, A, B, C](this, f)
//  }
//
//  final case class FlatMap[E, A, B, C](check: Check[E, A, B], func: B => Check[E, A, C]) extends Check[E, A, C] {
//    def apply(a: A)(implicit s: Semigroup[E]): Validated[E, C] =
//      check(a).withEither(_.flatMap(b => func(b)(a).toEither))
//  }

  /**
   * 2つのチェックをチェーンするandThenを実装する
   */
//  import cats.Semigroup
//  import cats.data.Validated
//
//  sealed trait Check[E, A, B] {
//    def apply(in: A)(implicit s: Semigroup[E]): Validated[E, B]
//
//    def andThen[C](that: Check[E, B, C]): Check[E, A, C] =
//      AndThen[E, A, B, C](this, that)
//  }
//
//  final case class AndThen[E, A, B, C](check1: Check[E, A, B], check2: Check[E, B, C]) extends Check[E, A, C] {
//    def apply(a: A)(implicit s: Semigroup[E]): Validated[E, C] =
//      check1(a).withEither(_.flatMap(b => check2(b).toEither))
//  }

  // 10.4.3 Recap
  /**
   * Predicateの完全な実装は以下
   */
//  import cats.Semigroup
//  import cats.data.Validated
//  import cats.syntax.semigroup._
//  import cats.syntax.apply._
//  import cats.syntax.validated._
//
//  sealed trait Predicate[E, A] {
//    import Predicate._
//    import Validated._
//
//    def and(that: Predicate[E, A]): Predicate[E, A] = And(this, that)
//
//    def or(that: Predicate[E, A]): Predicate[E, A] = Or(this, that)
//
//    def apply(a: A)(implicit s: Semigroup[E]): Validated[E, A] =
//      this match {
//        case Pure(func) => func(a)
//
//        case And(left, right) => (left(a), right(a)).mapN((_, _) => a)
//
//        case Or(left, right) =>
//          left(a) match {
//            case Valid(_) => Valid(a)
//            case Invalid(e1) =>
//              right(a) match {
//                case Valid(_)    => Valid(a)
//                case Invalid(e2) => Invalid(e1 |+| e2)
//              }
//          }
//      }
//  }
//
//  object Predicate {
//    final case class And[E, A](left: Predicate[E, A], right: Predicate[E, A]) extends Predicate[E, A]
//    final case class Or[E, A](left: Predicate[E, A], right: Predicate[E, A]) extends Predicate[E, A]
//    final case class Pure[E, A](func: A => Validated[E, A]) extends Predicate[E, A]
//
//    def apply[E, A](f: A => Validated[E, A]): Predicate[E, A] = Pure(f)
//
//    def lift[E, A](err: E, fn: A => Boolean): Predicate[E, A] =
//      Pure(a => if (fn(a)) a.valid else err.invalid)
//  }

  /**
   * Checkの完全な実装は以下
   */
//  import cats.Semigroup
//  import cats.data.Validated
//
//  sealed trait Check[E, A, B] {
//    import Check._
//
//    def apply(in: A)(implicit s: Semigroup[E]): Validated[E, B]
//
//    def map[C](f: B => C): Check[E, A, C] = Map[E, A, B, C](this, f)
//
//    def flatMap[C](f: B => Check[E, A, C]) =
//      FlatMap[E, A, B, C](this, f)
//
//    def andThen[C](next: Check[E, B, C]): Check[E, A, C] =
//      AndThen[E, A, B, C](this, next)
//  }
//
//  object Check {
//    final case class Map[E, A, B, C](check: Check[E, A, B], func: B => C) extends Check[E, A, C] {
//      def apply(a: A)(implicit s: Semigroup[E]): Validated[E, C] = check(a).map(func)
//    }
//
//    final case class FlatMap[E, A, B, C](check: Check[E, A, B], func: B => Check[E, A, C]) extends Check[E, A, C] {
//      def apply(a: A)(implicit s: Semigroup[E]): Validated[E, C] =
//        check(a).withEither(_.flatMap(b => func(b)(a).toEither))
//    }
//
//    final case class AndThen[E, A, B, C](check: Check[E, A, B], next: Check[E, B, C]) extends Check[E, A, C] {
//      def apply(a: A)(implicit s: Semigroup[E]): Validated[E, C] =
//        check(a).withEither(_.flatMap(b => next(b).toEither))
//    }
//
//    final case class Pure[E, A, B](func: A => Validated[E, B]) extends Check[E, A, B] {
//      def apply(a: A)(implicit s: Semigroup[E]): Validated[E, B] = func(a)
//    }
//
//    final case class PurePredicate[E, A](pred: Predicate[E, A]) extends Check[E, A, A] {
//      def apply(a: A)(implicit s: Semigroup[E]): Validated[E, A] = pred(a)
//    }
//
//    def apply[E, A](pred: Predicate[E, A]): Check[E, A, A] = PurePredicate(pred)
//
//    def apply[E, A, B](func: A => Validated[E, B]): Check[E, A, B] = Pure(func)
//  }

  /**
   * Predicateにはモノイドがあり、Checkにはモナドがある
   * いくつかのバリデーションチェックの例を実装する
   * Predicateは入力のチェックがTrueの場合、入力を返す
   * Checkは入力を変換して、返す
   */
//  import cats.data.{NonEmptyList, Validated}
//  import cats.syntax.apply._
//  import cats.syntax.validated._
//
//  type Errors = NonEmptyList[String]
//
//  def error(s: String): NonEmptyList[String] = NonEmptyList(s, Nil)
//
//  def longerThan(n: Int): Predicate[Errors, String] =
//    Predicate.lift(error(s"Must be longer than $n characters"), str => str.size > n)
//
//  val alphanumeric: Predicate[Errors, String] =
//    Predicate.lift(error(s"Must be all alphanumeric characters"), str => str.forall(_.isLetterOrDigit))
//
//  def contains(char: Char): Predicate[Errors, String] =
//    Predicate.lift(error(s"Must contain the character $char"), str => str.contains(char))
//
//  def containsOnce(char: Char): Predicate[Errors, String] =
//    Predicate.lift(error(s"Must contain the character $char only once"), str => str.filter(c => c == char).size == 1)
//
//  val checkUsername: Check[Errors, String, String] = Check(longerThan(3).and(alphanumeric))
//
//  val splitEmail: Check[Errors, String, (String, String)] =
//    Check(_.split('@') match {
//      case Array(name, domain) => (name, domain).validNel[String]
//      case _                   => "Must contain a single @ character".invalidNel[(String, String)]
//    })
//
//  val checkLeft: Check[Errors, String, String] = Check(longerThan(0))
//
//  val checkRight: Check[Errors, String, String] = Check(longerThan(3).and(contains('.')))
//
//  val joinEmail: Check[Errors, (String, String), String] =
//    Check { case (l, r) =>
//      (checkLeft(l), checkRight(r)).mapN(_ + "@" + _)
//    }
//
//  val checkEmail: Check[Errors, String, String] = splitEmail.andThen(joinEmail)
//
//  final case class User(username: String, email: String)
//
//  def createUser(username: String, email: String): Validated[Errors, User] =
//    (checkUsername(username), checkEmail(email)).mapN(User)

  // 10.5 Kleislis
  /**
   * Predicateは本質的にA => Validated[E, A]であり、Checkはこれらの関数を基礎としたラッパーである
   *
   * A => Validated[E, A]はA => F[B]に抽象化できる
   * これはflatMap関数に渡す関数の型である
   *
   * ...
   */

  // 10.6 Summary
}
