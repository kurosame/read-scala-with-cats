package sandbox

object Chapter1 {
  // 1.1 Anatomy of a Type Class
  // このセクションでは、Scalaで型クラスがどう実装されているかを見る
  // 1.1.1 The Type Class
  // シンプルなJSON AST定義
  sealed trait Json

  final case class JsObject(get: Map[String, Json]) extends Json
  final case class JsString(get: String) extends Json
  final case class JsNumber(get: Double) extends Json
  final case object JsNull extends Json

  // trait JsonWriter[A] {
  //   def write(value: A): Json
  // }

  // 1.1.2 Type Class Instances
  // implicitで暗黙的にインスタンスを生成
  final case class Person(name: String, email: String)

  object JsonWriterInstances {
    implicit val stringWriter: JsonWriter[String] =
      new JsonWriter[String] {
        def write(value: String): Json =
          JsString(value)
      }

    implicit val personWriter: JsonWriter[Person] =
      new JsonWriter[Person] {
        def write(value: Person): Json =
          JsObject(
            Map(
              "name" -> JsString(value.name),
              "email" -> JsString(value.email)
            )
          )
      }
  }

  // 1.1.3 Type Class Use
  // シングルトンオブジェクトにメソッドを配置
  object Json {
    def toJson[A](value: A)(implicit w: JsonWriter[A]): Json =
      w.write(value)
  }

//  def main(args: Array[String]): Unit = {
//    import JsonWriterInstances._
//    // implicitで定義してある関連する型クラスインスタンスのpersonWriterを検出して、使ってくれる
//    println(Json.toJson(Person("Dave", "dave@example.com"))) // JsObject(Map(name -> JsString(Dave), email -> JsString(dave@example.com)))
//
//    // implicitを使っていないと、以下のようになる
//    // Json.toJson(Person("Dave", "dave@example.com"))(personWriter)
//  }

  // シングルトンオブジェクトにimplicitクラスを配置
  object JsonSyntax {
    implicit class JsonWriterOps[A](value: A) {
      def toJson(implicit w: JsonWriter[A]): Json =
        w.write(value)
    }
  }

//  def main(args: Array[String]): Unit = {
//    import JsonWriterInstances._
//    import JsonSyntax._
//    println(Person("Dave", "dave@example.com").toJson) // JsObject(Map(name -> JsString(Dave), email -> JsString(dave@example.com)))
//
//    // こちらも同様に、implicitを使っていないと、以下のようになる
//    // Person("Dave", "dave@example.com").toJson(personWriter)
//  }

  // implicitly関数
  // ジェネリック型クラスインターフェイス
  def implicitly[A](implicit value: A): A = value

//  def main(args: Array[String]): Unit = {
//    import JsonWriterInstances._
//    // 型を指定したら、それに該当する関数を取ってくる
//    // 以下はstringWriter
//    println(implicitly[JsonWriter[String]]) // chapter1.Chapter1$JsonWriterInstances$$anon$1@ea4a92b
//
//    // implicitを使っていないと、以下のようになる
//    // implicitly[JsonWriter[String]](JsonWriterInstances.stringWriter)
//  }

  // 1.2.1 Packaging Implicits
  // implicitの定義は、トップレベルでは定義できず、オブジェクトやトレイト内に配置する必要がある
  // 上記の例では、JsonWriterInstancesオブジェクトにパッケージングした
  // これをJsonWriterのコンパニオンオブジェクトに配置することもできる
  // コンパニオンオブジェクト内にインスタンスを配置することは、implicitのスコープと呼ばれるもので機能する

  // 1.2.2 Implicit Scope
  // 主に以下のスコープがある
  // ・ローカルもしくは継承した定義
  // ・インポートした定義
  // ・型クラス（ここではJsonWriter）もしくは、型パラメータ（ここではStringかPerson）のコンパニオンオブジェクトの定義
  //
  // implicitの候補が複数あるとコンパイルエラーになる
  // implicit val writer1: JsonWriter[String] = JsonWriterInstances.stringWriter
  // implicit val writer2: JsonWriter[String] = JsonWriterInstances.stringWriter
  // Json.toJson("A string") // エラー

