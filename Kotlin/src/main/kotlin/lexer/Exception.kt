package lexer

data class UnknownTokenException(val char: Char, val position: Int, val readText: String) : Exception()

data class InvalidLiteralException(val char: Char) : Exception()
