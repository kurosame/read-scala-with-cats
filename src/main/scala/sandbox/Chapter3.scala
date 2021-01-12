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

  // 3.5 Functors in Cats
  // 3.5.1 The Functor Type Class and Instances
  /**
    * ファンクターの型クラスは、`cats.Functor`である
    * インスタンスは、`cats.instances`パッケージで型ごとに配置されている
    */
//  import cats.Functor
//  import cats.instances.list._
//  import cats.instances.option._
//
//  def main(args: Array[String]): Unit = {
//    val list1 = List(1, 2, 3)
//    val list2 = Functor[List].map(list1)(_ * 2)
//
//    val option1 = Option(123)
//    val option2 = Functor[Option].map(option1)(_.toString)
//
//    println(list1) // List(1, 2, 3)
//    println(list2) // List(2, 4, 6)
//    println(option1) // Some(123)
//    println(option2) // Some(123)
//  }

  /**
    * Functorはlift関数を提供する
    * これはファンクター上で動作し、`A => B`の関数を`F[A] => F[B]`の関数に変換する
    */
//  def main(args: Array[String]): Unit = {
//    import cats.Functor
//    import cats.instances.option._
//    val func = (x: Int) => x + 1 // Int => Int
//    val liftedFunc = Functor[Option].lift(func) // Option[Int] => Option[Int]
//    println(liftedFunc(Option(1))) // Some(2)
//
//    import cats.instances.list._
//    val list1 = List(1, 2, 3)
//    println(Functor[List].as(list1, "As")) // List(As, As, As)
//  }

  // 3.5.2 Functor Syntax
  /**
    * Functorはmapを提供する
    */
//  def main(args: Array[String]): Unit = {
//    import cats.instances.function._
//    import cats.syntax.functor._
//
//    val func1 = (a: Int) => a + 1
//    val func2 = (a: Int) => a * 2
//    val func3 = (a: Int) => s"${a}!"
//    val func4 = func1.map(func2).map(func3)
//
//    println(func4(123)) // 248!
//  }

  /**
    * 別の例を挙げる
    */
//  def main(args: Array[String]): Unit = {
//    import cats.Functor
//    import cats.syntax.functor._
//
//    def doMath[F[_]](start: F[Int])(implicit functor: Functor[F]): F[Int] =
//      start.map(n => n + 1 * 2)
//
//    import cats.instances.option._
//    import cats.instances.list._
//
//    println(doMath(Option(20))) // Some(22)
//    println(doMath(List(1, 2, 3))) // List(3, 4, 5)
//  }

  /**
    * 上記の例がどのように動くか説明するために、`cats.syntax.functor`のmap関数の定義の簡易版を見てみる
    */
//  import cats.Functor
//  implicit class FunctorOps[F[_], A](src: F[A]) {
//    def map[B](func: A => B)(implicit functor: Functor[F]): F[B] =
//      functor.map(src)(func)
//  }

  /**
    * 以下のコードで、fooはmapを持っていないと仮定すると
    * foo.map(value => value + 1)
    *
    * コンパイラーは、スコープ内にimplicitのFunctorが存在する場合に限り、FunctorOpsでラップして動くようにする
    * new FunctorOps(foo).map(value => value + 1)
    *
    * なお、スコープ内にimplicitのFunctorが存在しない場合は、コンパイラーになる
    *
    * mapを持たないBoxの例を以下に示す
    */
//  import cats.Functor
//  implicit class FunctorOps[F[_], A](src: F[A]) {
//    def map[B](func: A => B)(implicit functor: Functor[F]): F[B] =
//      functor.map(src)(func)
//  }
//
//  final case class Box[A](value: A)
//
//  val box = Box[Int](123)
//
//  // Boxはmapを持たないが、FunctorOpsがあるのでmapを使えるようになる
//  // ただし、`implicit functor: Functor[F]`が必要、つまり、Functor[Box]インスタンスが必要なので、以下はエラー
//  box.map(value => value + 1)

  // 3.5.3 Instances for Custom Types
  /**
    * map関数を定義するだけでファンクターを定義できる
    * OptionのFunctorの例を以下に示す
    */
