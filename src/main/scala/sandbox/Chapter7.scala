package sandbox

object Chapter7 {
  // 7 Foldable and Traverse
  /**
   * この章では、コレクションの繰り返しをキャプチャする2つの型クラスについて説明します
   * ・Foldableは、おなじみのfoldLeftとfoldRight操作を抽象化します
   * ・Traverseは、Applicativeを使用して、foldingより少ない苦労で繰り返しする高レベルの抽象化です
   *
   * まず、Foldable式を見てから、foldingが複雑になり、Traverseが便利になるケースを考察します
   */
  // 7.1 Foldable
  /**
   * Foldable型クラスは、List,Vector,Streamなどのシーケンスで使われているfoldLeftとfoldRightをキャプチャします
   * Foldableを使うと、様々なシーケンス型で機能する汎用的なfoldを書くことができます
   * また、新しいシーケンスを考え出し、それをコードに差し込むこともできます
   * Foldableは、MonoidとEvalモナドの優れたユースケースを提供します
   */
  // 7.1.1 Folds and Folding
  /**
   * foldingの一般的なコンセプトの簡単な要約から始めましょう
   * アキュムレータ値とバイナリ関数を提供して、シーケンス内の各アイテムと組み合わせます
   */
//  def main(args: Array[String]): Unit = {
//    def show[A](list: List[A]): String =
//      list.foldLeft("nil")((accum, item) => s"$item then $accum")
//
//    // バイナリ関数は呼ばれない
//    println(show(Nil)) // nil
//
//    // accum:
//    // nil
//    // 1 then nil
//    // 2 then 1 then nil
//    // 3 then 2 then 1 then nil
//    println(show(List(1, 2, 3))) // 3 then 2 then 1 then nil
//  }

  /**
   * foldLeft関数はシーケンスで再帰的に機能します
   * バイナリ関数はアイテムごとに繰り返し呼び出され、各呼び出しの結果が次のアキュムレータになります
   * シーケンスの最後に到達すると、最終的なアキュムレータが最終結果になります
   */
  /**
   * 実行する操作によって、折り畳む順序が重要になる場合があります
   * このため、foldには2つの標準的なバリエーションがあります
   * ・foldLeft: 左から右に走査します
   * ・foldRight: 右から左に走査します
   * 図11に動きを示します
   */
  /**
   * 二項演算が結合法則である場合、foldLeftとfoldRightは同等です
   * (((0 + 1) + 2) + 3) == (1 + (2 + (3 + 0)))
   * たとえば、アキュムレータとして0を使用し、演算として加算を使用して、いずれかの方向に折り畳むことにより、List[Int]を合計できます
   */
//  def main(args: Array[String]): Unit = {
//    println(List(1, 2, 3).foldLeft(0)(_ + _)) // 6
//    println(List(1, 2, 3).foldRight(0)(_ + _)) // 6
//  }

  /**
   * 非結合法則の操作を提供する場合、評価の順序が異なります
   * たとえば、減算を使用して折り畳むと、動きが異なる結果が得られます
   */
//  def main(args: Array[String]): Unit = {
//    // (((0 - 1) - 2) - 3)
//    println(List(1, 2, 3).foldLeft(0)(_ - _)) // -6
//
//    // (1 - (2 - (3 - 0)))
//    println(List(1, 2, 3).foldRight(0)(_ - _)) // 2
//  }

  // 7.1.2 Exercise: Reflecting on Folds
  /**
   * 空のリストをアキュムレータ、::を二項演算子としてfoldLeftとfoldRightを使用してみてください
   * それぞれどのような結果が得られますか？
   */
//  def main(args: Array[String]): Unit = {
//    // 1 :: Nil -> 2 :: 1 :: Nil -> 3 :: 2 :: 1 :: Nil
//    println(List(1, 2, 3).foldLeft(List(): List[Int])((x, y) => y :: x)) // List(3, 2, 1)
//
//    // 3 :: Nil -> 2 :: 3 :: Nil -> 1 :: 2 :: 3 :: Nil
//    println(List(1, 2, 3).foldRight(List(): List[Int])(_ :: _)) // List(1, 2, 3)
//  }

