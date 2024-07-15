<?php

declare(strict_types=1);

namespace Shin1x1\ToyJsonParser\Test\Lexer;

use PHPUnit\Framework\Attributes\DataProvider;
use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use Shin1x1\ToyJsonParser\Lexer\Exception\LexerException;
use Shin1x1\ToyJsonParser\Lexer\Lexer;
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

class LexerTest extends TestCase
{
    /**
     * @param Token[] $expected
     */
    #[Test]
    #[DataProvider('dataProvider')]
    public function getNextToken(array $expected, string $json): void
    {
        $sut = new Lexer($json);
        $tokens = [];
        while ($token = $sut->getNextToken()) {
            $tokens[] = $token;
            if ($token instanceof EofToken) {
                break;
            }
        }

        $this->assertEquals($expected, $tokens);
    }

    /**
     * @return array<string, array{0:Token[], 1:string}>
     */
    public static function dataProvider(): array
    {
        return [
            'example'          => [
                [
                    new LeftCurlyBracketToken(),
                    new StringToken('name'),
                    new ColonToken(),
                    new StringToken('John'),
                    new CommaToken(),
                    new StringToken('age'),
                    new ColonToken(),
                    new NumberToken(30),
                    new CommaToken(),
                    new StringToken('car'),
                    new ColonToken(),
                    new NullToken(),
                    new RightCurlyBracketToken(),
                    new EofToken(),
                ],
                '{ "name":"John", "age":30, "car":null }',
            ],
            'empty'            => [
                [
                    new EofToken(),
                ],
                '',
            ],
            'structural token' => [
                [
                    new LeftSquareBracketToken(),
                    new RightSquareBracketToken(),
                    new LeftCurlyBracketToken(),
                    new RightCurlyBracketToken(),
                    new ColonToken(),
                    new CommaToken(),
                    new EofToken(),
                ],
                '[]{}:,',
            ],
            'string'           => [
                [
                    new StringToken("あab\"\\/\f\n\r\tc\\z好"),
                    new EofToken(),
                ],
                '"あab\"\\\/\f\n\r\tc\z\u597D"'
            ],
            'number'           => [
                [
                    new NumberToken(123),
                    new LeftSquareBracketToken(),
                    new NumberToken(-100),
                    new CommaToken(),
                    new NumberToken(12310),
                    new CommaToken(),
                    new NumberToken(0.01),
                    new EofToken(),
                ],
                '123[-100,123.1e+2,10.0e-3'
            ],
            'literal'          => [
                [
                    new TrueToken(),
                    new CommaToken(),
                    new FalseToken(),
                    new CommaToken(),
                    new NullToken(),
                    new CommaToken(),
                    new EofToken(),
                ],
                'true,false,null,',
            ],
        ];
    }

    #[Test]
    public function getNextToken_error(): void
    {
        $this->expectException(LexerException::class);
        $this->expectExceptionMessage('Invalid character a');

        $sut = new Lexer("[true,\nabc]");
        while (!($sut->getNextToken() instanceof EofToken)) {
            $sut->getNextToken();
        }
    }

    #[Test]
    public function getNextToken_error_literal(): void
    {
        $this->expectException(LexerException::class);
        $this->expectExceptionMessage('Unexpected literal nul]');

        $sut = new Lexer("[nul]");
        while (!($sut->getNextToken() instanceof EofToken)) {
            $sut->getNextToken();
        }
    }

    #[Test]
    public function getNextToken_error_literal_end_of_text(): void
    {
        $this->expectException(LexerException::class);
        $this->expectExceptionMessage('Unexpected end of text');

        $sut = new Lexer("nul");
        while (!($sut->getNextToken() instanceof EofToken)) {
            $sut->getNextToken();
        }
    }
}