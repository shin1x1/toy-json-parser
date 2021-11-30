package parser

import lexer.{Lexer, Scanner, Token}
import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test

import scala.util.Success

class ParserTest:
  @Test def parse(): Unit =
    val scanner = Scanner("""[null,true,false,[0,"あabc",-23.2],{"name": {"no":10e2}}]""")

    val array = List(
      JsonValue.Null,
      JsonValue.True,
      JsonValue.False,
      JsonValue.Array(
        List(
          JsonValue.Number(0.0),
          JsonValue.String("あabc"),
          JsonValue.Number(-23.2),
        )
      ),
      JsonValue.Object(
        Map(
          "name" -> JsonValue.Object(
            Map("no" -> JsonValue.Number(1000.0))
          )
        )
      )
    )

    assertEquals(Success(JsonValue.Array(array)), Parser.parse(scanner))

  @Test def parse_empty(): Unit =
    val scanner = Scanner("")
    assertEquals(Success(JsonValue.Null), Parser.parse(scanner))

  @Test def parse_lf(): Unit =
    val scanner = Scanner("\n")
    assertEquals(Success(JsonValue.Null), Parser.parse(scanner))

  @Test def parse_number(): Unit =
    val scanner = Scanner("-123.45")
    assertEquals(Success(JsonValue.Number(-123.45)), Parser.parse(scanner))

  @Test def parse_string(): Unit =
    val scanner = Scanner(""""abc"""")
    assertEquals(Success(JsonValue.String("abc")), Parser.parse(scanner))

  @Test def parse_empty_array(): Unit =
    val scanner = Scanner("[]")
    assertEquals(Success(JsonValue.Array(List())), Parser.parse(scanner))

  @Test def parse_array(): Unit =
    val scanner = Scanner("""[1, true, "a"]""")
    val array = List(JsonValue.Number(1.0), JsonValue.True, JsonValue.String("a"))
    assertEquals(Success(JsonValue.Array(array)), Parser.parse(scanner))

  @Test def parse_empty_object(): Unit =
    val scanner = Scanner("{}")
    assertEquals(Success(JsonValue.Object(Map())), Parser.parse(scanner))

  @Test def parse_object(): Unit =
    val scanner = Scanner("""{"key1": true, "key2": 123}""")
    val map = Map("key1" -> JsonValue.True, "key2" -> JsonValue.Number(123.0))
    assertEquals(Success(JsonValue.Object(map)), Parser.parse(scanner))
