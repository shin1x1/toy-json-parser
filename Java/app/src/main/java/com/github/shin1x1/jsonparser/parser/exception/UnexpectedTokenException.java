package com.github.shin1x1.jsonparser.parser.exception;

import com.github.shin1x1.jsonparser.lexer.Token;

public class UnexpectedTokenException extends RuntimeException {
    public UnexpectedTokenException(Token token) {
        super(String.format("Unexpected token:%s", token));
    }
}
