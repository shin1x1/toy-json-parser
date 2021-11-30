<?php

declare(strict_types=1);

namespace Shin1x1\ToyJsonParser\Lexer\Exception;

use Exception;

final class LexerException extends Exception
{
    public function __construct(string $message)
    {
        parent::__construct($message);
    }
}
