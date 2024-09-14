package parser

sealed interface JsonValue {
    object True : JsonValue {
        override fun toString(): kotlin.String = "true"
    }

    object False : JsonValue {
        override fun toString(): kotlin.String = "false"
    }

    object Null : JsonValue {
        override fun toString(): kotlin.String = "null"
    }

    data class Number(
        val value: Double,
    ) : JsonValue {
        override fun toString(): kotlin.String = "Number($value)"
    }

    data class String(
        val value: kotlin.String,
    ) : JsonValue {
        override fun toString(): kotlin.String = "String($value)"
    }

    data class Array(
        val value: List<JsonValue>,
    ) : JsonValue {
        override fun toString(): kotlin.String = "Array($value)"
    }

    data class Object(
        val value: Map<kotlin.String, JsonValue>,
    ) : JsonValue {
        override fun toString(): kotlin.String = "Object($value)"
    }
}
