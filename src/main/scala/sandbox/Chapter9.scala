package sandbox

object Chapter9 {
  // 9 Case Study: Map-Reduce
  /**
   * このケーススタディでは、Monoid,Functorを使って、シンプルで強力な並列処理フレームワークを実装する
   */

  // 9.1 Parallelizing map and fold
  /**
   * mapはシーケンス内の個々の要素を個別に変換する（F[A]に関数A => Bを適用し、F[B]にする）
   * よって、mapは並列化できる
   * reduce(fold)は分散すると順序を制御できなくなるので、減算のfoldなどは結果が変わる可能性がある
   *
   * reduce(a1, reduce(a2, a3)) == reduce(reduce(a1, a2), a3)
   *
   * ただし、上記のように結合性があれば、分散しても結果が変わることはない
   *
   * fold操作では型Bのseed(fold初期値のMonoid[B].empty)が必要
   * foldは任意の数の並列ステップに分散される可能性があるため、seedが計算結果に影響を与えることがあってはいけない
   * つまり、seedが単位元である必要がある
   *
   * reduce(seed, a1) == reduce(a1, seed) == a1
   *
   * 上記を要約すると、以下の場合でfoldが並列化でき、正しい結果が得られる
   * ・reduce関数は結合的である
   * ・seedが単位元
   * つまりこれは、Monoidである
   *
   * この章では、シンプルなmap-reduceを実装する
   * まずは、foldMap関数を実装する
   */

  // 9.2 Implementing foldMap
  /**
   * まずは、foldMapのシグネチャーを書く
   * foldMapは次のパラメーターを受け入れる必要がある
   * ・Vector[A]型のシーケンス
   * ・A => B型の関数、Bに対応するMonoidがある
   */
//  import cats.Monoid
//
//  def foldMap[A, B: Monoid](values: Vector[A])(func: A => B): B = ???

  /**
   * 次にfoldMapの本体を実装する
   * ① 型Aのシーケンスのアイテムから始める
   * ② mapを適用し、型Bのアイテムのシーケンスを生成する
   * ③ Monoidを使用して、アイテムを単一のBに減らす
   * ①[A, A, A] -> ②[B, B, B] -> ③B
   */
//  import cats.Monoid
//  import cats.syntax.semigroup._
//
//  def foldMap[A, B: Monoid](as: Vector[A])(func: A => B): B =
//    as.map(func).foldLeft(Monoid[B].empty)(_ |+| _)
//
//  // さらにコンパクトに書ける
//  def foldMap2[A, B: Monoid](as: Vector[A])(func: A => B): B =
//    as.foldLeft(Monoid[B].empty)(_ |+| func(_))
//
//  def main(args: Array[String]): Unit = {
//    import cats.instances.int._
//    println(foldMap(Vector(1, 2, 3))(identity)) // 6
//    println(foldMap2(Vector(1, 2, 3))(identity)) // 6
//
//    import cats.instances.string._
//    println(foldMap(Vector(1, 2, 3))(_.toString + "! ")) // 1! 2! 3!
//    println(foldMap2(Vector(1, 2, 3))(_.toString + "! ")) // 1! 2! 3!
//    println(foldMap("Hello world!".toVector)(_.toString.toUpperCase)) // HELLO WORLD!
//    println(foldMap2("Hello world!".toVector)(_.toString.toUpperCase)) // HELLO WORLD!
//  }

  // 9.3 Parallelising foldMap
  /**
   * ① 初期状態は型Aのリストから始まる
   * ② データを分割し、各CPUで実行するバッチにそのデータを割り当てる
   * ③ 並列処理でmapを実行する
   * ④ 並列処理でreduceを実行する（各バッチでローカルに結果を保持）
   * ⑤ 各バッチの結果を単一の最終結果にreduceする
   *
   * ① [A, A, A, A, A, A, A, A, A]
   * ② CPU1[A, A, A] CPU2[A, A, A] CPU3[A, A, A]
   * ③ CPU1[B, B, B] CPU2[B, B, B] CPU3[B, B, B]
   * ④ CPU1[B] CPU2[B] CPU3[B]
   * ⑤ B
   */

  /**
   * 次の9.3.1でFutureを使用して並列アルゴリズムを実装してみる
   */

  // 9.3.1 Futures, Thread Pools, and ExecutionContexts
  /**
   * Futureは、implicitのExecutionContextによって決定されるスレッドプールで実行される
   * Future.applyなどを使ってFutureを生成する時は常に、スコープ内にExecutionContextが必要
   */
//  import scala.concurrent.{Await, Future}
//  import scala.concurrent.duration._
//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  def main(args: Array[String]): Unit = {
//    val future1 = Future { (1 to 100).toList.foldLeft(0)(_ + _) }
//    val future2 = Future { (100 to 200).toList.foldLeft(0)(_ + _) }
//
//    println(Await.result(future1, 1.second)) // 5050
//    println(Await.result(future2, 1.second)) // 15150
//  }

