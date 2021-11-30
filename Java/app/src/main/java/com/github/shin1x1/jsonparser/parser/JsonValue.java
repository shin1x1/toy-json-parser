package com.github.shin1x1.jsonparser.parser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public sealed interface JsonValue {
    record Null() implements JsonValue {
    }

    record True() implements JsonValue {
    }

    record False() implements JsonValue {
    }

    record Number(BigDecimal value) implements JsonValue {
        public Number(java.lang.String value) {
            this(new BigDecimal(value));
        }
    }

    record String(java.lang.String value) implements JsonValue {
    }

    record Array(List<JsonValue> value) implements JsonValue {
    }

    record Object(Map<java.lang.String, JsonValue> value) implements JsonValue {
    }
}
