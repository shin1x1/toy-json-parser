package parser

import lexer.Token

data class InvalidTokenException(val token: Token) : Exception()
