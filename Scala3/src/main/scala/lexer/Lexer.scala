package lexer

import scala.annotation.tailrec
import scala.math.pow
import scala.util.{Failure, Success, Try}

enum Token:
  case LeftBracket, RightBracket
  case LeftBrace, RightBrace
  case Colon, Comma
  case Null, True, False
  case Number(value: Double)
  case String(value: scala.Predef.String)

class InvalidCharacter(val char: Char) extends Exception

class InvalidCodepoint() extends Exception

class InvalidLiteralCharacter() extends Exception

type TryToken = Try[Token]

object Lexer:
  def getNextToken(scanner: Scanner): TryToken = scanner.consume() match
    case Some(c) => lex(c)(using scanner)
    case None => Failure(IndexOutOfBoundsException())

  private def lex(char: Char)(using Scanner): TryToken = char match
    case '[' => Success(Token.LeftBracket)
    case ']' => Success(Token.RightBracket)
    case '{' => Success(Token.LeftBrace)
    case '}' => Success(Token.RightBrace)
    case ':' => Success(Token.Colon)
    case ',' => Success(Token.Comma)
    case '-' | '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' => lexNumber(char)
    case '"' => lexString(char)
    case 't' => lexLiteral("true", Token.True)
    case 'f' => lexLiteral("false", Token.False)
    case 'n' => lexLiteral("null", Token.Null)
    case x => Failure(InvalidCharacter(x))

  private def lexNumber(char: Char)(using scanner: Scanner): TryToken =
    @tailrec
    def lex(string: String): String = scanner.peek() match
      case Some(c) if "-+.e0123456789".contains(c) => {
        scanner.consume()
        lex(string :+ c)
      }
      case _ => string

    try
      Success(Token.Number(lex(char.toString).toDouble))
    catch
      case e: Exception => Failure(e)

  private def lexString(char: Char)(using scanner: Scanner): TryToken =
    def lexCodepoint(): Option[Char] =
      val isHex = (c: Char) => "0123456789ABCDEF".contains(c)
      for
        h1 <- scanner.consume().filter(isHex)
        h2 <- scanner.consume().filter(isHex)
        h3 <- scanner.consume().filter(isHex)
        h4 <- scanner.consume().filter(isHex)
      yield
        ((h1 - '0' << 12) + (h2 - '0' << 8) + (h3 - '0' << 4) + h4 - '0').toChar

    @tailrec
    def lex(string: String, backslash: Boolean = false): TryToken = scanner.consume() match
      case Some(s) if backslash => {
        val ch = s match
          case '"' => '"'
          case '\\' => '\\'
          case '/' => '/'
          case 'b' => '\b'
          case 'f' => '\f'
          case 'n' => '\n'
          case 'r' => '\r'
          case 't' => '\t'
          case 'u' => lexCodepoint() match
            case Some(c1) => c1
            case None => return Failure(InvalidCodepoint())
          case _ => '?'

        lex(string :+ ch, false)
      }
      case Some(s) if s == '"' => Success(Token.String(string))
      case Some(s) if s == '\\' => lex(string, true)
      case Some(s) => lex(string :+ s)
      case None => Failure(Exception())

    lex("")

  private def lexLiteral(literal: String, token: Token)(using scanner: Scanner): TryToken =
    // skip head character in literal
    literal.substring(1).foreach(c => scanner.consume() match
      case Some(s) if s == c => ()
      case _ => return Failure(InvalidLiteralCharacter())
    )

    Success(token)