  // 1.2.3 Recursive Implicit Resolution
  // Option用のJsonWriterを定義する
  // implicit val optionIntWriter: JsonWriter[Option[Int]] = ???
  // implicit val optionPersonWriter: JsonWriter[Option[Person]] = ???
  // これだとAとOption[A]の2つをimplicit valで定義する必要があるため、拡張性がない

  // Option[A]を処理するためのコードを定義し、Someの時にAも処理できるようにする
  // implicit def optionWriter[A](
  //     implicit writer: JsonWriter[A]): JsonWriter[Option[A]] =
  //   new JsonWriter[Option[A]] {
  //     def write(option: Option[A]): Json =
  //       option match {
  //         case Some(aValue) => writer.write(aValue)
  //         case None         => JsNull
  //       }
  //   }

//  def main(args: Array[String]): Unit = {
//    import JsonWriterInstances._
//    // 以下ではコンパイラーは、JsonWriter[Option[String]]を検索し、JsonWriter[Option[A]]を発見する
//    println(Json.toJson(Option("A string"))) // JsString(A string)
//    // toJsonのimplicitにoptionWriterを直接指定
//    // optionWriterのimplicitのJsonWriter[String]に該当する関数を再帰的に検索する
//    println(Json.toJson(Option("A string"))(optionWriter[String])) // JsString(A string)
//    println(Json.toJson(Option("A string"))(optionWriter(stringWriter))) // JsString(A string)
//  }

  // implicitで定義した関数のパラメーターはimplicitにしないといけない
  // implicit def optionWriter[A](writer: JsonWriter[A]): JsonWriter[Option[A]] = ??? // Warning

  // 1.3 Exercise: Printable Library
  // 1. format関数を含む型クラスPrintable[A]を定義せよ
  //    format関数はAが引数、Stringが戻り値となる
  // 2. StringとIntのPrintableインスタンスを含むPrintableInstancesを作成せよ
  // 3. 2つの汎用インターフェイスを持つPrintableオブジェクトを定義せよ
  // 答え見た
  trait Printable[A] {
    def format(value: A): String
  }

  object PrintableInstances {
    implicit val stringPrintable: Printable[String] =
      new Printable[String] {
        def format(value: String): String = value
      }

    implicit val intPrintable: Printable[Int] =
      new Printable[Int] {
        def format(value: Int): String = value.toString
      }
  }

  object Printable {
    def format[A](input: A)(implicit p: Printable[A]): String =
      p.format(input)

    def print[A](input: A)(implicit p: Printable[A]): Unit =
      println(format(input))
  }

  // 1.4 Meet Cats
  // このセクションでは、Catsで型クラスがどう実装されているか見る

  // 1.4.1 Importing Type Classes
  import cats.Show
  // すべてのCats型クラスのコンパニオンオブジェクトははapply関数を持つ
  // ただし、implicitで定義されたインスタンスをスコープ内に含めないと以下はエラーになる
  // val showInt = Show.apply[Int] // エラー: No implicit arguments of type: Show[Int]

  // 1.4.2 Importing Default Instances
  // cats.instancesパッケージをインポートすればエラーはなくなり、使えるようになる
  import cats.instances.int._
  import cats.instances.string._
  // val showInt: Show[Int] = Show.apply[Int]
  // val showString: Show[String] = Show.apply[String]

//  def main(args: Array[String]): Unit = {
//    val intAsString: String = showInt.show(123)
//    val stringAsString: String = showString.show("abc")
//    println(intAsString) // 123
//    println(stringAsString) // abc
//  }

  // 1.4.3 Importing Interface Syntax
  // 上記のcats.instancesでインポートしたすべての型にshowメソッドを追加できる
  import cats.syntax.show._

//  def main(args: Array[String]): Unit = {
//    val shownInt = 123.show
//    val shownString = "abc".show
//    println(shownInt) // 123
//    println(shownString) // abc
//  }

  // 1.4.4 Importing All The Things!
  // すべての型クラスと構文をインポートできる
  // import cats._
  // import cats.implicits._

  // 1.4.5 Defining Custom Instances
  // JavaでShowのインスタンスを以下のように定義できる
  import java.util.Date

