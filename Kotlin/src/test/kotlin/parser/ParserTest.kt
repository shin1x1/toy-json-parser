package parser

import lexer.Lexer
import org.junit.Test
import stream.CharacterStream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class ParserTest {
    @Test
    fun parse() {
        val json = """[null,true,false,[123,"abcあ",0,-23.2],{"name": {"no":10e2}}]"""
        val sut = Parser(Lexer(CharacterStream(json)))

        val array = listOf(
            JsonValue.Null,
            JsonValue.True,
            JsonValue.False,
            JsonValue.Array(
                listOf(
                    JsonValue.Number(123.0),
                    JsonValue.String("abcあ"),
                    JsonValue.Number(0.0),
                    JsonValue.Number(-23.2),
                )
            ),
            JsonValue.Object(
                mapOf(
                    "name" to JsonValue.Object(
                        mapOf(
                            "no" to JsonValue.Number(1000.0)
                        )
                    )
                )
            )
        )

        assertEquals(JsonValue.Array(array), sut.parse().getOrThrow())
    }

    @Test
    fun parse_empty_string() {
        val json = ""
        val sut = Parser(Lexer(CharacterStream(json)))

        assertSame(JsonValue.Null, sut.parse().getOrThrow())
    }

    @Test
    fun parse_remain_tokens() {
        val json = "[]!"
        val sut = Parser(Lexer(CharacterStream(json)))

        assertIs<Exception>(sut.parse().exceptionOrNull())
    }

    @Test
    fun parse_unknown_token() {
        val json = "[!]"
        val sut = Parser(Lexer(CharacterStream(json)))

        println(sut.parse())
        assertIs<Exception>(sut.parse().exceptionOrNull())
    }
}
