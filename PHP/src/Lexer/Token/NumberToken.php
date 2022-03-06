<?php

declare(strict_types=1);

namespace Shin1x1\ToyJsonParser\Lexer\Token;

use JetBrains\PhpStorm\Immutable;

final class NumberToken implements Token
{
    public function __construct(#[Immutable] private readonly int|float $value)
    {
    }

    public function getValue(): int|float
    {
        return $this->value;
    }
}
