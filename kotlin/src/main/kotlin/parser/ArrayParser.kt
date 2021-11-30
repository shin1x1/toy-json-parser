package parser

import lexer.Lexer
import lexer.Token

object ArrayParser {
    private enum class State {
        Default, Value, Comma
    }

    fun parse(lexer: Lexer): Result<JsonValue.Array> {
        var state = State.Default
        var array = listOf<JsonValue>()

        while (true) {
            val token = lexer.getNextToken().getOrElse { return Result.failure(it) }
            when (state) {
                State.Default -> {
                    when (token) {
                        Token.RightBrace -> return Result.success(JsonValue.Array(array))
                        else -> {
                            val ret = ValueParser.parse(lexer, token).getOrElse { return Result.failure(it) }
                            array = array.plus(ret)
                            state = State.Value
                        }
                    }
                }
                State.Value -> {
                    when (token) {
                        Token.RightBrace -> return Result.success(JsonValue.Array(array))
                        Token.Comma -> state = State.Comma
                        else -> return Result.failure(InvalidTokenException(token))
                    }
                }
                State.Comma -> {
                    val ret = ValueParser.parse(lexer, token).getOrElse { return Result.failure(it) }
                    array = array.plus(ret)
                    state = State.Value
                }
            }
        }
    }
}
