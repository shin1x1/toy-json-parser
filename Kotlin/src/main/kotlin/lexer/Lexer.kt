package lexer

import stream.CharacterStream
import kotlin.math.pow

class Lexer(private val stream: CharacterStream) {
    fun getNextToken(): Result<Token> {
        return stream.consume().fold(
            onSuccess = { detectToken(it) },
            onFailure = { Result.failure(it) },
        )
    }

    private fun detectToken(ch: Char): Result<Token> {
        return when (ch) {
            '[' -> Result.success(Token.LeftBrace)
            ']' -> Result.success(Token.RightBrace)
            '{' -> Result.success(Token.LeftBracket)
            '}' -> Result.success(Token.RightBracket)
            ':' -> Result.success(Token.Colon)
            ',' -> Result.success(Token.Comma)
            '-' -> lexNumber(ch)
            in '0'..'9' -> lexNumber(ch)
            '"' -> StringLexer.lex(stream)
            't' -> lexLiteral("true", Token.True)
            'f' -> lexLiteral("false", Token.False)
            'n' -> lexLiteral("null", Token.Null)
            ' ', '\n', '\r', '\t' -> getNextToken()
            else -> Result.failure(UnknownTokenException(ch, stream.position, stream.readText()))
        }
    }

    private fun lexNumber(first: Char): Result<Token.Number> {
        var chs = listOf(first)

        val numbers = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '.', '+', 'e')
        while (true) {
            val ch = stream.peek().getOrNull() ?: break
            if (!numbers.contains(ch)) {
                break
            }

            chs = chs.plus(ch)
            stream.consume()
        }

        return Result.success(Token.Number(chs.fold("") { c, acc -> c + acc }.toDouble()))
    }

    private fun lexLiteral(
        keyword: String,
        token: Token,
    ): Result<Token> {
        for (i in 2..keyword.length) {
            val ch = stream.consume().getOrElse { return Result.failure(it) }

            if (ch != keyword[i - 1]) {
                return Result.failure(InvalidLiteralException(ch))
            }
        }

        return Result.success(token)
    }

    fun isEot(): Boolean = stream.isEot()
}

private object StringLexer {
    fun lex(stream: CharacterStream): Result<Token.String> {
        val string = StringBuilder()

        var backslash = false
        while (true) {
            val ch = stream.consume().getOrElse { return Result.failure(it) }

            when (backslash) {
                false -> {
                    if (ch == '\\') {
                        backslash = true
                        continue
                    }

                    backslash = false

                    if (ch == '"') {
                        return Result.success(Token.String(string.toString()))
                    }

                    string.append(ch)
                }

                true -> {
                    backslash = false
                    when (ch) {
                        '"' -> string.append('"')
                        '\\' -> string.append('\\')
                        '/' -> string.append('/')
                        'b' -> string.append('\b')
                        'f' -> string.append(0xF.toChar())
                        'n' -> string.append('\n')
                        'r' -> string.append('\r')
                        't' -> string.append('\t')
                        'u' -> {
                            lexCodepoint(stream).fold(
                                onSuccess = { string.append(it) },
                                onFailure = { return Result.failure(it) },
                            )
                        }
                    }
                }
            }
        }
    }

    private fun lexCodepoint(stream: CharacterStream): Result<Char> {
        fun isHex(ch: Char): Boolean =
            when (ch) {
                in '0'..'9' -> true
                in 'A'..'F' -> true
                else -> false
            }

        var code = 0
        for (i in 0..3) {
            val hex =
                stream.consume().fold(
                    onSuccess = { if (isHex(it)) it else return Result.failure(Exception("Invalid hex: $it")) },
                    onFailure = { return Result.failure(it) },
                )

            code += (hex - '0') * (16.0.pow(3 - i)).toInt()
        }

        return Result.success(code.toChar())
    }
}
