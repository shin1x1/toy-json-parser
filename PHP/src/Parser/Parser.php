<?php

declare(strict_types=1);

namespace Shin1x1\ToyJsonParser\Parser;

use Shin1x1\ToyJsonParser\Lexer\Lexer;
use Shin1x1\ToyJsonParser\Lexer\Token\EofToken;
use Shin1x1\ToyJsonParser\Parser\Exception\ParserException;

final class Parser
{
    public function __construct(private readonly Lexer $lexer)
    {
    }

    public function parse(): array|string|int|float|bool|null
    {
        $ret = ValueParser::parse($this->lexer, $this->lexer->getNextToken());

        if ($this->lexer->getNextToken() instanceof EofToken) {
            return $ret;
        }

        throw new ParserException(message: 'Unparsed tokens detected');
    }
}