  // 7.1.3 Exercise: Scaf-fold-ing Other Methods
  /**
   * foldLeftとfoldRightはとても一般的な関数です
   * それらを使用して、他の高レベルのシーケンス操作を実装できます
   * Listのmap,flatMap,filter,sum関数をfoldRightで実装して、これを証明してください
   */
  // 答え見た
//  def main(args: Array[String]): Unit = {
//    def map[A, B](list: List[A])(func: A => B): List[B] =
//      list.foldRight(List.empty[B]) { (item, accum) =>
//        func(item) :: accum
//      }
//
//    def flatMap[A, B](list: List[A])(func: A => List[B]): List[B] =
//      list.foldRight(List.empty[B]) { (item, accum) =>
//        func(item) ::: accum
//      }
//
//    def filter[A](list: List[A])(func: A => Boolean): List[A] =
//      list.foldRight(List.empty[A]) { (item, accum) =>
//        if (func(item)) item :: accum else accum
//      }
//
//    def sumWithNumeric[A](list: List[A])(implicit numeric: Numeric[A]): A =
//      list.foldRight(numeric.zero)(numeric.plus)
//
//    import cats.Monoid
//    def sumWithMonoid[A](list: List[A])(implicit monoid: Monoid[A]): A =
//      list.foldRight(monoid.empty)(monoid.combine)
//
//    // accum
//    // 6 :: Nil
//    // 4 :: 6 :: Nil
//    // 2 :: 4 :: 6 :: Nil
//    println(map(List(1, 2, 3))(_ * 2)) // List(2, 4, 6)
//
//    // accum
//    // List(3, 30, 300) ::: Nil
//    // List(2, 20, 200) ::: List(3, 30, 300) ::: Nil
//    // List(1, 10, 100) ::: List(2, 20, 200) ::: List(3, 30, 300) ::: Nil
//    println(flatMap(List(1, 2, 3))(a => List(a, a * 10, a * 100))) // List(1, 10, 100, 2, 20, 200, 3, 30, 300)
//
//    // accum
//    // 3 :: Nil
//    // 3 :: Nil
//    // 1 :: 3 :: Nil
//    println(filter(List(1, 2, 3))(_ % 2 == 1)) // List(1, 3)
//
//    // accum
//    // 3 + 0
//    // 2 + 3 + 0
//    // 1 + 2 + 3 + 0
//    println(sumWithNumeric(List(1, 2, 3))) // 6
//
//    // MonoidのIntのcombineは加算
//    import cats.instances.int._
//    // accum
//    // 3 + 0
//    // 2 + 3 + 0
//    // 1 + 2 + 3 + 0
//    println(sumWithMonoid(List(1, 2, 3))) // 6
//  }

  // 7.1.4 Foldable in Cats
  /**
   * CatsのFoldableは、foldLeftとfoldRightを型クラスに抽象化します
   * Foldableインスタンスは、これら2つの関数が定義されており、多くの派生関数が継承されています
   * Catsは、いくつかのScalaデータ型（List,Vector,LazyList,Option）に対して、Foldableインスタンスを提供します
   *
   * Foldable.applyを利用して、通常通りにインスタンスを呼び出し、foldLeftの実装を直接呼び出すことができます
   * Listを使用した例を次に示します
   */
//  import cats.Foldable
//  import cats.instances.list._
//
//  def main(args: Array[String]): Unit = {
//    val ints = List(1, 2, 3)
//    println(Foldable[List].foldLeft(ints, 0)(_ + _)) // 6
//  }

  /**
   * VectorやLazyListなどの他のシーケンスでも同じように機能します
   * ここでは、0または1の要素のシーケンスのように扱われるOptionを使用した例を紹介します
   */
//  import cats.Foldable
//  import cats.instances.option._
//
//  def main(args: Array[String]): Unit = {
//    val maybeInt = Option(123)
//    // 10 * 123
//    println(Foldable[Option].foldLeft(maybeInt, 10)(_ * _)) // 1230
//  }

  // 7.1.4.1 Folding Right
  /**
   * Foldableは、EvalモナドについてfoldRightをfoldLeftとは異なる方法で定義します
   */
//  def foldRight[A, B](fa: F[A], lb: Eval[B])(
//      f: (A, Eval[B]) => Eval[B]): Eval[B]

  /**
   * Evalを使用すると、コレクションのデフォルトのfoldRightの定義でない場合でも、foldingは常にスタックセーフになります
   * （foldRightは基本的にスタックセーフではないが、EvalのfoldRightはスタックセーフである）
   * たとえば、LazyListのfoldRightのデフォルトの実装はスタックセーフではありません
   * LazyListが長いほど、foldのスタックは大きくなり、StackOverflowErrorをトリガーします
   */
//  import cats.Eval
//  import cats.Foldable
//
//  def bigData = (1 to 100000).to(LazyList)
//
//  // StackOverflowError
//  // (... + (99998 + (99999 + (100000 + 0L))))
//  bigData.foldRight(0L)(_ + _)

  /**
   * Foldableを使うと、スタックセーフ操作が強制されるため、オーバーフロー例外が修正されます
   */
//  import cats.Eval
//  import cats.Foldable
//  import cats.instances.lazyList._
//
//  def bigData = (1 to 100000).to(LazyList)
//
//  val eval: Eval[Long] =
//    Foldable[LazyList].foldRight(bigData, Eval.now(0L)) { (num, eval) =>
//      // Evalのmapはトランポリン化されるので、スタックセーフ
//      // 引数fを末尾再帰で手続き的に呼び出せる
//      eval.map(_ + num)
//    }
//
//  def main(args: Array[String]): Unit = {
//    println(eval.value) // 5000050000
//  }

