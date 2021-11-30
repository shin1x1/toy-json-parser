# Scala 3 toy json parser

A toy json parser implementation in Scala 3.

## Usage

* Read from stdin

```shell
$ echo '{"a":100,"b":[true,"helloあ😊",23e10]}' |  scala3 target/scala-3.1.0/toy-json-parser-with-scala-3_3-0.1.0-SNAPSHOT.jar
Success(Object(Map(a -> Number(100.0), b -> Array(List(True, String(helloあ😊), Number(2.3E11))))))
```

* Read from JSON File

```shell
$ cat json
{"a":100,"b":[true,"helloあ😊",23e10]}

$ scala3 target/scala-3.1.0/toy-json-parser-with-scala-3_3-0.1.0-SNAPSHOT.jar json
Success(Object(Map(a -> Number(100.0), b -> Array(List(True, String(helloあ😊), Number(2.3E11))))))
```
