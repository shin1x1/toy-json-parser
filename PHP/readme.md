# PHP 8.1 toy json parser

Toy json parser was built in pure PHP 8.1. This repo is to learn that writing the parser with PHP.

You should use `json_decode()` to develop the web application :)

## Usage

```php
$ make
```

## Run parser

```php
$ echo '{"a":100,"b":[true,"hello",23e10]}' | make run
docker compose run --rm php-ci php parser.php
Creating php_php-ci_run ... done
array(2) {
  ["a"]=>
  int(100)
  ["b"]=>
  array(3) {
    [0]=>
    bool(true)
    [1]=>
    string(5) "hello"
    [2]=>
    float(230000000000)
  }
}
```

## Run test

```php
$ make test
```