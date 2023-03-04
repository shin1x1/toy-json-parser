<?php

declare(strict_types=1);

namespace Shin1x1\ToyJsonParser\Parser;

use Shin1x1\ToyJsonParser\Lexer\Lexer;
use Shin1x1\ToyJsonParser\Lexer\Token\ColonToken;
use Shin1x1\ToyJsonParser\Lexer\Token\CommaToken;
use Shin1x1\ToyJsonParser\Lexer\Token\EofToken;
use Shin1x1\ToyJsonParser\Lexer\Token\RightCurlyBracketToken;
use Shin1x1\ToyJsonParser\Lexer\Token\StringToken;
use Shin1x1\ToyJsonParser\Parser\Exception\ParserException;

final class ObjectParser
{
    private const STATE_START = 'START';
    private const STATE_KEY = 'KEY';
    private const STATE_COLON = 'COLON';
    private const STATE_COMMA = 'COMMA';
    private const STATE_VALUE = 'VALUE';

    /**
     * @see https://github.com/shin1x1/php8-toy-json-parser/blob/master/diagrams/object_parser.png
     * @return array<string, mixed>
     */
    public static function parse(Lexer $lexer): array
    {
        $array = [];
        $key = '';
        $state = self::STATE_START;

        while (true) {
            $token = $lexer->getNextToken();
            if ($token instanceof EofToken) {
                break;
            }

            switch ($state) {
                case self::STATE_START:
                    if ($token instanceof RightCurlyBracketToken) {
                        return $array;
                    }
                    if ($token instanceof StringToken) {
                        $key = $token->getValue();
                        $state = self::STATE_KEY;
                        break;
                    }
                    throw new ParserException(token: $token);
                case self::STATE_KEY:
                    if ($token instanceof ColonToken) {
                        $state = self::STATE_COLON;
                        break;
                    }
                    throw new ParserException(token: $token);
                case self::STATE_COLON:
                    $array[$key] = ValueParser::parse($lexer, $token);
                    $state = self::STATE_VALUE;
                    break;
                case self::STATE_VALUE:
                    if ($token instanceof RightCurlyBracketToken) {
                        return $array;
                    }
                    if ($token instanceof CommaToken) {
                        $state = self::STATE_COMMA;
                        break;
                    }
                    throw new ParserException(token: $token);
                case self::STATE_COMMA:
                    if ($token instanceof StringToken) {
                        $key = $token->getValue();
                        $state = self::STATE_KEY;
                        break;
                    }
                    throw new ParserException(token: $token);
                default:
                    throw new ParserException(token: $token);
            }
        }

        throw new ParserException(message: 'No end of object');
    }
}

