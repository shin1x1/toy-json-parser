package com.github.shin1x1.jsonparser.lexer;

import com.github.shin1x1.jsonparser.lexer.exception.UnexpectedCharacterException;
import com.github.shin1x1.jsonparser.lexer.exception.UnexpectedEotException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LexerTest {
    @Test
    void getNextToken() throws IOException {
        var sut = new Lexer(new Scanner("[]{}:,true,false, \n\r\tnull123.45\"a\\u3042bc\""));

        assertEquals(new Token.LeftBracket(), sut.getNextToken().orElseThrow());
        assertEquals(new Token.RightBracket(), sut.getNextToken().orElseThrow());
        assertEquals(new Token.LeftBrace(), sut.getNextToken().orElseThrow());
        assertEquals(new Token.RightBrace(), sut.getNextToken().orElseThrow());
        assertEquals(new Token.Colon(), sut.getNextToken().orElseThrow());
        assertEquals(new Token.Comma(), sut.getNextToken().orElseThrow());
        assertEquals(new Token.True(), sut.getNextToken().orElseThrow());
        assertEquals(new Token.Comma(), sut.getNextToken().orElseThrow());
        assertEquals(new Token.False(), sut.getNextToken().orElseThrow());
        assertEquals(new Token.Comma(), sut.getNextToken().orElseThrow());
        assertEquals(new Token.Null(), sut.getNextToken().orElseThrow());
        assertEquals(new Token.Number("123.45"), sut.getNextToken().orElseThrow());
        assertEquals(new Token.String("aあbc"), sut.getNextToken().orElseThrow());
    }

    @Test
    void getNextToken_string() throws IOException {
        var sut = new Lexer(new Scanner("\"a\""));

        assertEquals(new Token.String("a"), sut.getNextToken().orElseThrow());
    }

    @Test
    void getNextToken_backslash() throws IOException {
        var sut = new Lexer(new Scanner("\"\\\"\\/\\b\\f\\n\\r\\t\\u3042\""));

        assertEquals(new Token.String("\"/\b\f\n\r\tあ"), sut.getNextToken().orElseThrow());
    }

    @Test
    void getNextToken_number() throws IOException {
        var sut = new Lexer(new Scanner("-123e5"));

        assertEquals(new Token.Number("-1.23e7"), sut.getNextToken().orElseThrow());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "tru1", "nul1", "fal1"})
    void getNextToken_UnexpetedCharacter(String json) throws IOException {
        var sut = new Lexer(new Scanner(json));

        assertThrows(UnexpectedCharacterException.class, sut::getNextToken);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"a", "\"\\u123", "tru", "nul", "fals"})
    void getNextToken_UnexpetedEot(String json) throws IOException {
        var sut = new Lexer(new Scanner(json));

        assertThrows(UnexpectedEotException.class, sut::getNextToken);
    }
}