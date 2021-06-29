package sandbox

object Chapter11 {
  // 11 Case Study: CRDTs
  /**
   * このケーススタディでは、最終的に一貫性のあるデータを使用できるデータ構造の1つであるCommutative Replicated Data Types (CRDTs)について説明します
   * ⇒ CRDTはコンフリクトしない複製可能なデータ
   *
   * まず、最終的に一貫性のあるシステムの有用性と難しさを説明し、次にモノイドとその拡張機能を使って問題を解決できるか示します
   * 最後に、その解決策をScalaでモデル化します
   *
   * ここでの我々の目標は、特定の型のCRDTのScalaの実装に焦点を合てることです
   * すべてのCRDTの包括的な調査を目的としているわけではありません
   * CRDTは急速に発展している分野であり、より多くのことを学ぶためには文献を読むことをお勧めします
   */

  // 11.1 Eventual Consistency
  /**
   * システムの規模が1台のマシンを超えると、データの管理方法について選択しなければなりません
   *
   * 1つのアプローチは、すべてのマシンが同じデータビューを持つ、一貫性のあるシステムを構築することです
   * たとえば、ユーザーがパスワードを変更した場合、そのパスワードのコピーを保存しているすべてのマシンがその変更を受け入れなければ、正常に完了しないことです
   *
   * 一貫性のあるシステムは作業しやすいですが、デメリットもあります
   * 1回の変更で多くのメッセージがマシン間で送信される可能性があるため、待ち時間が長くなる傾向があります
   * また、障害が発生するとマシン間の通信が遮断され、ネットワークが分断されてしまうため、稼働率が比較的低い傾向にあります
   * ネットワークの分断がある場合、一貫性のあるシステムはマシン間の不整合を防ぐために、さらなるアップデートを拒否することがあります
   *
   * 別のアプローチは、最終的に一貫性のあるシステムです
   * これは特定の時点で、マシンが異なるデータビューを持つことを許可することを意味します
   * ただし、すべてのマシンが通信でき、それ以上のアップデートがない場合、最終的にはすべてのマシンが同じデータビューを持つことになります
   *
   * 最終的に一貫性のあるシステムでは、マシン間の通信が少なくて済むため、待ち時間を短縮できます
   * パーティション化されたマシンは、ネットワークが修正された時にアップデートを受け入れ、そのアップデートを調整できるため、システムの稼働時間も改善されます
   *
   * 大きな問題はマシン間でこの調整をどのように行うかということです
   * CRDTsは、この問題に対する1つのアプローチを提供します
   */

  // 11.2 The GCounter
  /**
   * 1つの特定のCRDTの実装を見てみましょう
   * 次にプロパティを一般化して、一般的なパターンを見つけることができるか確認します
   *
   * ここで説明するデータ構造は、GCounterと呼ばれます
   * それはインクリメントのみの分散カウンターで、たとえば、多くのWebサーバーでリクエストを処理するWebサイトへの訪問者数をカウントするために使用できます
   */

  // 11.2.1 Simple Counters
  /**
   * 単純なカウンターが機能しない理由を理解するために、単純な訪問者数を格納する2つのサーバーがあると想像してください
   * それぞれマシンAとマシンBと呼びましょう
   * 各マシンは整数カウンターを格納しており、カウンターはすべてゼロから始まります
   *
   * マシンA: 0
   * マシンB: 0
   *
   * ここで、Webトラフィックを受信するとします
   * ロードバランサーは、5つのリクエストをAとBに分散し、Aは3人の訪問者にサービスを提供し、Bは2人の訪問者にサービスを提供します
   *
   * マシンA: 3
   * マシンB: 2
   *
   * マシンのシステムの状態が一貫性がないため、一貫性を保つために調整する必要があります
   * 単純なカウンターを使用した調整方法としては、カウンターを交換して追加することです
   *
   * マシンA: 3 + 2(マシンBのカウンター)
   * マシンB: 2 + 3(マシンAのカウンター)
   * ⇒
   * マシンA: 5
   * マシンB: 5
   *
   * これまでは良いのですが、まもなく事態は崩壊していきます
   * Aが1人の訪問者にサービスを提供するとします
   * これは、合計6人の訪問者が見たことを意味します
   *
   * マシンA: 6
   * マシンB: 5
   *
   * マシンは、加算を使って再度調整を試み、以下に示す答えを導き出します
   *
   * マシンA: 6 + 5(マシンBのカウンター)
   * マシンB: 5 + 6(マシンAのカウンター)
   * ⇒
   * マシンA: 11
   * マシンB: 11
   *
   * これは明らかに間違っています！
   * 問題は、単純なカウンターでは、マシン間の相互作用の履歴に関する十分な情報が得られないことです
   * 幸いなことに、正しい答えを得るために完全な履歴を保存する必要はありません（その要約だけで十分です）
   * GCounterがこの問題をどのように解決するかを見てみましょう
   */