  /**
   * 上記の例ではExecutionContext.Implicits.globalをインポートしている
   * これはマシンのCPUごとに1つのスレッドを持つスレッドプールが割り当てられる
   * Futureを作成すると、ExecutionContextはそれを実行するようにスケジュールする
   * プールに空きスレッドがある場合、Futureはすぐに実行を開始する
   * 最近のマシンのCPUはマルチコアなので、future1とfuture2は並列実行される可能性が高い
   *
   * mapやflatMapなどのコンビネーターは、前のFutureの結果に基づいて、次に実行される計算をスケジュールする
   */
//  import scala.concurrent.{Await, Future}
//  import scala.concurrent.duration._
//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  def main(args: Array[String]): Unit = {
//    val future1 = Future { (1 to 100).toList.foldLeft(0)(_ + _) }
//    val future2 = Future { (100 to 200).toList.foldLeft(0)(_ + _) }
//
//    val future3 = future1.map(_.toString)
//    val future4 = for {
//      a <- future1
//      b <- future2
//    } yield a + b
//
//    println(Await.result(future3, 1.second)) // 5050
//    println(Await.result(future4, 1.second)) // 20200
//
//    // sequenceを使って、List[Future[A]]をFuture[List[A]]に変換できる
//    val future5 = Future.sequence(List(Future(1), Future(2), Future(3)))
//    println(future5) // Future(Success(List(1, 2, 3)))
//
//    // Traverseインスタンスのsequenceを使って、同じように変換できる
//    import cats.instances.future._
//    import cats.instances.list._
//    import cats.syntax.traverse._
//    val future6 = List(Future(1), Future(2), Future(3)).sequence
//    println(future6) // Future(Success(List(1, 2, 3)))
//
//    // cats.instances.futureから取得できるFutureのMonad,Monoid実装もある
//    import cats.{Monad, Monoid}
//    import cats.instances.int._
//    val future7 = Monad[Future].pure(42)
//    val future8 = Monoid[Future[Int]].combine(Future(1), Future(2))
//    println(future7) // Future(Success(42))
//    println(future8) // Future(Success(3))
//  }

  // 9.3.2 Dividing Work
  /**
   * 処理をバッチに分割する方法を見てみる
   *
   * 以下のコマンドを実行すると、利用可能なCPUコア数が分かる
   * Runtime.getRuntime.availableProcessors
   *
   * grouped関数を使用して、シーケンスを分割できる
   * これを使用して、CPUコアごとに処理を分割する
   */
//  def main(args: Array[String]): Unit = {
//    println((1 to 10).toList.grouped(3).toList)
//    // List(List(1, 2, 3), List(4, 5, 6), List(7, 8, 9), List(10))
//  }

  // 9.3.3 Implementing parallelFoldMap
  /**
   * foldMapの並列バージョンであるparallelFoldMapを実装する
   * シグネチャーは以下
   */
//  def parallelFoldMap[A, B: Monoid](values: Vector[A])(func: A => B): Future[B] = ???

  /**
   * CPUコアごとに処理をバッチに分割する
   * 各バッチを並列スレッドで処理する
   */
//  import cats.Monoid
//  import cats.instances.int._
//  import cats.syntax.semigroup._
//  import scala.concurrent.{Await, Future}
//  import scala.concurrent.duration._
//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  def foldMap[A, B: Monoid](as: Vector[A])(func: A => B): B =
//    as.map(func).foldLeft(Monoid[B].empty)(_ |+| _)
//
//  def parallelFoldMap[A, B: Monoid](values: Vector[A])(func: A => B): Future[B] = {
//    // 各CPUに渡すアイテムの数を計算する
//    val numCores = Runtime.getRuntime.availableProcessors
//    val groupSize = (1.0 * values.size / numCores).ceil.toInt
//
//    // grouped関数を使用して、シーケンスを分割
//    val groups: Iterator[Vector[A]] = values.grouped(groupSize)
//
//    // 各グループをfoldMapするFutureを作成
//    val futures: Iterator[Future[B]] =
//      groups.map { group =>
//        Future {
//          group.foldLeft(Monoid[B].empty)(_ |+| func(_))
//        }
//      }
//    // foldMapを使うとfuturesはさらに簡潔に書ける
//    // val futures: Iterator[Future[B]] =
//    //   groups.map(group => Future(foldMap(group)(func)))
//
//    // 最終結果を計算
//    Future.sequence(futures).map { iterable =>
//      iterable.foldLeft(Monoid[B].empty)(_ |+| _)
//    }
//  }
//
//  def main(args: Array[String]): Unit = {
//    val result: Future[Int] = parallelFoldMap((1 to 1000000).toVector)(identity)
//    println(Await.result(result, 1.second)) // 1784293664
//  }

  // 9.3.4 parallelFoldMap with more Cats
  /**
   * CatsのFoldableとTraverseableを使ってparallelFoldMapを再実装する
   */
//  import cats.Monoid
//  import cats.instances.int._
//  import cats.instances.future._
//  import cats.instances.vector._
//  import cats.syntax.foldable._
//  import cats.syntax.traverse._
//  import scala.concurrent._
//  import scala.concurrent.duration._
//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  def parallelFoldMap[A, B: Monoid](values: Vector[A])(func: A => B): Future[B] = {
//    val numCores = Runtime.getRuntime.availableProcessors
//    val groupSize = (1.0 * values.size / numCores).ceil.toInt
//
//    values
//      .grouped(groupSize)
//      .toVector
//      .traverse(group => Future(group.foldMap(func)))
//      .map(_.combineAll)
//  }
//
//  def main(args: Array[String]): Unit = {
//    val future: Future[Int] = parallelFoldMap((1 to 1000).toVector)(_ * 1000)
//    println(Await.result(future, 1.second)) // 500500000
//  }

  // 9.4 Summary
  /**
   * map-reduceのアルゴリズムを以下の3つのステップに従い、実装した
   * 1. 各CPUコア（ノード）の1つのバッチにデータを送信し、処理する
   * 2. 各バッチでmap-reduceを実行
   * 3. モノイド加算を使用して、結果を結合
   */
}
