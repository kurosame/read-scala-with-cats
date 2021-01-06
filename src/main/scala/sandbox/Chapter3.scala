package chapter3

object Chapter3 {
  // 3 Functors
  // 3.1 Examples of Functors
  /**
    * ファンクターはmap関数を持つものである
    * map関数は、リスト内のすべての値を一度に変換するものと考える必要がある
    * 値は変更されるが、リストの構造（要素の数と順序）は同じである
    * 同様にOptionのmapは、SomeとNoneのコンテキストは変わらないが、中身を変換する
    * EitherのLeftとRightも同様
    *
    * mapはコンテキストを変更しないため、mapを繰り返し呼び出して、複数の計算をシーケンスできる
    * List(1, 2, 3).map(n => n + 1).map(n => n * 2).map(n => s"${n}!") // List("4!", "6!", "8!")
    */
  // 3.2 More Examples of Functors
  /**
    * Futureは、非同期計算をキューに入れ、非同期計算をシーケンスできるファンクターである
    * Futureが動いている時、内部の状態について保証はされない
    * Futureでラップされた計算は、進行中・完了・拒否のいずれかである可能性がある
    * Futureが完了すると、すぐにmap関数を呼び出せる
    * そうでない場合、スレッドプールが関数呼び出しをキューに入れ、後で実行される
    * 関数がいつ呼び出されるか分からないが、どのような順序で呼び出されるかは分かっている
    */
//  def main(args: Array[String]): Unit = {
//    import scala.concurrent.{Future, Await}
//    import scala.concurrent.ExecutionContext.Implicits.global
//    import scala.concurrent.duration._
//
//    val future: Future[String] =
//      Future(123).map(n => n + 1).map(n => n * 2).map(n => s"${n}!")
//
//    println(Await.result(future, 1.second)) // 248!
//  }

  /**
    * Futureは、参照透過性ではないため、純粋な関数型プログラミングとは言えない
    * Futureは、常に計算結果をキャッシュする機能がないので、副作用がある計算をラップすると、予測できない結果が得られる可能性がある
    */
  /**
    * Functions
    * 単一引数の関数Function1もファンクターである
    * Function1のマッピングは関数の合成である
    */
//  def main(args: Array[String]): Unit = {
//    import cats.instances.function._
//    import cats.syntax.functor._
//
//    val func1: Int => Double = (x: Int) => x.toDouble
//    val func2: Double => Double = (y: Double) => y * 2
//
//    println((func1 map func2)(1)) // 2.0
//    println((func1 andThen func2)(1)) // 2.0
//    println(func2(func1(1))) // 2.0
//  }

  /**
    * 関数の合成はシーケンスである
    * 単一の操作を実行する関数から始め、mapを使用するたびに、チェーンに別の操作を追加する
    * map自体はどの操作も実行できないが、すべての操作を順番に実行させることができる
    * Futureと同様の遅延キューイングと考えることができる
    */
//  def main(args: Array[String]): Unit = {
//    import cats.instances.function._
//    import cats.syntax.functor._
//
//    val func =
//      ((x: Int) => x.toDouble)
//        .map(x => x + 1)
//        .map(x => x * 2)
//        .map(x => s"${x}!")
//
//    println(func(123)) // 248.0!
//  }

  // 3.3 Definition of a Functor
  /**
    * Functorは、シーケンス計算をカプセル化するクラスである
    * Functorは、F[A]型であり、(A => B) => F[B]型のmap操作を持つ
    */
//  trait Functor[F[_]] {
//    def map[A, B](fa: F[A])(f: A => B): F[B]
//  }

  /**
    * Functorは以下の法則を持つ
    *
    * fa.map(a => a) == fa
    * aがaとなる関数（恒等関数）を使ってmapしても、それは何もしていないことと等しい
    *
    * fa.map(g(f(_))) == fa.map(f).map(g)
    * 「gとfを関数合成したものをmapする」と「fをmapして、その後gをmapする」の2つは等しい
    */
  // 3.4 Aside: Higher Kinds and Type Constructors
}
