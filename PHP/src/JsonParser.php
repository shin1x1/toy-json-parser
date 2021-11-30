<?php

declare(strict_types=1);

namespace Shin1x1\ToyJsonParser;

final class JsonParser
{
    public function parse(string $json): array|string|int|float|bool|null
    {
        $lexer = new Lexer\Lexer($json);
        $parser = new Parser\Parser($lexer);

        return $parser->parse();
    }
}