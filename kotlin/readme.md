# Kotlin toy json parser

A toy json parser implementation in Kotlin.

## Usage

* Read from stdin

```shell
$ echo '{"a":100,"b":[true,"hello",23e10]}' |  ./gradlew run

> Task :run
Success(Object({a=Number(100.0), b=Array([true, String(hello), Number(2.3E11)])}))

BUILD SUCCESSFUL in 742ms
```

* Read from JSON File

```shell
$ cat json
{"a":100,"b":[true,"hello",23e10]}

$ ./gradlew run --args=json                                                                                                                       ✘ 1

> Task :run
Success(Object({a=Number(100.0), b=Array([true, String(hello), Number(2.3E11)])}))

BUILD SUCCESSFUL in 724ms
```
