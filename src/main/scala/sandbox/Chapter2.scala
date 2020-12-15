package sandbox

object Chapter2 {
  // 2 Monoids and Semigroups
  /**
    * このセクションでは、monoidとsemigroupについて説明します
    * これらにより、値の追加と結合が可能になります
    * Ints,Strings,Lists,Optionsなどのインスタンスがあります
    * いくつかの単純な型と操作を見て、得ることができる一般的な原則を確認しましょう
    */
  /**
    * 整数の加算
    * Intsの加算は「閉じた」二項演算です（ここの「閉じた」は拡張性がないという意味だと思う）
    * つまり、2つのIntsを加算すると、常に別のIntが生成されます
    * 2 + 1 == 3
    *
    * 任意の`Int a`に対して、`a + 0 == 0 + a == a`という性質を持つ単位元0もあります
    * 2 + 0 == 0 + 2 == 2
    *
    * 加算の他の性質もあります
    * たとえば、常に同じ結果が得られるので、要素を加算する順序は関係ありません
    * これは結合性と呼ばれる性質です
    * (1 + 2) + 3 == 1 + (2 + 3) == 6
    */
  /**
    * 整数の乗算
    * 0の代わりに1を単位元として使用する場合、加算と同じ性質が乗算にも適用されます
    * 1 * 3 == 3 * 1 == 3
    *
    * 加算と同様の結合性もあります
    * (1 * 2) * 3 == 1 * (2 * 3) == 6
    */
  /**
    * Stringとシーケンス連結
    * 二項演算子の文字列連結を使用して、Stringsを追加することもできます
    * "One" ++ "two" == "Onetwo"
    *
    * 単位元として、空文字列
    * "" ++ "Hello" == "Hello" ++ "" == "Hello"
    *
    * 繰り返しますが、連結は結合性です
    * ("One" ++ "Two") ++ "Three" == "One" ++ ("Two" ++ "Three") == "OneTwoThree"
    *
    * シーケンスを意識させるため通常の（String連結の）+の代わりに++を使用したことに注意してください
    * 二項演算子での連結と単位元として空のシーケンスを使用して、他の型（Listなど）のシーケンスでも同じことができます
    */
  // 2.1 Definition of a Monoid
  /**
    * 結合則の二項加算と単位元を持つ「加算」のシナリオを上記でいくつか見てきました
    * これがモノイドであることを知っても驚きはないでしょう
    * 正式には、型Aのモノイドは次の通りです
    * ・`(A, A) => A`にできるcombineという操作
    * ・型Aのempty要素
    *
    * この定義はScalaコードにうまく変換されます
    * Catsの定義を簡略化したものを次に示します
    */
//   trait Monoid[A] {
//     def combine(x: A, y: A): A
//     def empty: A
//   }

  /**
    * combineとemptyという操作を提供することに加えて、モノイドは形式上いくつかの法則に従わなければなりません
    * 型Aのすべての値x,y,zにおいて、combineは結合則であり、emptyは単位元である必要があります
    */
  // 結合則
//  def associativeLaw[A](x: A, y: A, z: A)(implicit m: Monoid[A]): Boolean = {
//    m.combine(x, m.combine(y, z)) ==
//      m.combine(m.combine(x, y), z)
//  }

  // 単位元
//  def identityLaw[A](x: A)(implicit m: Monoid[A]): Boolean = {
//    (m.combine(x, m.empty) == x) &&
//    (m.combine(m.empty, x) == x)
//  }

