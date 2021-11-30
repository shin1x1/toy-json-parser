package lexer

import org.junit.Test
import stream.CharacterStream
import stream.EotException
import kotlin.test.assertEquals
import kotlin.test.assertIs

class LexerTest {
    @Test
    fun getNextToken() {
        val json = """[1,1.23,true,false,null]""" + "\n\t\r" + """{"name": "あMike\\\"a"}"""
        val sut = Lexer(CharacterStream(json))

        assertEquals(Token.LeftBrace, sut.getNextToken().getOrThrow())
        assertEquals(Token.Number(1.0), sut.getNextToken().getOrThrow())
        assertEquals(Token.Comma, sut.getNextToken().getOrThrow())
        assertEquals(Token.Number(1.23), sut.getNextToken().getOrThrow())
        assertEquals(Token.Comma, sut.getNextToken().getOrThrow())
        assertEquals(Token.True, sut.getNextToken().getOrThrow())
        assertEquals(Token.Comma, sut.getNextToken().getOrThrow())
        assertEquals(Token.False, sut.getNextToken().getOrThrow())
        assertEquals(Token.Comma, sut.getNextToken().getOrThrow())
        assertEquals(Token.Null, sut.getNextToken().getOrThrow())
        assertEquals(Token.RightBrace, sut.getNextToken().getOrThrow())

        assertEquals(Token.LeftBracket, sut.getNextToken().getOrThrow())
        assertEquals(Token.String("name"), sut.getNextToken().getOrThrow())
        assertEquals(Token.Colon, sut.getNextToken().getOrThrow())
        assertEquals(Token.String("あMike\\\"a"), sut.getNextToken().getOrThrow())
        assertEquals(Token.RightBracket, sut.getNextToken().getOrThrow())

        assertIs<EotException>(sut.getNextToken().exceptionOrNull())
    }

    @Test
    fun getNextToken_invalid_token() {
        val json = """[!]"""
        val sut = Lexer(CharacterStream(json))

        sut.getNextToken() // [
        assertIs<UnknownTokenException>(sut.getNextToken().exceptionOrNull())
    }

    @Test
    fun getNextToken_invalid_literal() {
        val json = """tr!"""
        val sut = Lexer(CharacterStream(json))

        assertIs<InvalidLiteralException>(sut.getNextToken().exceptionOrNull())
        assertIs<EotException>(sut.getNextToken().exceptionOrNull())
    }

    @Test
    fun lexString_escape_sequence() {
        val json = """"\"\\\/\b\f\n\r\t\u3042""""
        val sut = Lexer(CharacterStream(json))

        assertEquals(Token.String(""""\/""" + "\b\u000f\n\r\t\u3042"), sut.getNextToken().getOrThrow())
    }

    @Test
    fun lexString_codepoint() {
        val json = """"a\u3042""""
        val sut = Lexer(CharacterStream(json))

        assertEquals(Token.String("aあ"), sut.getNextToken().getOrThrow())
    }

    @Test
    fun lexString_invalid_codepoint() {
        val json = """"a\u30z2""""
        val sut = Lexer(CharacterStream(json))

        assertIs<Exception>(sut.getNextToken().exceptionOrNull())
    }
}