  // 11.2.2 GCounters
  /**
   * GCounterの最初の賢いアイデアは、各マシンに自分が知っているすべてのマシン（自分自身を含む）のカウンターを格納することです
   * 前の例では、AとBの2台のマシンがありました
   * 以下に示すように両方のマシンにAのカウンターとBのカウンターが格納されます
   *
   * マシンA: { A: 0, B: 0 }
   * マシンB: { A: 0, B: 0 }
   *
   * GCounterのルールは、マシンが自分のカウンターをインクリメントすることだけが許されるということです
   * Aが3人の訪問者にサービスを提供し、Bが2人の訪問者にサービスを提供する場合、カウンターは以下のようになります
   *
   * マシンA: { A: 3, B: 0 }
   * マシンB: { A: 0, B: 2 }
   *
   * 2台のマシンがカウンターを調整する場合、ルールは各マシンに格納されている最大値を取得することです
   * 今回の例では、最初のマージの結果は以下のようになります
   *
   * マシンA: { A: 3, B: 0 } ⇒ マシンBのBを取得
   * マシンB: { A: 0, B: 2 } ⇒ マシンAのAを取得
   * ⇒
   * マシンA: { A: 3, B: 2 }
   * マシンB: { A: 3, B: 2 }
   *
   * その後のWebリクエストはincrement-own-counterルールで処理され、マージはtake-maximum-valueルールで処理され、以下のように各マシンに正しい値が設定される
   *
   * マシンA: { A: 3, B: 2 } + マシンAに1リクエスト
   * マシンB: { A: 3, B: 2 }
   * ⇒
   * マシンA: { A: 4, B: 2 } ⇒ 自分（A）のカウンターのみインクリメント（increment-own-counterルール）
   * マシンB: { A: 3, B: 2 }
   * ⇒
   * マシンA: { A: 4, B: 2 } ⇒ 自分が最大値を保持しているのでマージ不要
   * マシンB: { A: 4, B: 2 } ⇒ マシンAの最大値は4なのでマージ（take-maximum-valueルール）
   *
   * GCounterを使用すると、各マシンは相互作用の完全な履歴を保存することなく、システム全体の状態を正確に把握できます
   * マシンがWebサイト全体の合計トラフィックを計算する場合、マシンごとのすべてのカウンターを合計します
   * 結果は直近で実行された調整に応じて、正確またはそれに近いものになります（すべてのマシンが同じ値とは限らない）
   * 最終的には、ネットワークが停止しても、システムは常に一貫した状態に収束します
   * （もし、履歴で状態を管理していた場合は、途中ネットワークが遮断されて履歴が失われると一貫した状態にはならない）
   */

  // 11.2.3 Exercise: GCounter Implementation
  /**
   * 以下のインターフェイスでGCounterを実装することができます
   * ここでは、マシンIDをStringとして表現しています
   *
   * final case class GCounter(counters: Map[String, Int]) {
   *   def increment(machine: String, amount: Int) = ???
   *   def merge(that: GCounter): GCounter = ???
   *   def total: Int = ???
   * }
   *
   * 実装を完了してください！
   */
  // 答え見た
//  final case class GCounter(counters: Map[String, Int]) {
//    def increment(machine: String, amount: Int) = {
//      // 自分のマシンのカウンターを取得（無ければ0）し、リクエスト数（amount）を加算
//      val value = amount + counters.getOrElse(machine, 0)
//      GCounter(counters + (machine -> value))
//    }
//
//    // 別のマシン（that）のGCounterを引数に受け取る
//    def merge(that: GCounter): GCounter = {
//      // 別のマシン（that）と自分のマシン（this）を結合
//      // that.countersを++で結合しているのは、thatがCを持っているパターンを考慮するため
//      GCounter(that.counters ++ this.counters.map { case (k, v) =>
//        // 各マシンのvalueの最大値を取得し、valueを更新する
//        k -> (v.max(that.counters.getOrElse(k, 0)))
//      })
//    }
//
//    // 各マシンのvalueを合計
//    def total: Int = counters.values.sum
//  }

