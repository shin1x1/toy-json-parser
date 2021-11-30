package com.github.shin1x1.jsonparser.lexer;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class Scanner {
    @Nonnull
    private final InputStreamReader reader;
    private int current;

    public Scanner(@Nonnull InputStream input) throws IOException {
        this.reader = new InputStreamReader(input);
        this.current = read();
    }

    public Scanner(@Nonnull String json) throws IOException {
        this(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
    }

    private int read() throws IOException {
        return reader.read();
    }

    public Optional<Character> peek() {
        return isEot() ? Optional.empty() : Optional.of((char) current);
    }

    public Optional<Character> consume() throws IOException {
        var ch = peek();

        if (!isEot()) {
            current = read();
        }

        return ch;
    }

    public boolean isEot() {
        return current == -1;
    }
}
