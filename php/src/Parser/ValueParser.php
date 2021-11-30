<?php

declare(strict_types=1);

namespace Shin1x1\ToyJsonParser\Parser;

use Shin1x1\ToyJsonParser\Lexer\Lexer;
use Shin1x1\ToyJsonParser\Lexer\Token\FalseToken;
use Shin1x1\ToyJsonParser\Lexer\Token\LeftCurlyBracketToken;
use Shin1x1\ToyJsonParser\Lexer\Token\LeftSquareBracketToken;
use Shin1x1\ToyJsonParser\Lexer\Token\NullToken;
use Shin1x1\ToyJsonParser\Lexer\Token\NumberToken;
use Shin1x1\ToyJsonParser\Lexer\Token\StringToken;
use Shin1x1\ToyJsonParser\Lexer\Token\Token;
use Shin1x1\ToyJsonParser\Lexer\Token\TrueToken;
use Shin1x1\ToyJsonParser\Parser\Exception\ParserException;

final class ValueParser
{
    public static function parse(Lexer $lexer, Token $token): array|string|int|float|bool|null
    {
        return match (true) {
            $token instanceof LeftSquareBracketToken => ArrayParser::parse($lexer),
            $token instanceof StringToken => $token->getValue(),
            $token instanceof NumberToken => $token->getValue(),
            $token instanceof TrueToken => true,
            $token instanceof FalseToken => false,
            $token instanceof NullToken => null,
            $token instanceof LeftCurlyBracketToken => ObjectParser::parse($lexer),
            default => throw new ParserException(token: $token),
        };
    }
}
