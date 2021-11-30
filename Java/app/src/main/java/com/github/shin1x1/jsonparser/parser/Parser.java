package com.github.shin1x1.jsonparser.parser;

import com.github.shin1x1.jsonparser.lexer.Lexer;
import com.github.shin1x1.jsonparser.lexer.Token;
import com.github.shin1x1.jsonparser.lexer.exception.UnexpectedEotException;
import com.github.shin1x1.jsonparser.parser.exception.NoKeyException;
import com.github.shin1x1.jsonparser.parser.exception.UnexpectedTokenException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public final class Parser {
    @Nonnull
    private final Lexer lexer;

    public Parser(@Nonnull Lexer lexer) {
        this.lexer = lexer;
    }

    public JsonValue parse() throws IOException {
        var token = lexer.getNextToken();
        if (token.isEmpty()) {
            return new JsonValue.Null();
        }

        return parseValue(token.get());
    }

    private JsonValue parseValue(Token token) throws IOException {
        if (token instanceof Token.True) {
            return new JsonValue.True();
        } else if (token instanceof Token.False) {
            return new JsonValue.False();
        } else if (token instanceof Token.Null) {
            return new JsonValue.Null();
        } else if (token instanceof Token.String) {
            return new JsonValue.String(((Token.String) token).value());
        } else if (token instanceof Token.Number) {
            return new JsonValue.Number(((Token.Number) token).value());
        } else if (token instanceof Token.LeftBracket) {
            return parseArray();
        } else if (token instanceof Token.LeftBrace) {
            return parseObject();
        }

        throw new UnexpectedTokenException(token);
    }

    private JsonValue.Array parseArray() throws IOException {
        enum State {
            Default, Value, Comma
        }

        var list = new ArrayList<JsonValue>();
        var state = State.Default;

        while (true) {
            var token = lexer.getNextToken().orElseThrow(UnexpectedEotException::new);

            switch (state) {
                case Default -> {
                    if (token instanceof Token.RightBracket) {
                        return new JsonValue.Array(list);
                    }
                    list.add(parseValue(token));
                    state = State.Value;
                }
                case Value -> {
                    if (token instanceof Token.RightBracket) {
                        return new JsonValue.Array(list);
                    } else if (token instanceof Token.Comma) {
                        state = State.Comma;
                    } else {
                        throw new UnexpectedTokenException(token);
                    }
                }
                case Comma -> {
                    list.add(parseValue(token));
                    state = State.Value;
                }
            }
        }
    }

    private JsonValue.Object parseObject() throws IOException {
        enum State {
            Default, Key, Colon, Value, Comma
        }

        var map = new HashMap<String, JsonValue>();
        Optional<String> key = Optional.empty();
        var state = State.Default;

        while (true) {
            var token = lexer.getNextToken().orElseThrow(UnexpectedEotException::new);

            switch (state) {
                case Default -> {
                    if (token instanceof Token.RightBrace) {
                        return new JsonValue.Object(map);
                    }

                    if (token instanceof Token.String) {
                        key = Optional.of(((Token.String) token).value());
                        state = State.Key;
                    } else {
                        throw new UnexpectedTokenException(token);
                    }
                }
                case Key -> {
                    if (token instanceof Token.Colon) {
                        state = State.Colon;
                    } else {
                        throw new UnexpectedTokenException(token);
                    }
                }
                case Colon -> {
                    var value = parseValue(token);
                    var k = key.orElseThrow(NoKeyException::new);

                    map.put(k, value);
                    key = Optional.empty();
                    state = State.Value;
                }
                case Value -> {
                    if (token instanceof Token.RightBrace) {
                        return new JsonValue.Object(map);
                    }

                    if (token instanceof Token.Comma) {
                        state = State.Comma;
                    } else {
                        throw new UnexpectedTokenException(token);
                    }
                }
                case Comma -> {
                    if (token instanceof Token.String) {
                        key = Optional.of(((Token.String) token).value());
                        state = State.Key;
                    } else {
                        throw new UnexpectedTokenException(token);
                    }
                }
            }
        }
    }
}
