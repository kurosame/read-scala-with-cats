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
}