  // implicit val dateShow: Show[Date] =
  //   new Show[Date] {
  //     def show(date: Date): String =
  //       s"${date.getTime}ms since the epoch."
  //   }

//  def main(args: Array[String]): Unit = {
//    println(new Date().show) // 1605883196572ms since the epoch.
//  }

  // しかし、Showのコンパニオンオブジェクトで以下を定義すれば、簡略化できる
  // object Show {
  //   def show[A](f: A => String): Show[A] = ???
  //   def fromToString[A]: Show[A] = ???
  // }

  implicit val dateShow: Show[Date] =
    Show.show(date => s"${date.getTime}ms since the epoch.")

  // 1.4.6 Exercise: Cat Show
  // Printableの代わりにShowを使って、前のセクションのCatアプリケーションを再実装せよ
  // 答え見た
  // そもそも、Catアプリケーションが何なのか分からなかった
  // final case class Cat(name: String, age: Int, color: String)

  implicit val catShow: Show[Cat] = Show.show[Cat] { cat =>
    val name = cat.name.show
    val age = cat.age.show
    val color = cat.color.show
    s"$name is a $age year-old $color cat."
  }

//  def main(args: Array[String]): Unit = {
//    println(Cat("Garfield", 38, "ginger and black").show) // Garfield is a 38 year-old ginger and black cat.
//  }

  // 1.5 Example: Eq
  // この章では、cats.Eqを見る
  // 以下はミスをしている、IntとOption[Int]を比較しているので、常にfalseを返してしまう
  // itemとSome(1)を比較するのが正しい
  // List(1, 2, 3).map(Option(_)).filter(item => item == 1)
  // ==では上記は型エラーにならない（ランタイムエラーになる）が、Eqだとタイプセーフな設計なので、上記を型エラーにしてくれる

  // 1.5.1 Equality, Liberty, and Fraternity
  // cats.syntax.eqで定義されているインターフェイス構文では、スコープ内にEq[A]インスタンスがある場合、
  // 同等性をチェックする「===」と「=!=」の2つのメソッドが用意されている
  // 「===」が2つのオブジェクトの平等比較で「=!=」が2つのオブジェクトの不平等比較になる

  // 1.5.2 Comparing Ints
  import cats.Eq
  import cats.instances.int._

  val eqInt = Eq[Int]

//  def main(args: Array[String]): Unit = {
//    println(eqInt.eqv(123, 123)) // true
//    println(eqInt.eqv(123, 234)) // false
//    // println(eqInt.eqv(123, "123")) // type mismatch error
//    // println(123 == "123") // runtime error
//  }

  import cats.syntax.eq._

//  def main(args: Array[String]): Unit = {
//    println(123 === 123) // true
//    println(123 =!= 234) // true
//    // println(123 === "123") // type mismatch error
//  }

  // 1.5.3 Comparing Options
  // import cats.instances.int._
  // import cats.instances.option._

//  def main(args: Array[String]): Unit = {
//    // 以下は型が完全一致していないからエラー
//    // IntとOption[Int]のスコープにEqインスタンスがあるが、比較している値はSome[Int]だからである
//    // println(Some(1) === None) // error
//    // 以下のようにすればよい
//    println((Some(1): Option[Int]) === (None: Option[Int])) // false
//    // さらに標準ライブラリのOption.applyとOption.emptyを使って、より良い方法で書ける
//    println(Option(1) === Option.empty[Int]) // false
//    // もしくは以下のような書き方もある
//    import cats.syntax.option._
//    println(1.some === none[Int]) // false
//    println(1.some =!= none[Int]) // true
//  }

  // 1.5.4 Comparing Custom Types
  // Eq.instanceを使って、独自のEqインスタンスを定義できる
  import java.util.Date
  import cats.instances.long._

  implicit val dateEq: Eq[Date] =
    Eq.instance[Date] { (date1, date2) =>
      date1.getTime === date2.getTime
    }

  val x = new Date()
  Thread.sleep(1000)
  val y = new Date()

//  def main(args: Array[String]): Unit = {
//    println(x === x) // true
//    println(x === y) // false
//  }

  // 1.5.5 Exercise: Equality, Liberty, and Felinity
  // 前のセクションのCatにEqインスタンスを実装せよ
  final case class Cat(name: String, age: Int, color: String)

