package com.github.shin1x1.jsonparser.lexer;

import com.github.shin1x1.jsonparser.lexer.exception.UnexpectedCharacterException;
import com.github.shin1x1.jsonparser.lexer.exception.UnexpectedEotException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Optional;

public final class Lexer {
    @Nonnull
    private final Scanner scanner;

    public Lexer(@Nonnull Scanner scanner) {
        this.scanner = scanner;
    }

    public Optional<Token> getNextToken() throws IOException {
        var ch = scanner.consume();

        if (ch.isEmpty()) {
            return Optional.empty();
        }

        char c = ch.get();
        if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
            return getNextToken();
        }

        return Optional.of(lex(c));
    }

    private Token lex(char ch) throws IOException {
        return switch (ch) {
            case '[' -> new Token.LeftBracket();
            case ']' -> new Token.RightBracket();
            case '{' -> new Token.LeftBrace();
            case '}' -> new Token.RightBrace();
            case ':' -> new Token.Colon();
            case ',' -> new Token.Comma();
            case '"' -> lexString();
            case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> lexNumber(ch);
            case 't' -> lexLiteral("true", new Token.True());
            case 'f' -> lexLiteral("false", new Token.False());
            case 'n' -> lexLiteral("null", new Token.Null());
            default -> throw new UnexpectedCharacterException(ch);
        };
    }

    private Token lexNumber(char first) throws IOException {
        var chs = new StringBuilder(String.valueOf(first));

        while (true) {
            var och = scanner.peek();
            if (och.isEmpty()) {
                break;
            }

            var ch = och.get();
            if (!"0123456789+-e.".contains(ch.toString())) {
                break;
            }

            scanner.consume();
            chs.append(ch);
        }

        return new Token.Number(chs.toString());
    }

    private Token lexString() throws IOException {
        var chs = new StringBuilder();
        var backslash = false;

        while (true) {
            var ch = scanner.consume().orElseThrow(UnexpectedEotException::new);

            if (!backslash) {
                if (ch == '"') {
                    return new Token.String(chs.toString());
                }

                if (ch == '\\') {
                    backslash = true;
                    continue;
                }

                chs.append(ch);
                continue;
            }

            backslash = false;
            chs.append(
                    switch (ch) {
                        case '"' -> '"';
                        case '\\' -> '\\';
                        case '/' -> '/';
                        case 'b' -> '\b';
                        case 'f' -> '\f';
                        case 'n' -> '\n';
                        case 'r' -> '\r';
                        case 't' -> '\t';
                        case 'u' -> lexCodepoint();
                        default -> throw new UnexpectedCharacterException(ch);
                    }
            );
        }
    }

    private char lexCodepoint() throws IOException {
        var code = 0;
        for (var i = 0; i < 4; i++) {
            var ch = scanner.consume().orElseThrow(UnexpectedEotException::new);

            if (!"0123456789ABCDEF".contains(ch.toString())) {
                throw new UnexpectedCharacterException(ch);
            }

            code += (ch - '0') * Math.pow(16, 3 - i);
        }

        return (char) code;
    }

    private Token lexLiteral(String literal, Token token) throws IOException {
        for (int i = 1; i < literal.length(); i++) {
            var ch = scanner.consume().orElseThrow(UnexpectedEotException::new);
            if (ch != literal.charAt(i)) {
                throw new UnexpectedCharacterException(ch);
            }
        }

        return token;
    }
}
