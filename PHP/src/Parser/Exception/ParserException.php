<?php

declare(strict_types=1);

namespace Shin1x1\ToyJsonParser\Parser\Exception;

use Exception;
use Shin1x1\ToyJsonParser\Lexer\Token\NumberToken;
use Shin1x1\ToyJsonParser\Lexer\Token\StringToken;
use Shin1x1\ToyJsonParser\Lexer\Token\Token;

final class ParserException extends Exception
{
    public function __construct(Token $token = null, string $message = 'Syntax error')
    {
        if ($token instanceof Token) {
            if ($token instanceof StringToken || $token instanceof NumberToken) {
                $message = sprintf('%s type=%s value=%s', $message, $token::class, $token->getValue());
            } else {
                $message = sprintf('%s type=%s', $message, $token::class);
            }
        }

        parent::__construct($message);
    }
}
