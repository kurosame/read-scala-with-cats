package chapter1

object Chapter1 {

  // 1.1.1 The Type Class
  // シンプルなJSON AST定義
  sealed trait Json

  final case class JsObject(get: Map[String, Json]) extends Json
  final case class JsString(get: String) extends Json
  final case class JsNumber(get: Double) extends Json
  final case object JsNull extends Json

  trait JsonWriter[A] {
    def write(value: A): Json
  }

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
            ))
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
  implicit def optionWriter[A](
      implicit writer: JsonWriter[A]): JsonWriter[Option[A]] =
    new JsonWriter[Option[A]] {
      def write(option: Option[A]): Json =
        option match {
          case Some(aValue) => writer.write(aValue)
          case None         => JsNull
        }
    }

  def main(args: Array[String]): Unit = {
    import JsonWriterInstances._
    // 以下ではコンパイラーは、JsonWriter[Option[String]]を検索し、JsonWriter[Option[A]]を発見する
    println(Json.toJson(Option("A string"))) // JsString(A string)
    // toJsonのimplicitにoptionWriterを直接指定
    // optionWriterのimplicitのJsonWriter[String]に該当する関数を再帰的に検索する
    println(Json.toJson(Option("A string"))(optionWriter[String])) // JsString(A string)
    println(Json.toJson(Option("A string"))(optionWriter(stringWriter))) // JsString(A string)
  }

  // implicitで定義した関数のパラメーターはimplicitにしないといけない
  // implicit def optionWriter[A](writer: JsonWriter[A]): JsonWriter[Option[A]] = ??? // Warning

  // 1.3 Exercise: Printable Library
  // 1. format関数を含む型クラスPrintable[A]を定義せよ
  //    format関数はAが引数、Stringが戻り値となる
  // 2. StringとIntのPrintableインスタンスを含むPrintableInstancesを作成せよ
  // 3. 2つの汎用インターフェイスを持つPrintableオブジェクトを定義せよ
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
}