  // 11.3 Generalisation
  /**
   * これで分散型の最終的に一貫性のあるインクリメント専用カウンターが作成されました
   * これは有用な成果ですが、ここで止まりたくありません
   * このセクションではGCounterの操作を抽象化して、自然数だけでなく、より多くのデータ型で機能するようにします
   *
   * GCounterは、自然数に対して以下の演算を使用します
   * ・追加（incrementとtotal内で）
   * ・最大（merge内で）
   * ・単位元0（incrementとmerge内で）
   *
   * どこかでモノイドが入ることは想像がつくと思いますが、依存しているプロパティを詳しく見てみましょう
   *
   * 復習として、第2章でモノイドは2つの法則を満たさなければならないことを確認しました
   * ・`+`二項演算は結合法則でなければなりません
   *  (a + b) + c == a + (b + c)
   * ・単位元はempty要素でなければなりません
   *  0 + a == a + 0 == a
   *
   * カウンターを初期化するには、incrementに単位元が必要です
   * また、mergeの特定のシーケンスが正しい値を与えることを保証するために、結合性に依存しています
   *
   * totalでは、結合性と可換性に暗黙的に依存しており、マシンごとのカウンターをどのような順序で合計しても、正しい値が得られるようになっています
   * （可換性：演算や操作の順序を入れ換えても結果が同じになる性質）
   * また、暗黙的に単位元を想定できるため、カウンターを格納していないマシンをスキップできます
   * （単位元がemptyという前提であればtotalに影響しないので、カウンターされていないマシンは無視できるみたいな）
   *
   * mergeのプロパティはもう少し興味深いものです
   * マシンAがマシンBをマージするのと、マシンBがマシンAをマージするのは同じ結果が得られるので、可換性に依存しています
   * 3台以上のマシンのデータをマージする時に正しい結果を得られるようにするには、結合性が必要です
   * 空のカウンターを初期化するには、単位元が必要です
   * 最後に、2台のマシンがマシンごとのカウンターに同じデータを保持している場合、データをマージしても誤った結果にならないために、idempotency（べき等性）というプロパティが必要です
   * べき等性演算は、複数回実行されても毎回同じ結果を返す演算です
   * 正式には、以下の関係が成り立つ場合、二項演算maxはべき等です
   * a max a = a
   *
   * よりコンパクトに書くと、以下のようになります
   * increment: 単位元、結合性
   * merge: 単位元、可換性、結合性、べき等性
   * total: 単位元、可換性、結合性
   *
   * これから以下のことが分かります
   * ・incrementは、モノイドが必要です
   * ・totalは、可換モノイドが必要です
   * ・mergeは、有界半束とも呼ばれるべき等可換モノイドが必要です
   *
   * incrementとgetの両方で同じ2項演算（加算）が使用されるため、両方に同じ可換モノイドを必要とするのが普通です
   * （getはまだ出てない）
   *
   * この調査は、プロパティまたは抽象化の法則について考える力を示しています
   * これらのプロパティを確認したので、GCounterで使用される自然数をこれらのプロパティを満たす演算を持つ任意のデータ型で置き換えることができます
   * 簡単な例は、二項演算が和集合（Union）であり、単位元が空のSetであるSetです
   * IntをSet[A]に置き換えるだけで、GSet型を作成できます
   */

  // 11.3.1 Implementation
  /**
   * この一般化をコードで実装しましょう
   * incrementとtotalには可換モノイドが必要であり、mergeには有界半束（べき等可換モノイド）が必要であることを忘れないでください
   *
   * Catsは、MonoidとCommutativeMonoidを提供しますが、有界半束の型クラスは提供しません
   * （※14: Spireと呼ばれる密接に関連したライブラリがすでにその抽象化を提供しています）
   * そのため、独自のBoundedSemiLattice型クラスを実装します
   */
  //  import cats.kernel.CommutativeMonoid
  //
  //  trait BoundedSemiLattice[A] extends CommutativeMonoid[A] {
  //    def combine(a1: A, a2: A): A
  //    def empty: A
  //  }

  /**
   * 上記の実装では、BoundedSemiLattice[A]はCommutativeMonoid[A]を拡張します
   * これは、有界半束が可換モノイド（正確にはべき等可換モノイド）であるためです
   */

