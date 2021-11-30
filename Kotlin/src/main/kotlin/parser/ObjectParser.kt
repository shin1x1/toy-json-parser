package parser

import lexer.Lexer
import lexer.Token

object ObjectParser {
    private enum class State {
        Default, Value, Comma, Colon, Key,
    }

    fun parse(lexer: Lexer): Result<JsonValue.Object> {
        var state = State.Default
        var map = mapOf<String, JsonValue>()
        var key = ""

        while (true) {
            val token = lexer.getNextToken().getOrElse { return Result.failure(it) }
            when (state) {
                State.Default -> {
                    when (token) {
                        Token.RightBracket -> return Result.success(JsonValue.Object(map))
                        is Token.String -> {
                            key = token.value
                            state = State.Key
                        }
                        else -> return Result.failure(InvalidTokenException(token))
                    }
                }
                State.Key -> {
                    when (token) {
                        Token.Colon -> state = State.Colon
                        else -> return Result.failure(InvalidTokenException(token))
                    }
                }
                State.Colon -> {
                    if (key.isEmpty()) return Result.failure(InvalidTokenException(token))

                    val ret = ValueParser.parse(lexer, token).getOrElse { return Result.failure(it) }
                    map = map.plus(key to ret)
                    state = State.Value
                }
                State.Value -> {
                    when (token) {
                        Token.RightBracket -> return Result.success(JsonValue.Object(map))
                        Token.Comma -> state = State.Comma
                        else -> return Result.failure(InvalidTokenException(token))
                    }
                }
                State.Comma -> {
                    when (token) {
                        is Token.String -> {
                            key = token.value
                            state = State.Key
                        }
                        else -> return Result.failure(InvalidTokenException(token))
                    }
                }
            }
        }
    }
}
