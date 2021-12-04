# Go toy json parser

A toy json parser implementation in Go 1.17.

## Usage

* Read from stdin

```shell
$ echo '{"a":100,"b":[true,"helloあ😊",23e10]}' | go run main.go
{"a":100.000000,"b":[true,"helloあ😊",230000000000.000000]}
```

* Read from JSON File

```shell
$ cat json
{"a":100,"b":[true,"helloあ😊",23e10]}

$ go run main.go json
{"b":[true,"helloあ😊",230000000000.000000],"a":100.000000}
```
