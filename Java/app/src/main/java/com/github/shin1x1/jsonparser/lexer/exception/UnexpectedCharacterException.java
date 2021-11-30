package com.github.shin1x1.jsonparser.lexer.exception;

public class UnexpectedCharacterException extends RuntimeException {
    public UnexpectedCharacterException(char invalidCharacter) {
        super(String.format("Unexpected character:%c", invalidCharacter));
    }
}