//  import cats.Functor
//
//  implicit val optionFunctor: Functor[Option] =
//    new Functor[Option] {
//      def map[A, B](value: Option[A])(func: A => B): Option[B] =
//        value.map(func)
//    }

  /**
    * FutureのカスタムFunctorを定義する場合、future.mapのimplicitのExecutionContextが必要になる
    */
//  import cats.Functor
//  import scala.concurrent.{Future, ExecutionContext}
//
//  implicit def futureFunctor(implicit ec: ExecutionContext): Functor[Future] =
//    new Functor[Future] {
//      def map[A, B](value: Future[A])(func: A => B): Future[B] =
//        value.map(func)
//    }
//
//  import scala.concurrent.ExecutionContext.Implicits.global
//  // コンパイラーはスコープ内を検索し、以下を暗黙的に解決する（この上のimportを削除すると以下はエラーになる）
//  Functor[Future]
//  Functor[Future](futureFunctor)

  // 3.5.4 Exercise: Branching out with Functors
  /**
    * 以下の二分木データ型のFunctorを記述せよ
    * BranchとLeafのインスタンスでコードが期待通りに動くことを確認せよ
    */
//  sealed trait Tree[+A]
//
//  final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]
//  final case class Leaf[A](value: A) extends Tree[A]
//
//  // 答え見た
//  import cats.Functor
//
//  implicit val treeFunctor: Functor[Tree] =
//    new Functor[Tree] {
//      def map[A, B](tree: Tree[A])(func: A => B): Tree[B] =
//        tree match {
//          case Branch(left, right) => Branch(map(left)(func), map(right)(func))
//          case Leaf(value)         => Leaf(func(value))
//        }
//    }
//
//  object Tree {
//    def branch[A](left: Tree[A], right: Tree[A]): Tree[A] = Branch(left, right)
//    def leaf[A](value: A): Tree[A] = Leaf(value)
//  }
//
//  def main(args: Array[String]): Unit = {
//    import cats.syntax.functor._
//    println(Tree.leaf(100).map(_ * 2)) // Leaf(200)
//    println(Tree.branch(Tree.leaf(10), Tree.leaf(20)).map(_ * 2)) // Branch(Leaf(20),Leaf(40))
//  }

  /**
    * 次に反変ファンクターと不変ファンクターという型クラスを見ていく
    * ・これまで見てきたFunctorのmapは、チェーンへの変換を追加する
    * ・反変ファンクターは、チェーンに操作を追加する
    * ・不変ファンクターは、双方向のチェーンの操作を構築する
    *
    * これらは4章のモナドを理解するのには、あまり必要がない
    * ただし、6章のSemigroupalやApplicativeを理解するのには、役に立つ
    */
  // 3.5.5 Contravariant Functors and the contramap Method
  /**
    * 反変ファンクターは、チェーンに操作を「追加」することを表す
    */
//  trait Printable[A] {
//    def format(value: A): String
//    def contramap[B](func: B => A): Printable[B] = ???
//  }
//  def format[A](value: A)(implicit p: Printable[A]): String = p.format(value)

  // 3.5.5.1 Exercise: Showing off with Contramap
  /**
    * 上記のPrintableのcontramap関数を実装せよ
    */
//  // 答え見た
//  // 外側のformatと内側のformatを区別するため、selfを使う
//  trait Printable[A] { self =>
//    def format(value: A): String
//    def contramap[B](func: B => A): Printable[B] =
//      new Printable[B] {
//        // funcでBをAに変換し、formatでAをStringに変換する
//        def format(value: B): String = self.format(func(value))
//      }
//  }
//  def format[A](value: A)(implicit p: Printable[A]): String = p.format(value)

  /**
    * StringとBooleanのPrintableを定義する
    */
//  implicit val stringPrintable: Printable[String] =
//    new Printable[String] {
//      def format(value: String): String = s"'${value}'"
//    }
//
//  implicit val booleanPrintable: Printable[Boolean] =
//    new Printable[Boolean] {
//      def format(value: Boolean): String = if (value) "yes" else "no"
//    }
//
////  def main(args: Array[String]): Unit = {
////    println(format("hello")) // 'hello'
////    println(format(true)) // yes
////  }

  /**
    * 次にBoxケースクラスのPrintableインスタンスを定義する
    * implicit defでPrintableを定義せよ
    */