  /**
    * たとえば、整数の減算は結合則ではないため、モノイドではありません
    * (1 - 2) - 3 != 1 - (2 - 3)
    */
  /**
    * 実際には、独自のMonoidインスタンスを記述する時に、法則について考えるだけで済みます
    * 違法なインスタンスを作成すると、Catsの他の機能で使った時に、予測不可能な結果をもたらす可能性があるため、危険です
    * ほとんどの場合、Catsが提供するインスタンスを信頼して、ライブラリの作者が何をしているか知っていると仮定することができます
    * ⇒（Monoidの法則に従ってライブラリが作成されているという前提があれば、関数の中身を見なくてもある程度処理内容が予想できる的な）
    */
  // 2.2 Definition of a Semigroup
  /**
    * セミグループは、emptyがないモノイドの単なるcombineにすぎません
    * 多くのセミグループはモノイドでもありますが、emptyを定義できないデータ型もあります
    * たとえば、シーケンス連結と整数の加算はモノイドであることを確認しました
    * ただし、空ではないシーケンスと正の整数に制限すると、emptyを感覚的に定義することはできません
    * （単位元となる空のシーケンスと0が定義できないから）
    * CatsのNonEmptyListデータ型はSemigroupでは実装されているが、Monoidでは実装されていません
    *
    * 単純化されたCatsのMonoidの定義は次の通りです
    */
  // emptyが定義できるのはMonoid、emptyが定義できないのはSemigroup
  // Monoidより、Semigroupが使えるか先に見るのが望ましい
//  trait Semigroup[A] {
//    def combine(x: A, y: A): A
//  }
//
//  trait Monoid[A] extends Semigroup[A] {
//    def empty: A
//  }

  /**
    * 型クラスについて議論するとき、この種類の継承がよく見られます
    * それはモジュール性を提供し、動作を再利用できるようにします
    * もし、型AのMonoidを定義したら、タダでSemigroupを取得できます
    * 同様にもし、関数がSemigroup[B]型の引数を必要とする場合、代わりにMonoid[B]を渡すことができます
    */
  // 2.3 Exercise: The Truth About Monoids
  /**
    * モノイドの例をいくつか見てきましたが、まだまだたくさんあります
    * Booleanを考えます
    * この型にいくつのモノイドを定義できますか？
    * それぞれのモノイドに、combineとemptyを定義し、モノイド法則が成り立つことを確信してください
    * 出発点として、以下の定義を使用します
    */
//  trait Semigroup[A] {
//    def combine(x: A, y: A): A
//  }
//
//  trait Monoid[A] extends Semigroup[A] {
//    def empty: A
//  }
//
//  object Monoid {
//    def apply[A](implicit monoid: Monoid[A]) = monoid
//  }
//
//  val booleanOr = new Monoid[Boolean] {
//    def combine(x: Boolean, y: Boolean): Boolean = x || y
//    def empty: Boolean = false
//  }
//  val booleanAnd = new Monoid[Boolean] {
//    def combine(x: Boolean, y: Boolean): Boolean = x && y
//    def empty: Boolean = true
//  }

  // 2.4 Exercise: All Set for Monoids
  /**
    * セットにはどのようなモノイドとセミグループがあるでしょうか？
    */
  // 答え見た
  /** Monoid */
  // A型の型引数が必要なので、関数として定義する
  // unionは和集合なので、emptyが定義できる
//  implicit def setUnionMonoid[A]: Monoid[Set[A]] =
//    new Monoid[Set[A]] {
//      def combine(a: Set[A], b: Set[A]) = a union b
//      def empty = Set.empty[A]
//    }

  // setUnionMonoidのimplicit定義で任意の型のMonoid[Set[...]]が呼び出せる
//  val intSetMonoid = Monoid[Set[Int]]
//  val strSetMonoid = Monoid[Set[String]]
//
//  def main(args: Array[String]): Unit = {
//    println(intSetMonoid.combine(Set(1, 2), Set(2, 3))) // Set(1, 2, 3)
//    println(strSetMonoid.combine(Set("A", "B"), Set("B", "C"))) // Set(A, B, C)
//  }

  /** Semigroup */
  // intersectはセミグループを形成できるが、積集合なのでemptyのような単位元が定義できない
//  implicit def setIntersectionSemigroup[A]: Semigroup[Set[A]] =
//    new Semigroup[Set[A]] {
//      def combine(a: Set[A], b: Set[A]) = a intersect b
//    }
//
//  object Semigroup {
//    def apply[A](implicit semigroup: Semigroup[A]) = semigroup
//  }
//
//  val intSetMonoid2 = Semigroup[Set[Int]]
//  val strSetMonoid2 = Semigroup[Set[String]]
//
//  def main(args: Array[String]): Unit = {
//    println(intSetMonoid2.combine(Set(1, 2), Set(2, 3))) // Set(2)
//    println(strSetMonoid2.combine(Set("A", "B"), Set("B", "C"))) // Set(B)
//  }

