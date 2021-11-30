# PHP 8.0 toy json parser

Toy json parser was built in pure PHP 8.0. This repo is to learn that writing the parser with PHP.

You should use `json_decode()` to develop the web application :)

## Usage

```php
$ make
```

## Run parser

```php
$ echo '[100,true,false]' | make run
docker-compose run --rm php-ci php parser.php
Creating php8-toy-json-parser-dev_php-ci_run ... done
array(3) {
  [0]=>
  int(100)
  [1]=>
  bool(true)
  [2]=>
  bool(false)
}
```

## Run test

```php
$ make test
```