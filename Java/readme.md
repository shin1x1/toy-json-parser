# Java toy json parser

A toy json parser implementation in Java 17.

## Usage

* Read from stdin

```shell
$ echo '{"a":100,"b":[true,"helloあ😊",23e10]}' |  ./gradlew run

> Task :app:run
Object[value={a=Number[value=100], b=Array[value=[True[], String[value=helloあ😊], Number[value=2.3E+11]]]}]

BUILD SUCCESSFUL in 756ms
2 actionable tasks: 1 executed, 1 up-to-date
```

* Read from JSON File

```shell
$ cat json
{"a":100,"b":[true,"helloあ😊",23e10]}

./gradlew run --args=`pwd`/json                                                                                                               ✘ 1

> Task :app:run
Object[value={a=Number[value=100], b=Array[value=[True[], String[value=helloあ😊], Number[value=2.3E+11]]]}]

BUILD SUCCESSFUL in 702ms
2 actionable tasks: 1 executed, 1 up-to-date
```
