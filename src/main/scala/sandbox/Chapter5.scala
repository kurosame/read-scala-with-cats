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

}
