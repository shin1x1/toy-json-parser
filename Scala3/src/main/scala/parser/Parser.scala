package parser

import lexer.{Lexer, Scanner, Token}

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

enum JsonValue:
  case Number(value: Double)
  case String(value: scala.Predef.String)
  case Null
  case True
  case False
  case Array(value: List[JsonValue])
  case Object(value: Map[scala.Predef.String, JsonValue])

type TryJsonValue = Try[JsonValue]

object Parser:
  def parse(scanner: Scanner): TryJsonValue =
    if scanner.isEot() then return Success(JsonValue.Null)

    Lexer.getNextToken(scanner) match
      case Success(t) => parseValue(t)(using scanner)
      case Failure(e) => Failure(e)

  private def parseValue(token: Token)(using scanner: Scanner): TryJsonValue = token match
    case Token.Number(v) => Success(JsonValue.Number(v))
    case Token.String(v) => Success(JsonValue.String(v))
    case Token.Null => Success(JsonValue.Null)
    case Token.True => Success(JsonValue.True)
    case Token.False => Success(JsonValue.False)
    case Token.LeftBracket => parseArray()
    case Token.LeftBrace => parseObject()
    case x => Failure(Exception(s"Unexpected Token: $x"))

  private def parseArray()(using scanner: Scanner): TryJsonValue =
    enum State:
      case Default, Value, Comma

    import State.*

    @tailrec
    def parse(state: State, list: List[JsonValue]): Option[List[JsonValue]] =
      val token = Lexer.getNextToken(scanner).getOrElse(return None)

      state match
        case Default => token match
          case Token.RightBracket => Some(list)
          case _ => parseValue(token) match
            case Success(v) => parse(Value, list :+ v)
            case Failure(_) => None
        case Value => token match
          case Token.RightBracket => Some(list)
          case Token.Comma => parse(Comma, list)
          case _ => None
        case Comma => parseValue(token) match
          case Success(v) => parse(Value, list :+ v)
          case Failure(_) => None

    parse(Default, List()) match
      case Some(l) => Success(JsonValue.Array(l))
      case None => Failure(Exception("Invalid array"))

  private def parseObject()(using scanner: Scanner): TryJsonValue =
    enum State:
      case Default, Value, Comma, Colon, Key

    import State.*

    @tailrec
    def parse(state: State, map: Map[String, JsonValue], key: Option[String]): Option[Map[String, JsonValue]] =
      val token = Lexer.getNextToken(scanner).getOrElse(return None)

      state match
        case Default => token match
          case Token.RightBrace => Some(map)
          case Token.String(s) => parse(Key, map, Some(s))
          case _ => None
        case Key => token match
          case Token.Colon => parse(Colon, map, key)
          case _ => None
        case Colon => {
          val keyString = key.getOrElse(return None)
          parseValue(token) match
            case Success(v) => parse(Value, map + (keyString -> v), None)
            case Failure(_) => None
        }
        case Value => token match
          case Token.RightBrace => Some(map)
          case Token.Comma => parse(Comma, map, None)
          case _ => None
        case Comma => token match
          case Token.RightBrace => Some(map)
          case Token.String(s) => parse(Key, map, Some(s))
          case _ => None

    parse(Default, Map(), None) match
      case Some(l) => Success(JsonValue.Object(l))
      case None => Failure(Exception("Invalid object"))