  // 2.5 Monoids in Cats
  /**
    * ここまででモノイドとは何かを見てきましたが、Catsでの実装を見てみましょう
    * もう一度、実装の3つのポイント、すなわち、型クラス、インスタンス、インターフェイスを見ていきましょう
    */
  // 2.5.1 The Monoid Type Class
  /**
    * モノイド型クラスは、`cats.kernel.Monoid`であり、`cats.Monoid`としてエイリアスされています
    * Monoidは`cats.Semigroup`としてエイリアスされた`cats.kernel.Semigroup`を拡張しています
    * Catsを使用する場合、通常、catsパッケージから型クラスをインポートします
    */
//  import cats.Monoid
//  import cats.Semigroup

  /**
    * Cats Kernel?
    * Cats Kernelは、Catsのサブプロジェクトで、完全のCatsを必要としないライブラリ用の小さな型クラスのセットを提供しています
    * これらのコア型クラスは、`cats.kernel`で定義されていますが、すべてcatsパッケージにエイリアスされているため、
    * 違いを意識する必要はほとんどありません
    * 本書で扱うCats Kernel型クラスは、 Eq、Semigroup、Monoidです
    * 他のすべての型クラスは、Catsプロジェクトの一部であり、catsパッケージで直接定義されています
    *
    * Eq、Semigroup、Monoidとかは以下のみにある
    * https://typelevel.org/cats/api/cats/kernel/index.html
    *
    * Monad、Applicativeとかは以下のみにある
    * https://typelevel.org/cats/api/cats/index.html
    */
  // 2.5.2 Monoid Instances
  /**
    * Monoidはユーザーインターフェイスの標準のCatsパターンに従います
    * コンパニオンオブジェクトには、特定の型の型クラスインスタンスを返すapply関数があります
    * たとえば、Stringのモノイドインスタンスがほしいとき、スコープに正しいimplicitが含まれていれば、次のように記述できます
    */
//  import cats.Monoid
//  import cats.instances.string._
//
//  Monoid[String].combine("Hi ", "there") // "Hi there"
//  Monoid[String].empty // ""

  // 上記は以下と同様です
//  Monoid.apply[String].combine("Hi ", "there") // "Hi there"
//  Monoid.apply[String].empty // ""

  /**
    * ご存知の通り、MonoidはSemigroupを拡張しています
    * emptyが必要ない場合、次のように記述できます
    */
//  import cats.Semigroup
//
//  Semigroup[String].combine("Hi ", "there") // "Hi there"

  /**
    * Monoidの型クラスインスタンスは、Chapter1で説明した標準的な方法でcats.instancesの下に定義されています
    * たとえば、Intのインスタンスを取り込みたい場合は、cats.instances.intからインポートします
    */
//  import cats.Monoid
//  import cats.instances.int._
//
//  Monoid[Int].combine(32, 10) // 42

  /**
    * 同様にcats.instances.intとcats.instances.optionのインスタンスを使って、Monoid[Option[Int]]を組み立てることができます
    */
//  import cats.Monoid
//  import cats.instances.int._
//  import cats.instances.option._
//
//  val a = Option(22)
//  val b = Option(20)
//
//  Monoid[Option[Int]].combine(a, b) // Some(42)

  /**
    * 他の型を網羅したインポートは1.4.2を参照してください
    * 個別にインスタンスをインポートする正当な理由がない限り、すべてをインポートすることができます
    */
//  import cats._
//  import cats.implicits._

  // 2.5.3 Monoid Syntax
  /**
    * Catsは、`|+|`の形式でcombine関数の構文を提供します
    * combineはSemigroupで定義されているので、cats.syntax.semigroupからインポートしてアクセスします
    */
//  import cats.instances.string._
//  import cats.syntax.semigroup._
//
//  val stringResult = "Hi " |+| "there" |+| Monoid[String].empty // "Hi there"
//
//  import cats.instances.int._
//
//  val intResult = 1 |+| 2 |+| Monoid[Int].empty // 3

