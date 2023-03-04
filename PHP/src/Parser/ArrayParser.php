<?php

declare(strict_types=1);

namespace Shin1x1\ToyJsonParser\Parser;

use Shin1x1\ToyJsonParser\Lexer\Exception\LexerException;
use Shin1x1\ToyJsonParser\Lexer\Lexer;
use Shin1x1\ToyJsonParser\Lexer\Token\CommaToken;
use Shin1x1\ToyJsonParser\Lexer\Token\EofToken;
use Shin1x1\ToyJsonParser\Lexer\Token\RightSquareBracketToken;
use Shin1x1\ToyJsonParser\Parser\Exception\ParserException;

final class ArrayParser
{
    private const STATE_START = 'START';
    private const STATE_COMMA = 'COMMA';
    private const STATE_VALUE = 'VALUE';

    /**
     * @see https://github.com/shin1x1/php8-toy-json-parser/blob/master/diagrams/array_parser.png
     * @return array<int, mixed>
     */
    public static function parse(Lexer $lexer): array
    {
        $array = [];
        $state = self::STATE_START;

        while (true) {
            $token = $lexer->getNextToken();
            if ($token instanceof EofToken) {
                break;
            }

            switch ($state) {
                case self::STATE_START:
                    if ($token instanceof RightSquareBracketToken) {
                        return $array;
                    }
                    $array[] = ValueParser::parse($lexer, $token);
                    $state = self::STATE_VALUE;
                    break;
                case self::STATE_VALUE:
                    if ($token instanceof RightSquareBracketToken) {
                        return $array;
                    }
                    if ($token instanceof CommaToken) {
                        $state = self::STATE_COMMA;
                        break;
                    }
                    throw new ParserException(token: $token);
                case self::STATE_COMMA:
                    $array[] = ValueParser::parse($lexer, $token);
                    $state = self::STATE_VALUE;
                    break;
                default:
                    throw new ParserException(token: $token);
            }
        }

        throw new ParserException(message: 'No end of array');
    }
}

