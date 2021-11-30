package com.github.shin1x1.jsonparser.parser;

import com.github.shin1x1.jsonparser.lexer.Lexer;
import com.github.shin1x1.jsonparser.lexer.Scanner;
import com.github.shin1x1.jsonparser.lexer.exception.UnexpectedEotException;
import com.github.shin1x1.jsonparser.parser.exception.UnexpectedTokenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest {
    @Test
    void parse_number() throws IOException {
        var scanner = new Scanner("123.45");
        var sut = new Parser(new Lexer(scanner));

        assertEquals(new JsonValue.Number("123.45"), sut.parse());
    }

    @Test
    void parse_string() throws IOException {
        var scanner = new Scanner("\"abc\"");
        var sut = new Parser(new Lexer(scanner));

        assertEquals(new JsonValue.String("abc"), sut.parse());
    }

    @Test
    void parse_literal() throws IOException {
        var scanner = new Scanner("true");
        var sut = new Parser(new Lexer(scanner));

        assertEquals(new JsonValue.True(), sut.parse());
    }

    @Test
    void parse_empty() throws IOException {
        var scanner = new Scanner("");
        var sut = new Parser(new Lexer(scanner));

        assertEquals(new JsonValue.Null(), sut.parse());
    }

    @Test
    void parse_array() throws IOException {
        var scanner = new Scanner("[1,true,\"a\"]");
        var sut = new Parser(new Lexer(scanner));

        List<JsonValue> expectedList = Arrays.asList(
                new JsonValue.Number("1"),
                new JsonValue.True(),
                new JsonValue.String("a")
        );
        assertEquals(new JsonValue.Array(expectedList), sut.parse());
    }


    @Test
    void parse_array_recursive() throws IOException {
        var scanner = new Scanner("[[0,-1,3],\"a\"]");
        var sut = new Parser(new Lexer(scanner));

        List<JsonValue> expectedList = Arrays.asList(
                new JsonValue.Array(
                        Arrays.asList(
                                new JsonValue.Number("0"),
                                new JsonValue.Number("-1"),
                                new JsonValue.Number("3")
                        )
                ),
                new JsonValue.String("a")
        );
        assertEquals(new JsonValue.Array(expectedList), sut.parse());
    }

    @Test
    void parse_object() throws IOException {
        var scanner = new Scanner("{\"key1\": 1, \"key2\": {\"a\": [1,2,3]}}");
        var sut = new Parser(new Lexer(scanner));

        var expectedMap = new HashMap<String, JsonValue>() {
            {
                put("key1", new JsonValue.Number(new BigDecimal("1")));
                put("key2", new JsonValue.Object(new HashMap<>() {
                    {
                        put("a", new JsonValue.Array(Arrays.asList(
                                new JsonValue.Number("1"),
                                new JsonValue.Number("2"),
                                new JsonValue.Number("3")
                        )));
                    }
                }));
            }
        };
        assertEquals(new JsonValue.Object(expectedMap), sut.parse());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{:1}", "{{", "[,", ":"})
    void parse_UnexpectedToken(String json) throws IOException {
        var scanner = new Scanner(json);
        var sut = new Parser(new Lexer(scanner));

        assertThrows(UnexpectedTokenException.class, sut::parse);
    }

    @ParameterizedTest
    @ValueSource(strings = {"[", "[1", "{", "{\"k\":", "\"a"})
    void parse_UnexpectedEot(String json) throws IOException {
        var scanner = new Scanner(json);
        var sut = new Parser(new Lexer(scanner));

        assertThrows(UnexpectedEotException.class, sut::parse);
    }
}