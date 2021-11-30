# Rust Toy Json Parser

A toy json parser implementation in Rust.

## Usage

```shell
$ echo '{"a":100,"b":[true,"hello",23e10]}' | cargo run
Object({"a": Number(100.0), "b": Array([True, String("hello"), Number(230000000000.0)])})
```