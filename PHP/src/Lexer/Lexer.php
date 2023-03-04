<?php

declare(strict_types=1);

namespace Shin1x1\ToyJsonParser\Lexer;

use Shin1x1\ToyJsonParser\Lexer\Exception\LexerException;
use Shin1x1\ToyJsonParser\Lexer\Token\ColonToken;
use Shin1x1\ToyJsonParser\Lexer\Token\CommaToken;
use Shin1x1\ToyJsonParser\Lexer\Token\EofToken;
use Shin1x1\ToyJsonParser\Lexer\Token\FalseToken;
use Shin1x1\ToyJsonParser\Lexer\Token\LeftCurlyBracketToken;
use Shin1x1\ToyJsonParser\Lexer\Token\LeftSquareBracketToken;
use Shin1x1\ToyJsonParser\Lexer\Token\NullToken;
use Shin1x1\ToyJsonParser\Lexer\Token\NumberToken;
use Shin1x1\ToyJsonParser\Lexer\Token\RightCurlyBracketToken;
use Shin1x1\ToyJsonParser\Lexer\Token\RightSquareBracketToken;
use Shin1x1\ToyJsonParser\Lexer\Token\StringToken;
use Shin1x1\ToyJsonParser\Lexer\Token\Token;
use Shin1x1\ToyJsonParser\Lexer\Token\TrueToken;

final class Lexer
{
    private int $length;
    private int $position;

    public function __construct(private readonly string $json)
    {
        $this->length = strlen($this->json);
        $this->position = 0;
    }

    public function getNextToken(): Token
    {
        do {
            $ch = $this->consume();
            if ($ch === null) {
                return new EofToken();
            }
        } while ($this->isSkipCharacter($ch));

        return match ($ch) {
            '[' => new LeftSquareBracketToken(),
            ']' => new RightSquareBracketToken(),
            '{' => new LeftCurlyBracketToken(),
            '}' => new RightCurlyBracketToken(),
            ':' => new ColonToken(),
            ',' => new CommaToken(),
            '"' => $this->getStringToken(),
            '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' => $this->getNumberToken($ch),
            't' => $this->getLiteralToken('true', TrueToken::class),
            'f' => $this->getLiteralToken('false', FalseToken::class),
            'n' => $this->getLiteralToken('null', NullToken::class),
            default => throw new LexerException('Invalid character ' . $ch),
        };
    }

    private function isSkipCharacter(?string $ch): bool
    {
        return $ch === ' ' || $ch === "\n" || $ch === "\r" || $ch === "\t";
    }

    private function getStringToken(): StringToken
    {
        $str = '';

        while (($ch = $this->consume()) !== null) {
            if ($ch === '"') {
                return new StringToken($str);
            }

            if ($ch !== '\\') {
                $str .= $ch;
                continue;
            }

            $str .= match ($ch = $this->consume()) {
                '"' => '"',
                '\\' => '\\',
                '/' => '/',
                'b' => chr(0x8),
                'f' => "\f",
                'n' => "\n",
                'r' => "\r",
                't' => "\t",
                'u' => $this->getCharacterByCodePoint(),
                default => '\\' . $ch,
            };
        }

        throw new LexerException('No end of string');
    }

    private function getCharacterByCodePoint(): string
    {
        $codepoint = '';
        for ($i = 0; $i < 4; $i++) {
            $ch = $this->consume();
            if ($ch !== null
                && ('0' <= $ch && $ch <= '9'
                    || 'A' <= $ch && $ch <= 'F'
                    || 'a' <= $ch && $ch <= 'f')) {
                $codepoint .= $ch;
                continue;
            }

            throw new LexerException('Invalid code point');
        }

        return mb_chr((int)hexdec($codepoint));
    }

    /**
     * @see https://github.com/shin1x1/php8-toy-json-parser/blob/master/diagrams/number.png
     */
    private function getNumberToken(string $ch): NumberToken
    {
        $number = $ch;
        $state = match ($ch) {
            '-' => 'MINUS',
            '0' => 'INT_ZERO',
            default => 'INT',
        };
        $isFloat = false;

        $isDigit19 = fn($ch) => '1' <= $ch && $ch <= '9';
        $isDigit = fn($ch) => '0' <= $ch && $ch <= '9';
        $isExp = fn($ch) => $ch === 'e' || $ch === 'E';

        while (true) {
            $ch = $this->current();
            switch ($state) {
                case 'INT':
                    if ($isDigit($ch)) {
                        $number .= $this->consume();
                        break;
                    }

                    if ($ch === '.') {
                        $number .= $this->consume();
                        $state = 'DECIMAL_POINT';
                        break;
                    }

                    if ($isExp($ch)) {
                        $number .= $this->consume();
                        $state = 'EXP';
                        break;
                    }

                    break 2;
                case 'MINUS':
                    if ($isDigit19($ch)) {
                        $number .= $this->consume();
                        $state = 'INT';
                        break;
                    }

                    if ($ch === '0') {
                        $number .= $this->consume();
                        $state = 'INT_ZERO';
                        break;
                    }

                    break 2;
                case 'INT_ZERO':
                    if ($ch === '.') {
                        $number .= $this->consume();
                        $state = 'DECIMAL_POINT';
                        break;
                    }
                    if ($isDigit($ch)) {
                        throw new LexerException('Invalid number:' . $ch);
                    }

                    break 2;
                case 'DECIMAL_POINT':
                    $isFloat = true;
                    if ($isDigit($ch)) {
                        $number .= $this->consume();
                        $state = 'DECIMAL_POINT_INT';
                        break;
                    }

                    break 2;
                case 'DECIMAL_POINT_INT':
                    if ($isDigit($ch)) {
                        $number .= $this->consume();
                        break;
                    }

                    if ($isExp($ch)) {
                        $number .= $this->consume();
                        $state = 'EXP';
                        break;
                    }

                    break 2;
                case 'EXP':
                    $isFloat = true;
                    if ($isDigit($ch) || $ch === '-' || $ch === '+') {
                        $number .= $this->consume();
                        $state = 'EXP_INT';
                        break;
                    }

                    break 2;
                case 'EXP_INT':
                    if ($isDigit($ch)) {
                        $number .= $this->consume();
                        break;
                    }

                    break 2;
                default:
                    break 2;
            }
        }

        $lastCh = $number[strlen($number) - 1];
        if ('0' <= $lastCh && $lastCh <= '9') {
            return new NumberToken($isFloat ? (float)$number : (int)$number);
        }

        throw new LexerException('Invalid number:' . $ch);
    }

    /**
     * @template T of TrueToken|FalseToken|NullToken
     *
     * @param string $expectedName
     * @param class-string<T> $klass
     * @return T
     * @throws LexerException
     */
    private function getLiteralToken(string $expectedName, string $klass): TrueToken|FalseToken|NullToken
    {
        $name = $expectedName[0];

        for ($i = 1; $i < strlen($expectedName); $i++) {
            $ch = $this->consume();

            if ($ch === null) {
                throw new LexerException('Unexpected end of text');
            }

            $name .= $ch;
        }

        if ($name !== $expectedName) {
            throw new LexerException('Unexpected literal ' . $name);
        }

        return new $klass;
    }

    private function current(): string
    {
        return $this->json[$this->position] ?? '';
    }

    private function consume(): ?string
    {
        if ($this->length <= $this->position) {
            return null;
        }

        $ch = $this->current();
        $this->position++;

        return $ch;
    }
}
