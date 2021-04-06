package chapter5

object Chapter5 {
  // 5 Monad Transformers
  /**
    * DBからユーザー情報を検索したいとする
    * ユーザーは存在しない可能性があるので、Option[User]を返す
    * また、DBは通信障害などで失敗する可能性があるので、最終結果はEither[Error, Option[User]]を返す
    *
    * ユーザー情報を利用するには、以下のようにflatMapをネストする必要があり、面倒である
    */
//  def lookupUserName(id: Long): Either[Error, Option[String]] =
//    for {
//      optUser <- lookupUser(id)
//    } yield {
//      for {
//        user <- optUser
//      } yield user.name
//    }

  // 5.1 Exercise: Composing Monads
  /**
    * 2つの任意のモナドを組み合わせて1つのモナドを作成できるか
    */
//  import cats.Monad
//  import cats.syntax.applicative._
//
//  def compose[M1[_]: Monad, M2[_]: Monad] = {
//    type Composed[A] = M1[M2[A]]
//
//    new Monad[Composed] {
//      def pure[A](a: A): Composed[A] = a.pure[M2].pure[M1]
//      def flatMap[A, B](fa: Composed[A])(f: A => Composed[B]): Composed[B] = ???
//    }
//  }

  /**
    * 上記のM1とM2は何か不明なので、flatMapの定義を書くことは不可能である
    * ただし、M2が分かっている場合、たとえばM2がOptionだとすると、flatMapを定義できるようになる
    */
//  import cats.Monad
//  import cats.syntax.applicative._
//
//  def compose[M1[_]: Monad, M2[_]: Monad] = {
//    type Composed[A] = M1[M2[A]]
//
//    new Monad[Composed] {
//      def pure[A](a: A): Composed[A] = a.pure[M2].pure[M1]
//      def flatMap[A, B](fa: Composed[A])(f: A => Composed[B]): Composed[B] =
//        fa.flatMap(_.fold[Composed[B]](None.pure[M1])(f))
//    }
//  }

  /**
    * 上記のflatMapの定義のNoneはOption固有の概念である
    * Optionを他のモナドと組み合わせるためには、このように追加の詳細が必要
    * これがモナド変換子の考え方になる
    * Catsでは様々なモナド変換子が定義されており、他のモナドでそのモナドを構成するために必要な追加の詳細を提供する
    */
  // 5.2 A Transformative Example
  /**
    * CatsはEitherTやOptionTなどTサフィックスが付いたモナド変換子を提供する
    * 以下は、OptionTを利用して、ListとOptionを構成する例である
    */
//  import cats.data.OptionT
//
//  type ListOption[A] = OptionT[List, A]

  /**
    * 上記は、OptionT[List, A]を利用して、List[Option[A]]に変換できる
    *
    * 以下はListOptionのインスタンスを作成している
    */
//  import cats.data.OptionT
//  import cats.instances.list._
//  import cats.syntax.applicative._
//
//  type ListOption[A] = OptionT[List, A]
//
//  def main(args: Array[String]): Unit = {
//    val result1: ListOption[Int] = OptionT(List(Option(10)))
//    val result2: ListOption[Int] = 32.pure[ListOption]
//
//    println(result1) // OptionT(List(Some(10)))
//    println(result2) // OptionT(List(Some(32)))
//
//    val result3 = result1.flatMap { (x: Int) =>
//      result2.map { (y: Int) =>
//        x + y
//      }
//    }
//    println(result3) // OptionT(List(Some(42)))
//  }

  // 5.3 Monad Transformers in Cats
  /**
    * モナド変換子を理解するために必要な概念は以下である
    * ・利用可能な変換子クラス
    * ・変換子を使用してモナドのスタックを構築する方法
    * ・モナドスタックのインスタンスを構築する方法
    * ・ラップされたモナドにアクセスするためにスタックを引き離す方法
    */
  // 5.3.1 The Monad Transformer Classes
  /**
    * Catsで利用可能な変換子クラスのインスタンスは以下である
    * ・cats.data.OptionT
    * ・cats.data.EitherT
    * ・cats.data.ReaderT
    * ・cats.data.WriterT
    * ・cats.data.StateT
    * ・cats.data.IdT
    *
    * また、cats.data.KleisliとReaderTは同じもので、実際にはReaderTはKleisliの型エイリアスである
    */
  // 5.3.2 Building Monad Stacks
  /**
    * それぞれのモナド変換子はすべて同じ規則に従う
    *
    * type ListOption[A] = OptionT[List, A]
    * 上記はList[Option[A]]と同義だが、モナド変換子のモナド（Option）は内側、Listは外側のモナドとなっている
    *
    * type ErrorOr[A] = Either[String, A]
    * type ErrorOrOption[A] = OptionT[ErrorOr, A]
    * Either型でOptionTを使う場合、Eitherは2つの型パラメーターが必要になる
    */
//  import cats.syntax.applicative._
//  import cats.data.OptionT
//  import cats.instances.either._
//
//  type ErrorOrOption[A] = OptionT[ErrorOr, A]
//  type ErrorOr[A] = Either[String, A]
//
//  def main(args: Array[String]): Unit = {
//    val a = 10.pure[ErrorOrOption]
//    val b = 32.pure[ErrorOrOption]
//    val c = a.flatMap(x => b.map(y => x + y))
//
//    println(a) // OptionT(Right(Some(10)))
//    println(b) // OptionT(Right(Some(32)))
//    println(c) // OptionT(Right(Some(42)))
//  }