  // 11.3.2 Exercise: BoundedSemiLattice Instances
  /**
   * IntとSetのBoundedSemiLattice型クラスインスタンスを実装します
   * Intのインスタンスは、技術的には非負の数に対してのみ成立しますが、型の中でそれを明示的にモデル化する必要はありません
   */
  // 答え見た
  //  import cats.kernel.CommutativeMonoid
  //
  //  trait BoundedSemiLattice[A] extends CommutativeMonoid[A] {
  //    def combine(a1: A, a2: A): A
  //    def empty: A
  //  }
  //
  //  // 各インスタンスは、コンパニオンオブジェクトに配置するのが一般的
  //  object BoundedSemiLattice {
  //    implicit val intInstance: BoundedSemiLattice[Int] =
  //      new BoundedSemiLattice[Int] {
  //        def combine(a1: Int, a2: Int): Int = a1.max(a2)
  //        val empty: Int = 0
  //      }
  //
  //    implicit def setInstance[A]: BoundedSemiLattice[Set[A]] =
  //      new BoundedSemiLattice[Set[A]] {
  //        def combine(a1: Set[A], a2: Set[A]): Set[A] = a1.union(a2)
  //        val empty: Set[A] = Set.empty[A]
  //      }
  //  }

  // 11.3.3 Exercise: Generic GCounter
  /**
   * CommutativeMonoidとBoundedSemiLatticeを使用して、GCounterを一般化します
   *
   * これを実装する時は、Monoidの関数や構文を使用して実装を簡素化する機会を探ってください
   * これは、型クラスの抽象化がコードの複数のレベルでどのように機能するかを示す良い例です
   * モノイドを使用して大きなコンポーネント（CRDTs）を設計していますが、モノイドは小さなコンポーネントでも役に立ち、コードを簡素化し、短く明確にします
   */
  // 答え見た
//  import cats.kernel.CommutativeMonoid
//  import cats.instances.list._
//  import cats.instances.map._
//  import cats.syntax.semigroup._
//  import cats.syntax.foldable._
//
//  trait BoundedSemiLattice[A] extends CommutativeMonoid[A] {
//    def combine(a1: A, a2: A): A
//    def empty: A
//  }
//
//  object BoundedSemiLattice {
//    implicit val intInstance: BoundedSemiLattice[Int] =
//      new BoundedSemiLattice[Int] {
//        def combine(a1: Int, a2: Int): Int = a1.max(a2)
//        val empty: Int = 0
//      }
//
//    implicit def setInstance[A]: BoundedSemiLattice[Set[A]] =
//      new BoundedSemiLattice[Set[A]] {
//        def combine(a1: Set[A], a2: Set[A]): Set[A] = a1.union(a2)
//        val empty: Set[A] = Set.empty[A]
//      }
//  }
//
//  // ここからが答え
//  // 型がIntからAに抽象化された
//  // BoundedSemiLatticeコンパニオンオブジェクトで具体的な型でインスタンス化しておけば、GCounterの各関数を汎用的に使える
//  final case class GCounter[A](counters: Map[String, A]) {
//    def increment(machine: String, amount: A)(implicit m: CommutativeMonoid[A]): GCounter[A] = {
//      val value = amount |+| counters.getOrElse(machine, m.empty)
//      GCounter(counters + (machine -> value))
//    }
//
//    // 上のBoundedSemiLatticeをimplicit
//    def merge(that: GCounter[A])(implicit b: BoundedSemiLattice[A]): GCounter[A] =
//      GCounter(this.counters |+| that.counters)
//
//    def total(implicit m: CommutativeMonoid[A]): A =
//      this.counters.values.toList.combineAll
//  }

  // 11.4 Abstracting GCounter to a Type Class
  /**
   * BoundedSemiLatticeとCommutativeMonoidのインスタンスを持つ任意の値で機能する汎用GCounterを作成しました
   * ただし、マシンIDから値へのMap特有の表現に縛られています
   * このような制限を設ける必要はありませんし、逆に制限を設けない方が便利な場合もあります
   * 単純なMapからリレーショナルデータベースまで、扱いたいkey-valueストアはたくさんあります
   *
   * GCounter型クラスを定義すると、様々な具体的な実装を抽象化できます
   * これにより、たとえば、トレードオフ関係にあるパフォーマンスと耐久性を変更したい場合に、メモリのストアを永続のストアにシームレスに置き換えることができます
   *
   * これを実装する方法はいくつかあります
   * 1つのアプローチは、CommutativeMonoidとBoundedSemiLatticeに依存するGCounter型クラスを定義することです
   * これをMap抽象化のキー型と値型を表す2つの型パラメーター（K,V）を持つ型コンストラクターを取る型クラスとして定義します
   */
  //  // Map[String, A]をF[K, V]に抽象化
  //  trait GCounter[F[_, _], K, V] {
  //    def increment(f: F[K, V])(k: K, v: V)(implicit m: CommutativeMonoid[V]): F[K, V]
  //    def merge(f1: F[K, V], f2: F[K, V])(implicit b: BoundedSemiLattice[V]): F[K, V]
  //    def total(f: F[K, V])(implicit m: CommutativeMonoid[V]): V
  //  }
  //
  //  object GCounter {
  //    def apply[F[_, _], K, V](implicit counter: GCounter[F, K, V]) =
  //      counter
  //  }

