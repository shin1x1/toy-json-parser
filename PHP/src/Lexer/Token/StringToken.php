<?php

declare(strict_types=1);

namespace Shin1x1\ToyJsonParser\Lexer\Token;

use JetBrains\PhpStorm\Immutable;

final class StringToken implements Token
{
    public function __construct(#[Immutable] private readonly string $value)
    {
    }

    public function getValue(): string
    {
        return $this->value;
    }
}