  // 2.5.4 Exercise: Adding All The Things
  /**
    * 最先端のSuperAdder v3.5a-32は、数字を足し算するための世界初の選択肢です
    * プログラムのメイン関数には、シグネチャー`def add(items: List[Int]): Int`があります
    * 悲劇的な事故でこのコードは削除された！
    * 関数を書き換えて、危機を乗り越えましょう！
    */
  // 問題の訳の意味が分からない
//  def add(items: List[Int]): Int = items.foldLeft(0)(_ + _)

  /**
    * よくやった！
    * SuperAdderのマーケットシェアは拡大し続けており、現在は機能追加に対する需要があります
    * 人々は今`List[Option[Int]]`を追加したいです
    * addを変更して、これを可能にします
    * SuperAdderのコードベースは最高品質なので、コードの重複がないことを確認してください
    */
  // 答え見た
//  import cats.Monoid
//  import cats.syntax.semigroup._
//
//  // implicitのMonoid[A]を受け入れるaddを定義
//  def add[A](items: List[A])(implicit monoid: Monoid[A]): A =
//    items.foldLeft(monoid.empty)(_ |+| _)
//
//  def main(args: Array[String]): Unit = {
//    import cats.instances.int._
//    println(add(List(1, 2, 3))) // 6
//    import cats.instances.option._
//    println(add(List(Some(1), None, Some(2), None, Some(3)))) // Some(6)
//    // 以下はList[Some[Int]]という型を持っていないためエラーになる（Monoid[Option[Int]]は使えない）
//    // println(add(List(Some(1), Some(2), Some(3)))) // error: No implicits found Monoid[Some[Int]]
//  }

  /**
    * SuperAdderはPOS（point-of-sale）マーケットに参入しています
    * 今度はOrdersを合計します
    *
    * このコードをすぐにリリースする必要があり、`add`の修正を行うことはできません
    * そのようにしてくれ！
    */
  // 答え見た
//  import cats.Monoid
//  case class Order(totalCost: Double, quantity: Double)
//
//  // 2つのOrderのtotalCostとquantityを加算するMonoidを定義する
//  implicit val monoid: Monoid[Order] = new Monoid[Order] {
//    def combine(o1: Order, o2: Order) =
//      Order(
//        o1.totalCost + o2.totalCost,
//        o1.quantity + o2.quantity
//      )
//
//    def empty = Order(0, 0)
//  }