  /**
    * FutureとEitherとOptionの3つのモナドを組み合わせる
    */
//  import cats.syntax.applicative._
//  import scala.concurrent.Future
//  import cats.data.{EitherT, OptionT}
//  import cats.instances.future._
//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  type FutureEither[A] = EitherT[Future, String, A]
//  type FutureEitherOption[A] = OptionT[FutureEither, A]
//
//  def main(args: Array[String]): Unit = {
//    val futureEitherOr: FutureEitherOption[Int] = {
//      for {
//        a <- 10.pure[FutureEitherOption]
//        b <- 32.pure[FutureEitherOption]
//      } yield a + b
//    }
//    println(futureEitherOr) // OptionT(EitherT(Future(<not completed>)))
//  }

  // 5.3.3 Constructing and Unpacking Instances
  /**
    * モナド変換子はvalue関数で解凍できる
    */
//  import cats.syntax.applicative._
//  import cats.instances.either._
//  import cats.data.{OptionT, EitherT}
//  import cats.instances.future._
//  import scala.concurrent.{Future, Await}
//  import scala.concurrent.ExecutionContext.Implicits.global
//  import scala.concurrent.duration.DurationInt
//
//  type ErrorOrOption[A] = OptionT[ErrorOr, A]
//  type ErrorOr[A] = Either[String, A]
//
//  type FutureEither[A] = EitherT[Future, String, A]
//  type FutureEitherOption[A] = OptionT[FutureEither, A]
//
//  def main(args: Array[String]): Unit = {
//    val errorStack1 = OptionT[ErrorOr, Int](Right(Some(10)))
//    val errorStack2 = 32.pure[ErrorOrOption]
//
//    println(errorStack1) // OptionT(Right(Some(10)))
//    println(errorStack2) // OptionT(Right(Some(32)))
//    println(errorStack1.value) // Right(Some(10))
//    println(errorStack2.value.map(_.getOrElse(-1))) // Right(32)
//
//    val futureEitherOr: FutureEitherOption[Int] = {
//      for {
//        a <- 10.pure[FutureEitherOption]
//        b <- 32.pure[FutureEitherOption]
//      } yield a + b
//    }
//    val intermediate = futureEitherOr.value
//    val stack = intermediate.value
//    val res = Await.result(stack, 1.second)
//
//    println(futureEitherOr) // OptionT(EitherT(Future(Success(Right(Some(42))))))
//    println(intermediate) // EitherT(Future(Success(Right(Some(42)))))
//    println(stack) // Future(Success(Right(Some(42))))
//    println(res) // Right(Some(42))
//  }

  // 5.3.4 Default Instances
  /**
    * Catsの多くのモナドは対応する変換子とIdモナドを使用して定義される
    *
    * type Reader[E, A] = ReaderT[Id, E, A] // = Kleisli[Id, E, A]
    * type Writer[W, A] = WriterT[Id, W, A]
    * type State[S, A] = StateT[Id, S, A]
    */
  // 5.3.5 Usage Patterns
  /**
    * モナド変換子は汎用的に使うのが難しい場合がある
    * 様々なコンテキストでモナドを操作するため、アンパックとリパックを行う必要が生じる可能性がある
    * ローカルでモナド変換子を定義することで使用するモナド変換子を開発者が選択することができる
    */
//  import cats.data.Writer
//  import cats.instances.list._
//
//  type Logged[A] = Writer[List[String], A]
//
//  def parseNumber(str: String): Logged[Option[Int]] =
//    util.Try(str.toInt).toOption match {
//      case Some(num) => Writer(List(s"Read $str"), Some(num))
//      case None      => Writer(List(s"Failed on $str"), None)
//    }
//
//  // モナド変換子を使用する型はモナドでないといけない
//  // たとえば、OptionT.flatMapで内側のAを取るにはモナドの結合則を満たしている必要がある
//  def addAll(a: String, b: String, c: String): Logged[Option[Int]] = {
//    import cats.data.OptionT
//
//    val result: OptionT[Logged, Int] = for {
//      // Logged[Option[A]]のように型がネストしていても、OptionTに変換してflatMapすればAが取れる
//      // つまり、flatMapを2回書く必要がなくなる
//      a: Int <- OptionT(parseNumber(a))
//      b <- OptionT(parseNumber(b))
//      c <- OptionT(parseNumber(c))
//    } yield a + b + c
//
//    // モナド変換子を呼び出し元が使えない場合があるので、valueでアンパックしておく
//    result.value
//  }
//
//  def main(args: Array[String]): Unit = {
//    val result1 = addAll("1", "2", "3")
//    val result2 = addAll("1", "a", "3")
//
//    println(result1) // WriterT((List(Read 1, Read 2, Read 3),Some(6)))
//    println(result2) // WriterT((List(Read 1, Failed on a),None))
//  }

  // 5.4 Exercise: Monads: Transform and Roll Out

  // 5.5 Summary
  /**
  * モナド変換子を使うことで、ネストされた（モナドである）型を扱うときにflatMapのネストを書く必要がなくなる
  */
}
