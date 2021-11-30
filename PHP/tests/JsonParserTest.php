<?php

declare(strict_types=1);

namespace Shin1x1\ToyJsonParser\Test;

use PHPUnit\Framework\TestCase;
use Shin1x1\ToyJsonParser\JsonParser;
use Shin1x1\ToyJsonParser\Lexer\Exception\LexerException;
use Shin1x1\ToyJsonParser\Parser\Exception\ParserException;
use Throwable;

class JsonParserTest extends TestCase
{
    /**
     * @test
     * @dataProvider dataProvider
     * @param string $json
     */
    public function parse(string $json)
    {
        $sut = new JsonParser();

        $expected = json_decode($json, associative: true, flags: JSON_THROW_ON_ERROR);
        $this->assertSame($expected, $sut->parse($json));
    }

    public function dataProvider(): array
    {
        return [
            'test'                             => [
                '{
                    "key1": -100.1e+2,
                     "key2": "わ\"お",
                     "true":true,
                     "array":[123,20e10, -0],
                     "o": {
                        "a":1,
                        "b":[true,false,null]
                    }
                 }',
            ],
            'http://www.json.org/example.html' => [
                '{
                    "glossary": {
                        "title": "example glossary",
                        "GlossDiv": {
                            "title": "S",
                            "GlossList": {
                                "GlossEntry": {
                                    "ID": "SGML",
                                    "SortAs": "SGML",
                                    "GlossTerm": "Standard Generalized Markup Language",
                                    "Acronym": "SGML",
                                    "Abbrev": "ISO 8879:1986",
                                    "GlossDef": {
                                        "para": "A meta-markup language, used to create markup languages such as DocBook.",
                                        "GlossSeeAlso": ["GML", "XML"]
                                    },
                                    "GlossSee": "markup"
                                }
                            }
                        }
                    }
                }
                '
            ],
            'only literal'                     => [
                'true',
            ],
            'only number'                      => [
                '1',
            ],
            'only string'                      => [
                '"abc"',
            ],
            'empty string'                     => [
                '""',
            ],
            'empty array'                      => [
                '[]',
            ],
            'empty object'                     => [
                '{}',
            ],
        ];
    }

    /**
     * @test
     * @dataProvider dataProvider_error
     * @param string $json
     */
    public function parse_error(string $json)
    {
        try {
            json_decode($json, associative: true, flags: JSON_THROW_ON_ERROR);
            $this->assertTrue(false);
        } catch (Throwable) {
            // nop
        }

        try {
            $value = (new JsonParser())->parse($json);
            var_dump($value);
            $this->assertTrue(false);
        } catch (LexerException | ParserException) {
            $this->assertTrue(true);
        }
    }

    public function dataProvider_error()
    {
        return [
            [''],
            ['1.'],
            ['01'],
            ['1e'],
            ['1,2'],
            ['][1'],
            ['{"k1": 1, 2}'],
        ];
    }
}