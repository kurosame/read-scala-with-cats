package sandbox

object Chapter8 {
  // 8 Case Study: Testing Asynchronous Code
  /**
   * 非同期コードを同期化することで単体テストを簡素化する
   *
   * 7章のサーバーの稼働時間測定の例に戻る
   * 2つのコンポーネントを定義する
   * 1つ目はリモートサーバーの稼働時間をポーリングするUptimeClientである
   */
//  import scala.concurrent.Future
//
//  trait UptimeClient {
//    def getUptime(hostname: String): Future[Int]
//  }

  /**
   * 2つ目はサーバーのリストを維持し、ユーザーがサーバーの合計稼働時間をポーリングするUptimeServiceである
   */
//  import cats.instances.future._
//  import cats.instances.list._
//  import cats.syntax.traverse._
//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  class UptimeService(client: UptimeClient) {
//    def getTotalUptime(hostnames: List[String]): Future[Int] =
//      hostnames.traverse(client.getUptime).map(_.sum)
//  }

  /**
   * 単体テストでUptimeClientをスタブ化したいので、UptimeClientをモック化した
   * これは実際のサーバーを呼び出さず、ダミーデータを返す
   */
//  class TestUptimeClient(hosts: Map[String, Int]) extends UptimeClient {
//    def getUptime(hostname: String): Future[Int] =
//      Future.successful(hosts.getOrElse(hostname, 0))
//  }

  /**
   * UptimeServiceの単体テストを作成しているとする
   * 値を合計する性能のテストを行う
   */
//  def testTotalUptime() = {
//    val hosts = Map("host1" -> 10, "host2" -> 6)
//    val client = new TestUptimeClient(hosts)
//    val service = new UptimeService(client)
//    val actual = service.getTotalUptime(hosts.keys.toList)
//    val expected = hosts.values.sum
//    assert(actual == expected)
//  }

  /**
   * UptimeServiceが非同期であるため、上記はコンパイルできない
   * actualはFuture[Int]型でexpectedはInt型なので、これらを直接比較できない
   *
   * この問題を解決するには、いくつかの方法があるが、今回はテストコードを変更せずに機能するように修正する
   */

  // 8.1 Abstracting over Type Constructors
  /**
   * UptimeClientの以下の2つのバージョンを実装する必要がある
   * ・本番環境で使用する非同期バージョン
   * ・単体テストで使用する同期バージョン
   */
//  trait RealUptimeClient extends UptimeClient {
//    def getUptime(hostname: String): Future[Int]
//  }
//
//  trait TestUptimeClient extends UptimeClient {
//    def getUptime(hostname: String): Int
//  }

  /**
   * そして、UptimeClientのtrait関数にどのような戻り値の型を与える必要があるか
   * Future[Int]とIntを抽象化する必要がある
   */
//  trait UptimeClient {
//    def getUptime(hostname: String): ???
//  }

  /**
   * Intは保持するが、テストコードではFutureを破棄する
   * 4.3で説明したID型が使える
   *
   * type Id[A] = A
   *
   * Idを使用すると、UptimeClientの戻り値の型を抽象化できる
   * ・型コンストラクターF[_]をパラメーターとして受け取るUptimeClientのtrait定義を記述する
   * ・FをFutureとIdにそれぞれバインドする2つのtrait、RealUptimeClientとTestUptimeClientをextendする
   * ・getUptime関数のシグネチャーを記述し、コンパイルされることを確認する
   */
//  import cats.Id
//
//  trait UptimeClient[F[_]] {
//    def getUptime(hostname: String): F[Int]
//  }
//
//  trait RealUptimeClient extends UptimeClient[Future] {
//    def getUptime(hostname: String): Future[Int]
//  }
//
//  trait TestUptimeClient extends UptimeClient[Id] {
//    // Id[A]はAのエイリアスなので、単にIntと書くことができる
//    // def getUptime(hostname: String): Id[Int]
//    def getUptime(hostname: String): Int
//  }
//
//  // TestUptimeClientをMap[String, Int]を使った元の実装で具体化できる
//  class TestUptimeClient(hosts: Map[String, Int]) extends UptimeClient[Id] {
//    def getUptime(hostname: String): Int = hosts.getOrElse(hostname, 0)
//  }

  // 8.2 Abstracting over Monads
  /**
   * UptimeServiceを書き直す
   */
//  class UptimeService[F[_]](client: UptimeClient[F]) {
//    def getTotalUptime(hostnames: List[String]): F[Int] =
//      hostnames.traverse(client.getUptime).map(_.sum)
//  }

  /**
   * 上記はコンパイルエラーになる
   * traverseはApplicativeを持つ値のシーケンスのみで機能するからである
   * FにApplicativeがあることをコンパイラーに証明する必要がある
   */
//  import cats.Applicative
//  import cats.syntax.functor._
//
//  class UptimeService[F[_]: Applicative](client: UptimeClient[F]) {
//    def getTotalUptime(hostnames: List[String]): F[Int] =
//      hostnames.traverse(client.getUptime).map(_.sum)
//  }

  /**
   * これでテストコードが機能するようになった
   */
//  def testTotalUptime() = {
//    val hosts = Map("host1" -> 10, "host2" -> 6)
//    val client = new TestUptimeClient(hosts)
//    val service = new UptimeService(client)
//    val actual = service.getTotalUptime(hosts.keys.toList)
//    val expected = hosts.values.sum
//    assert(actual == expected)
//  }
//
//  testTotalUptime()

  // 8.3 Summary
}
