package com.github.shin1x1.jsonparser.lexer;

import java.math.BigDecimal;

public sealed interface Token {
    record Null() implements Token {
    }

    record True() implements Token {
    }

    record False() implements Token {
    }

    record LeftBrace() implements Token {
    }

    record RightBrace() implements Token {
    }

    record LeftBracket() implements Token {
    }

    record RightBracket() implements Token {
    }

    record Colon() implements Token {
    }

    record Comma() implements Token {
    }

    record Number(BigDecimal value) implements Token {
        public Number(java.lang.String value) {
            this(new BigDecimal(value));
        }
    }

    record String(java.lang.String value) implements Token {
    }
}