  implicit val catEq: Eq[Cat] =
    Eq.instance[Cat] { (c1, c2) =>
      c1.name === c2.name && c1.age === c2.age && c1.color === c2.color
    }

  // cat1とcat2、optionCat1とoptionCat2の等式と不等式を比較せよ
  val cat1 = Cat("Garfield", 38, "orange and black")
  val cat2 = Cat("Heathcliff", 33, "orange and black")

  val optionCat1 = Option(cat1)
  val optionCat2 = Option.empty[Cat]

//  def main(args: Array[String]): Unit = {
//    println(cat1 === cat2) // false
//    println(cat1 =!= cat2) // true
//    import cats.instances.option._
//    println(optionCat1 === optionCat2) // false
//    println(optionCat1 =!= optionCat2) // true
//  }

  // 1.6 Controlling Instance Selection
  // 型クラスを操作する時は、2つの問題を考える必要がある
  // ① 型定義されたインスタンスとそのサブタイプの関係性
  // 　 JsonWriter[Option[Int]]が定義された時、Json.toJson(Some(1))はこの定義を選択するだろうか？
  // 　 ⇒ SomeはOptionのサブタイプである
  // ② 利用可能な型クラスインスタンスが多数ある場合、どのように1つを選択するか

  // 1.6.1 Variance
  // 共変（Covariance）・反変（Contravariance）・不変（Invariance）の3つの変位（Variance）を説明する

  // +は共変を意味する
  // trait F[+A]

  // 共変性とは、BがAのサブタイプである場合、F[B]がF[A]のサブタイプであることを意味する
  // これはListやOptionなどのコレクションのモデリングで役に立つ
  trait List[+A]
  trait Option[+A]

  // Scalaの共変性により、ある型のコレクションをサブタイプのコレクションに置き換えることができる
  sealed trait Shape
  case class Circle(radius: Double) extends Shape

  // val circles: List[Circle] = ???
  // CircleはShapeのサブタイプなので、List[Shape]コレクションをList[Circle]に置き換えてよい
  // val shapes: List[Shape] = circles

  // -は反変を意味する
  // trait F[-A]

  // 反変性とは、AがBのサブタイプである場合、F[B]がF[A]のサブタイプであることを意味する
  // これはJsonWriterのように入力を表す型をモデリングするときに役に立つ
  trait JsonWriter[-A] {
    def write(value: A): Json
  }

  // 以下のコードは反変性をモデリングしている
  // CircleはShapeのサブタイプなので、shapeWriterとcircleWriterの両方をformatに渡せる
  // （CircleはShapeのサブタイプなので、JsonWriter[Shape]はJsonWriter[Circle]のサブタイプとなる）
  // （つまり、JsonWriter[Circle]が使える所はshapeWriterを使用できることを意味する）
  // 逆にShapeは、shapeWriterのみformatに渡せる
  //  val shape: Shape = ???
  //  val circle: Circle = ???

  //  val shapeWriter: JsonWriter[Shape] = ???
  //  val circleWriter: JsonWriter[Circle] = ???

  def format[A](value: A, writer: JsonWriter[A]): Json =
    writer.write(value)

  // +と-がない場合、不変を意味する
  // AとBに関係性がなく、F[A]とF[B]が互いにサブタイプではないことを意味する
  trait F[A]

  // コンパイラーがimplicitを検索する時、変位アノテーションを使用して、型クラスインスタンスの選択をある程度制御できる
  // ただし、発生しがちな2つの問題がある
  //  sealed trait A
  //  final case object B extends A
  //  final case object C extends A
  // ① スーパータイプで定義されたインスタンスが利用可能な場合、それがサブタイプで利用できるか
  // 　 つまり、Aのインスタンスを定義して、型Bと型Cの値に対して機能させることはできるか
  // ② サブタイプのインスタンスは、スーパータイプのインスタンスよりも優先されるか
  // 　 つまり、型Aと型Bのインスタンスを定義し、型Bの値がある場合、AよりもBのインスタンスが選択されるか
  // 上記の①と②は両立しない
  // 各変位ごとの動作は以下のようになる
  // 共変：①は不可、②は可
  // 反変：①は可、②は不可
  // 不変：①も②も不可

  // Catsは不変性の型を使用することを好む

  // 1.7 Summary
}