  /**
   * 標準ライブラリのスタックの安全性
   *
   * 標準ライブラリを使用する場合、スタックの安全性は通常は問題にはなりません
   * ListやVectorなど最も一般的に使用されるコレクション型は、スタックセーフなfoldRightの実装を提供します
   * (1 to 100000).toList.foldRight(0L)(_ + _) // 5000050000L
   * (1 to 100000).toVector.foldRight(0L)(_ + _) // 5000050000L
   *
   * ここでStreamを取り上げたのは、このルールの例外だからです（？）
   * しかし、どのようなデータ型を使用していても、Evalが我々をサポートしていることを知っておくと便利です
   */
  // 7.1.4.2 Folding with Monoids
  /**
   * Foldableは、foldLeftで定義された多くの便利な関数を提供します
   * これらの多くは、標準ライブラリの使い慣れた関数（find,exists,forall,toList,isEmpty,nonEmptyなど）の複製です
   */
//  import cats.Foldable
//  import cats.implicits._
//
//  def main(args: Array[String]): Unit = {
//    val res1 = Foldable[Option].nonEmpty(Option(42))
//    val res2 = Foldable[List].find(List(1, 2, 3))(_ % 2 == 0)
//
//    println(res1) // true
//    println(res2) // Some(2)
//  }

  /**
   * これらの使い慣れた方法に加えて、Catsはモノイドを利用する2つの方法を提供します
   *
   * ・combineAll(foldのエイリアス)は、Monoidを使用してシーケンス内のすべての要素を結合します
   * ・foldMapはユーザー提供の関数をシーケンスにmapして、その結果をMonoidを使用して結合します
   *
   * たとえば、combineAllを使用してList[Int]を合計できます
   */
//  import cats.Foldable
//  import cats.implicits.catsStdInstancesForList
//  import cats.instances.int._
//
//  def main(args: Array[String]): Unit = {
//    println(Foldable[List].combineAll(List(1, 2, 3))) // 6
//  }

  /**
   * または、foldMapを使用して各Intを文字列に変換し、連結することもできます
   */
//  import cats.Foldable
//  import cats.implicits.catsStdInstancesForList
//  import cats.instances.string._
//
//  def main(args: Array[String]): Unit = {
//    // 関数（_.toString）の結果を結合する
//    println(Foldable[List].foldMap(List(1, 2, 3))(_.toString)) // 123
//  }

  /**
   * 最後に、Foldableを組み合わせて、ネストされたシーケンスの深い探索をサポートすることができます
   */
//  import cats.Foldable
//  import cats.implicits.{catsStdInstancesForList, catsKernelStdGroupForInt}
//  import cats.instances.vector._
//
//  def main(args: Array[String]): Unit = {
//    val ints = List(Vector(1, 2, 3), Vector(4, 5, 6))
//
//    // ListとVectorのFoldableを合成すれば、ネストされていても数値を評価できる
//    println((Foldable[List] compose Foldable[Vector]).combineAll(ints)) // 21
//
//    println(Foldable[List].combineAll(ints)) // Vector(1, 2, 3, 4, 5, 6)
//    // println(Foldable[Vector].combineAll(ints)) // type mismatch;
//  }

  // 7.1.4.3 Syntax for Foldable
  /**
   * Foldableのすべての関数はcats.syntax.foldable経由で利用できます
   * いずれの場合も、Foldable関数の第一引数が、関数呼び出しのレシーバーになります
   */
//  import cats.implicits.{
//    catsStdInstancesForList,
//    catsKernelStdGroupForInt,
//    catsKernelStdMonoidForString
//  }
//  import cats.syntax.foldable._
//
//  def main(args: Array[String]): Unit = {
//    println(List(1, 2, 3).combineAll) // 6
//    println(List(1, 2, 3).foldMap(_.toString)) // 123
//  }

  /**
   * 暗黙的なものに対する明示的なもの
   *
   * Scalaは、関数がレシーバーで明示的に使用できない場合にのみFoldableインスタンスを使用することに注意してください
   * たとえば、次のコードは、Listで定義されているfoldLeftを使用します
   *
   * List(1, 2, 3).foldLeft(0)(_ + _)
   *
   * 一方、次の汎用コードはFoldableを使用します
   *
   * def sum[F[_]: Foldable](values: F[Int]): Int = values.foldLeft(0)(_ + _)
   *
   * 通常、この違いについて心配する必要はありません
   * そうゆうもんだよ！
   * 必要な関数を呼び出し、コンパイラーは必要に応じてFoldableを使用して、コードが期待通りに動作するようにします
   * foldRightのスタックセーフな実装が必要な場合は、アキュムレータとしてEvalを使用するだけで、コンパイラーがCatsから関数を選択するようになります
   */
}
