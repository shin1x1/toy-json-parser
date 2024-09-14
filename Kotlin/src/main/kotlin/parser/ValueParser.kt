package parser

import lexer.Lexer
import lexer.Token

object ValueParser {
    fun parse(
        lexer: Lexer,
        token: Token,
    ): Result<JsonValue> =
        when (token) {
            Token.Null -> Result.success(JsonValue.Null)
            Token.True -> Result.success(JsonValue.True)
            Token.False -> Result.success(JsonValue.False)
            is Token.Number -> Result.success(JsonValue.Number(token.value))
            is Token.String -> Result.success(JsonValue.String(token.value))
            is Token.LeftBrace -> ArrayParser.parse(lexer)
            is Token.LeftBracket -> ObjectParser.parse(lexer)
            else -> Result.failure(InvalidTokenException(Token.Null))
        }
}
