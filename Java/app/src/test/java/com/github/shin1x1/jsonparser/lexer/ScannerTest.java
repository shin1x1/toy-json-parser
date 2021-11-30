package com.github.shin1x1.jsonparser.lexer;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ScannerTest {
    @Test
    void peek() throws IOException {
        var sut = new Scanner("1");
        assertEquals('1', sut.peek().orElseThrow());
    }

    @Test
    void consume() throws IOException {
        var sut = new Scanner("1あ3");
        assertEquals('1', sut.consume().orElseThrow());
        assertEquals('あ', sut.consume().orElseThrow());
        assertEquals('3', sut.consume().orElseThrow());
        assertTrue(sut.consume().isEmpty());
    }

    @Test
    void isEot() throws IOException {
        var sut = new Scanner("1");
        assertFalse(sut.isEot());
        sut.consume();
        assertTrue(sut.isEot());
    }
}