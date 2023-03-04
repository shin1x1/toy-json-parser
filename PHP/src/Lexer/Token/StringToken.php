<?php

declare(strict_types=1);

namespace Shin1x1\ToyJsonParser\Lexer\Token;

final class StringToken implements Token
{
    public function __construct(private readonly string $value)
    {
    }

    public function getValue(): string
    {
        return $this->value;
    }
}
