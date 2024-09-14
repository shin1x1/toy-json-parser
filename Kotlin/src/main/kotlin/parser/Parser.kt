package parser

import lexer.Lexer
import stream.EotException

class Parser(
    private val lexer: Lexer,
) {
    fun parse(): Result<JsonValue> {
        if (lexer.isEot()) {
            return Result.success(JsonValue.Null)
        }

        val json =
            lexer.getNextToken().fold(
                onSuccess = { token ->
                    ValueParser.parse(lexer, token).onFailure { return Result.failure(it) }
                },
                onFailure = { return Result.failure(it) },
            )

        return lexer.getNextToken().fold(
            onSuccess = { Result.failure(Exception("Some tokens are remained: $it")) },
            onFailure = { if (it is EotException) json else Result.failure(it) },
        )
    }
}
