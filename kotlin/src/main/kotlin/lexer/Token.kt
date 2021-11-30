package lexer

sealed interface Token {
    object Null : Token {
        override fun toString(): kotlin.String = "Null"
    }

    object True : Token {
        override fun toString(): kotlin.String = "True"
    }

    object False : Token {
        override fun toString(): kotlin.String = "False"
    }

    object Colon : Token {
        override fun toString(): kotlin.String = "Colon"
    }

    object Comma : Token {
        override fun toString(): kotlin.String = "Comma"
    }

    object LeftBrace : Token {
        override fun toString(): kotlin.String = "LeftBrace"
    }

    object RightBrace : Token {
        override fun toString(): kotlin.String = "RightBrace"
    }

    object LeftBracket : Token {
        override fun toString(): kotlin.String = "LeftBracket"
    }

    object RightBracket : Token {
        override fun toString(): kotlin.String = "RightBracket"
    }

    data class Number(val value: Double) : Token {
        override fun toString(): kotlin.String = "Number:$value"
    }

    data class String(val value: kotlin.String) : Token {
        override fun toString(): kotlin.String = "String:$value"
    }
}
