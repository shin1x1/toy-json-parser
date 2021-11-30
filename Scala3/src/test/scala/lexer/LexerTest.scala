package lexer

import org.junit.Assert.{assertEquals, assertTrue}
import org.junit.Test

import scala.util.Success

class LexerTest:
  @Test def getNextToken(): Unit =
    val scanner = Scanner("""[]{}:,-123.32,12345e-3"\u3042a\""true,false,null""")

    assertEquals(Success(Token.LeftBracket), Lexer.getNextToken(scanner))
    assertEquals(Success(Token.RightBracket), Lexer.getNextToken(scanner))
    assertEquals(Success(Token.LeftBrace), Lexer.getNextToken(scanner))
    assertEquals(Success(Token.RightBrace), Lexer.getNextToken(scanner))
    assertEquals(Success(Token.Colon), Lexer.getNextToken(scanner))
    assertEquals(Success(Token.Comma), Lexer.getNextToken(scanner))
    assertEquals(Success(Token.Number(-123.32)), Lexer.getNextToken(scanner))
    assertEquals(Success(Token.Comma), Lexer.getNextToken(scanner))
    assertEquals(Success(Token.Number(12.345)), Lexer.getNextToken(scanner))
    assertEquals(Success(Token.String("あa\"")), Lexer.getNextToken(scanner))
    assertEquals(Success(Token.True), Lexer.getNextToken(scanner))
    assertEquals(Success(Token.Comma), Lexer.getNextToken(scanner))
    assertEquals(Success(Token.False), Lexer.getNextToken(scanner))
    assertEquals(Success(Token.Comma), Lexer.getNextToken(scanner))
    assertEquals(Success(Token.Null), Lexer.getNextToken(scanner))

  @Test def getNextToken_Unterminated_string(): Unit =
    val scanner = Scanner(""""a""")

    assertTrue(Lexer.getNextToken(scanner).isFailure)

  @Test def getNextToken_Invalid_character(): Unit =
    val scanner = Scanner("a")

    assertTrue(Lexer.getNextToken(scanner).isFailure)

  @Test def getNextToken_Invalid_literal(): Unit =
    val scanner = Scanner("tRue")

    assertTrue(Lexer.getNextToken(scanner).isFailure)

  @Test def getNextToken_Invalid_codepoint(): Unit =
    val scanner = Scanner("\\u3z12")

    assertTrue(Lexer.getNextToken(scanner).isFailure)
