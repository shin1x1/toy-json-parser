<?php
declare(strict_types=1);

use Shin1x1\ToyJsonParser\JsonParser;

require_once __DIR__ . '/vendor/autoload.php';

$json = file_get_contents('php://stdin');

$parser = new JsonParser();
var_dump($parser->parse($json));