  /**
   * Mapのこの型クラスのインスタンスを定義してみてください
   * いくつかの小さい変更を加えるだけで、GCounterのケースクラスのコードを再利用できるはずです
   */
  // 答え見た
  //  import cats.kernel.CommutativeMonoid
  //  import cats.instances.list._
  //  import cats.instances.map._
  //  import cats.syntax.semigroup._
  //  import cats.syntax.foldable._
  //
  //  trait BoundedSemiLattice[A] extends CommutativeMonoid[A] {
  //    def combine(a1: A, a2: A): A
  //    def empty: A
  //  }
  //
  //  object BoundedSemiLattice {
  //    implicit val intInstance: BoundedSemiLattice[Int] =
  //      new BoundedSemiLattice[Int] {
  //        def combine(a1: Int, a2: Int): Int = a1.max(a2)
  //        val empty: Int = 0
  //      }
  //
  //    implicit def setInstance[A]: BoundedSemiLattice[Set[A]] =
  //      new BoundedSemiLattice[Set[A]] {
  //        def combine(a1: Set[A], a2: Set[A]): Set[A] = a1.union(a2)
  //        val empty: Set[A] = Set.empty[A]
  //      }
  //  }
  //
  //  trait GCounter[F[_, _], K, V] {
  //    def increment(f: F[K, V])(k: K, v: V)(implicit m: CommutativeMonoid[V]): F[K, V]
  //    def merge(f1: F[K, V], f2: F[K, V])(implicit b: BoundedSemiLattice[V]): F[K, V]
  //    def total(f: F[K, V])(implicit m: CommutativeMonoid[V]): V
  //  }
  //
  //  object GCounter {
  //    def apply[F[_, _], K, V](implicit counter: GCounter[F, K, V]) =
  //      counter
  //
  //    // ここからが答え
  //    // F[K, V]をMap[K, V]に具体化（インスタンス化）
  //    // GCounterのコンパニオンオブジェクト内でimplicitで定義する
  //    implicit def mapGCounterInstance[K, V]: GCounter[Map, K, V] =
  //      new GCounter[Map, K, V] {
  //        def increment(map: Map[K, V])(key: K, value: V)(implicit m: CommutativeMonoid[V]): Map[K, V] = {
  //          val total = map.getOrElse(key, m.empty) |+| value
  //          map + (key -> total)
  //        }
  //
  //        def merge(map1: Map[K, V], map2: Map[K, V])(implicit b: BoundedSemiLattice[V]): Map[K, V] =
  //          map1 |+| map2
  //
  //        def total(map: Map[K, V])(implicit m: CommutativeMonoid[V]): V =
  //          map.values.toList.combineAll
  //      }
  //  }
  //
  //  def main(args: Array[String]): Unit = {
  //    import cats.instances.int._
  //
  //    val g1 = Map("a" -> 7, "b" -> 3)
  //    val g2 = Map("a" -> 2, "b" -> 5)
  //
  //    val counter = GCounter[Map, String, Int]
  //
  //    val merged = counter.merge(g1, g2)
  //    val total = counter.total(merged)
  //
  //    println(merged) // Map(a -> 7, b -> 5)
  //    println(total) // 12
  //  }

  /**
   * 型クラスのインスタンスの実装方法が少し不満が残ります
   * 実装の構造は定義するほとんどのインスタンスで同じですが、コードを再利用することはできません
   * （たとえば、mapGCounterInstanceのincrementとかMap専用なのでもう少し一般化したい（MapをFにしたい））
   */