  // 2.6 Applications of Monoids
  /**
    * モノイドとは何か（足し算や結合の概念の抽象化したもの）は分かりましたが、それはどこで役立つのでしょうか？
    * ここでは、モノイドが大きな役割を果たすいくつかのビッグアイデアを紹介します
    * これらについては、後述のケーススタディで詳しく説明します
    */
  // 2.6.1 Big Data
  /**
    * SparkやHadoopのようなビッグデータアプリケーションでは、データ分析を多くのマシンに分散し、
    * フォールトトレランスやスケーラビリティを実現しています
    * （フォールトトレランスとは、一部のマシンに障害が起きても、正常に処理を続行できること）
    * これは、各マシンがデータの一部を結果として返し、これらの結果を結合して最終的な結果を得る必要があることを意味します
    * ほとんどの場合、これはモノイドと見なすことができます
    *
    * ウェブサイトの総訪問者数を計算する場合、データの各部分でIntの計算をすることを意味します
    * 我々はモノイドインスタンスが加算であることが分かっているので、部分的な結果を結合するのが正しい方法です
    *
    * ウェブサイトのユニークの訪問者数を知りたい場合は、データの各部分にSet[User]を構築します
    * 我々はSetのモノイドインスタンスが和集合であることが分かっているので、部分的な結果を結合するのが正しい方法です
    *
    * サーバーログから99%と95%のレスポンスタイムを計算したい場合は、モノイドが存在するQTreeと呼ばれるデータ構造を使用できます
    *
    * 理解してもらえるとよいのですが
    * 大規模なデータセットで行いたい分析はほとんどすべてがモノイドであり、
    * このアイデアに基づいて表現力豊かで強力な分析システムを構築できます
    * これはまさにTwitterのAlgebirdとSummingbirdプロジェクトが行ったことです
    * （Algebirdは、Scala向けの抽象代数学ライブラリ）
    * （Summingbirdは、Scala向けのmap-reduceライブラリ）
    * このアイデアについては、map-reduceのケーススタディでさらに詳しく説明します
    */
  // 2.6.2 Distributed Systems
  /**
    * 分散システムでは、マシンごとにデータのビューが異なる場合があります
    * たとえば、あるマシンが他のマシンが受信していないアップデートを受信することがあります
    * 我々はこれらの異なるビューを一致させ、アップデートが来なくてもすべてのマシンが同じデータを持つようにします
    * これは「eventual consistency（結果整合性）」と呼ばれます
    *
    * データ型の特定のクラスがこの一致をサポートします
    * これらのデータ型は、「commutative replicated data types(CRDTs)（可換複製データ型）」と呼ばれます
    * （可換とは、演算や操作の順序を入れ換えても結果が同じになること）
    * 重要な操作は、2つのデータインスタンスをマージして、両方のインスタンスのすべての情報をキャプチャした結果を得ることです
    * この操作は、モノイドインスタンスを持つことに依存します
    * CRDTsのケーススタディでは、このアイデアについてさらに詳しく説明します
    */
  // 2.6.3 Monoids in the Small
  /**
    * 上記の2つの例は、モノイドがシステムアーキテクチャ全体に通知するケースです
    * モノイドが周りにあると、小さなコードフラグメント（コードの断片）を書きやすくなるケースも多くあります
    * この本のケーススタディには、多くの例があります
    */
  // 2.7 Summary
  /**
    * この章では大きな節目を迎えました
    * 最初の型クラスをファンシーな関数型プログラミング名でカバーしました
    * ・Semigroupは、追加または結合操作を表します
    * ・Monoidは、単位元または「zero」要素を追加することによって、Semigroupを拡張します
    * 型クラス自体、関心のある型のインスタンス、「|+|」操作によって与えられるセミグループ構文の3つをインポートすることで、
    * SemigroupsとMonoidsを使用できます
    */
//  import cats.Monoid
//  import cats.instances.string._
//  import cats.syntax.semigroup._
//
//  def main(args: Array[String]): Unit = {
//    println("Scala" |+| " with " |+| "Cats") // Scala with Cats
//  }

  /**
    * スコープ内に正しいインスタンスがあれば、何でもほしいものを追加することができます
    */
//  def main(args: Array[String]): Unit = {
//    import cats.syntax.semigroup._
//    import cats.instances.int._
//    import cats.instances.option._
//
//    println(Option(1) |+| Option(2)) // Some(3)
//
//    import cats.instances.map._
//    val map1 = Map("a" -> 1, "b" -> 2)
//    val map2 = Map("b" -> 3, "d" -> 4)
//
//    println(map1 |+| map2) // Map(b -> 5, d -> 4, a -> 1)
//
//    import cats.instances.string._
//    import cats.instances.tuple._
//    val tuple1 = ("hello", 123)
//    val tuple2 = ("world", 321)
//
//    println(tuple1 |+| tuple2) // (helloworld,444)
//  }

  /**
    * また、Monoidインスタンスがある任意の型で動作する汎用的なコードを書くこともできます
    */
//  import cats.Monoid
//  import cats.syntax.semigroup._
//  def addAll[A](values: List[A])(implicit monoid: Monoid[A]): A =
//    values.foldRight(monoid.empty)(_ |+| _)
//
//  def main(args: Array[String]): Unit = {
//    import cats.instances.int._
//    println(addAll(List(1, 2, 3))) // 6
//    import cats.instances.option._
//    println(addAll(List(None, Some(1), Some(2)))) // Some(3)
//  }

  /**
  * Monoidsは、Catsの登竜門です
  * これは理解しやすくて使いやすい
  * しかし、Catsが可能にする抽象化という点では、これらは氷山の一角に過ぎません
  * 次の章では、map関数で大切にされている型クラスの化身であるfunctorsについて見ていきます
  * そこから本当に楽しいことが始まるのです
  */
}