//  // 答え見た
//  final case class Box[A](value: A)
//
//  implicit def boxPrintable[A](implicit p: Printable[A]): Printable[Box[A]] =
//    new Printable[Box[A]] {
//      def format(box: Box[A]): String = p.format(box.value)
//    }
//
//  def main(args: Array[String]): Unit = {
//    println(format(Box("hello world"))) // 'hello world'
//    println(format(Box(true))) // yes
//    // format(Box(123)) // これはintPrintableがないのでエラー
//  }

  // 3.5.6 Invariant functors and the imap method
  /**
    * 不変ファンクターは、mapとcontramapを組み合わせたようなimap関数を実装してる
    * つまり、imapは双方向の変換を行う
    * 不変ファンクターのもっとも直感的な例は、エンコードとデコードである
    */
//  trait Codec[A] {
//    def encode(value: A): String
//    def decode(value: String): A
//    def imap[B](dec: A => B, enc: B => A): Codec[B] = ???
//  }
//  def encode[A](value: A)(implicit c: Codec[A]): String = c.encode(value)
//  def decode[A](value: String)(implicit c: Codec[A]): A = c.decode(value)
//
//  // 使い方
//  implicit val stringCodec: Codec[String] =
//    new Codec[String] {
//      def encode(value: String): String = value
//      def decode(value: String): String = value
//    }
//
//  // stringCodecとimapを使って、他の型のCodecを構築
//  implicit val intCodec: Codec[Int] = stringCodec.imap(_.toInt, _.toString)
//  implicit val booleanCodec: Codec[Boolean] =
//    stringCodec.imap(_.toBoolean, _.toString)

  // 3.5.6.1 Transformative Thinking with imap
  /**
    * 上記のCodecのimap関数を実装せよ
    */
//  // 答え見た
//  trait Codec[A] { self =>
//    def encode(value: A): String
//    def decode(value: String): A
//
//    def imap[B](dec: A => B, enc: B => A): Codec[B] = {
//      new Codec[B] {
//        def encode(value: B): String = self.encode(enc(value))
//        def decode(value: String): B = dec(self.decode(value))
//      }
//    }
//  }

  /**
    * DoubleのCodecを作成して、imap関数が機能することを示せ
    */
//  trait Codec[A] { self =>
//    def encode(value: A): String
//    def decode(value: String): A
//
//    def imap[B](dec: A => B, enc: B => A): Codec[B] = {
//      new Codec[B] {
//        def encode(value: B): String = self.encode(enc(value))
//        def decode(value: String): B = dec(self.decode(value))
//      }
//    }
//  }
//
//  implicit val stringCodec: Codec[String] =
//    new Codec[String] {
//      def encode(value: String): String = value
//      def decode(value: String): String = value
//    }
//
//  implicit val doubleCodec: Codec[Double] =
//    stringCodec.imap(_.toDouble, _.toString)
//
//  def main(args: Array[String]): Unit = {
//    val enc = doubleCodec.encode(123.4)
//    println(enc) // 123.4
//    println(enc.getClass) // class java.lang.String
//    val dec = doubleCodec.decode("123.4")
//    println(dec) // 123.4
//    println(dec.getClass) // double
//  }

  /**
    * 以下のBox型のCodecを実装せよ
    * final case class Box[A](value: A)
    */
//  trait Codec[A] { self =>
//    def encode(value: A): String
//    def decode(value: String): A
//
//    def imap[B](dec: A => B, enc: B => A): Codec[B] = {
//      new Codec[B] {
//        def encode(value: B): String = self.encode(enc(value))
//        def decode(value: String): B = dec(self.decode(value))
//      }
//    }
//  }
//
//  final case class Box[A](value: A)
//
//  // 答え見た
//  implicit def boxCodec[A](implicit c: Codec[A]): Codec[Box[A]] =
//    c.imap[Box[A]](Box(_), _.value)
//
//  def main(args: Array[String]): Unit = {
//    // implicit c: Codec[A]が分からない
//    val enc = boxCodec.encode(Box(123.4))
//    println(enc) //
//    println(enc.getClass) //
//    val dec = boxCodec.decode[Box[Double]]("123.4")
//    println(dec) //
//    println(dec.getClass) //
//  }
}