  // 11.5 Abstracting a Key Value Store
  /**
   * 1つの解決策は、型クラス内のkey-valueストアの思想をキャプチャし、KeyValueStoreインスタンスを持つ任意の型のGCounterインスタンスを生成することです
   * このような型クラスのコードは以下です
   */
  //  trait KeyValueStore[F[_, _]] {
  //    def put[K, V](f: F[K, V])(k: K, v: V): F[K, V]
  //
  //    def get[K, V](f: F[K, V])(k: K): Option[V]
  //
  //    def getOrElse[K, V](f: F[K, V])(k: K, default: V): V = get(f)(k).getOrElse(default)
  //
  //    def values[K, V](f: F[K, V]): List[V]
  //  }
  //
  //  // Mapの独自のインスタンスを実装します
  //  // 答え見た
  //  object KeyValueStore {
  //    implicit val mapKeyValueStoreInstance: KeyValueStore[Map] =
  //      new KeyValueStore[Map] {
  //        def put[K, V](f: Map[K, V])(k: K, v: V): Map[K, V] = f + (k -> v)
  //
  //        def get[K, V](f: Map[K, V])(k: K): Option[V] = f.get(k)
  //
  //        override def getOrElse[K, V](f: Map[K, V])(k: K, default: V): V =
  //          f.getOrElse(k, default)
  //
  //        def values[K, V](f: Map[K, V]): List[V] = f.values.toList
  //      }
  //  }

  /**
   * 型クラスができたので、インスタンスを持つデータ型を拡張する構文を実装できます
   */
//  // KeyValueStoreを使って、各関数を実装
//  implicit class KvsOps[F[_, _], K, V](f: F[K, V]) {
//    def put(key: K, value: V)(implicit kvs: KeyValueStore[F]): F[K, V] =
//      kvs.put(f)(key, value)
//
//    def get(key: K)(implicit kvs: KeyValueStore[F]): Option[V] =
//      kvs.get(f)(key)
//
//    def getOrElse(key: K, default: V)(implicit kvs: KeyValueStore[F]): V =
//      kvs.getOrElse(f)(key, default)
//
//    def values(implicit kvs: KeyValueStore[F]): List[V] =
//      kvs.values(f)
//  }

  /**
   * これでimplicit defを使って、KeyValueStoreとCommutativeMonoidのインスタンスを持つ任意のデータ型のGCounterインスタンスを生成できます
   */
  // （Map専用ではない）汎用的なgcounterInstanceを定義している
  // 上のmapKeyValueStoreInstanceでMap専用のインスタンス（putとかgetの）の実装をimplicitで定義
  // そのスコープ内でgcounterInstanceを呼べば、Mapのincrement、merge、totalになる
  // 〇〇専用インスタンスを用意すれば、そのスコープ内でgcounterInstanceが汎用的に使える
//  implicit def gcounterInstance[F[_, _], K, V](implicit kvs: KeyValueStore[F], km: CommutativeMonoid[F[K, V]]) =
//    new GCounter[F, K, V] {
//      def increment(f: F[K, V])(key: K, value: V)(implicit m: CommutativeMonoid[V]): F[K, V] = {
//        val total = f.getOrElse(key, m.empty) |+| value
//        f.put(key, total)
//      }
//
//      def merge(f1: F[K, V], f2: F[K, V])(implicit b: BoundedSemiLattice[V]): F[K, V] =
//        f1 |+| f2
//
//      def total(f: F[K, V])(implicit m: CommutativeMonoid[V]): V =
//        f.values.combineAll
//    }

  /**
   * このケーススタディの完全なコードは非常に長いですが、そのほとんどは型クラスに対する操作の構文を設定するボイラープレートです
   * SimulacrumやKind Projectorなどのコンパイラープラグインを使用して、これを削減できます
   */

  // 11.6 Summary
  /**
   * このケーススタディでは、型クラスを使用して、Scalaで単純なCRDTであるGCounterをモデル化する方法を説明しました
   * 今回の実装では、多くの柔軟性とコードの再利用が可能になりました
   * 「カウント」するデータ型にも、マシンIDをカウンターにマッピングするデータ型にも縛られません
   *
   * このケーススタディでは、CRDTsの調査ではなく、Scalaが提供するツールの使用に焦点を当てています
   * 他にも多くのCRDTsがあり、その中にはGCounterと同じように動作するものもあれば、実装が大きく異なるものもあります
   * かなり最近の調査では、基本的なCRDTsの多くの概要が分かります
   * ただし、これは活発な研究分野であり、CRDTsと最終的に一貫性に関心がある場合は、この分野の最近の出版物を読むことをお勧めします
   */
}
