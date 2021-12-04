package parser

import (
	"fmt"
	"github.com/shin1x1/toy-json-parser/Go/lexer"
)

type UnexpectedToken struct {
	token *lexer.Token
}

func (u UnexpectedToken) Error() string {
	return fmt.Sprintf("unexpected token: %+v", u.token)
}

type Parser struct {
	lexer *lexer.Lexer
}

func NewParser(lexer *lexer.Lexer) *Parser {
	return &Parser{lexer: lexer}
}

func (p *Parser) Parse() (*JsonValue, error) {
	t, err := p.lexer.GetNextToken()
	if err != nil {
		return nil, err
	}

	return p.parseValue(t)
}

func (p *Parser) parseValue(t *lexer.Token) (*JsonValue, error) {
	switch t.Type() {
	case lexer.TokenTrue:
		return NewJsonValue(ValueTypeTrue), nil
	case lexer.TokenFalse:
		return NewJsonValue(ValueTypeFalse), nil
	case lexer.TokenNull:
		return NewJsonValue(ValueTypeNull), nil
	case lexer.TokenNumber:
		return NewJsonValueNumber(t.Number()), nil
	case lexer.TokenString:
		return NewJsonValueString(t.String()), nil
	case lexer.TokenLeftBracket:
		return p.parseArray()
	case lexer.TokenLeftBrace:
		return p.parseObject()
	default:
		return nil, UnexpectedToken{token: t}
	}
}

type arrayState = int

const (
	_ arrayState = iota
	ArrayDefault
	ArrayValue
	ArrayComma
)

func (p *Parser) parseArray() (*JsonValue, error) {
	var array []*JsonValue
	state := ArrayDefault

	for {
		t, err := p.lexer.GetNextToken()
		if err != nil {
			return nil, err
		}

		switch state {
		case ArrayDefault:
			if t.Type() == lexer.TokenRightBracket {
				return NewJsonValueArray(array), nil
			}

			v, err := p.parseValue(t)
			if err != nil {
				return nil, err
			}

			array = append(array, v)
			state = ArrayValue
		case ArrayValue:
			if t.Type() == lexer.TokenRightBracket {
				return NewJsonValueArray(array), nil
			}
			if t.Type() != lexer.TokenComma {
				return nil, UnexpectedToken{token: t}
			}

			state = ArrayComma
		case ArrayComma:
			v, err := p.parseValue(t)
			if err != nil {
				return nil, err
			}

			array = append(array, v)
			state = ArrayValue
		}
	}
}

type objectState = int

const (
	_ objectState = iota
	ObjectDefault
	ObjectKey
	ObjectColon
	ObjectValue
	ObjectComma
)

func (p *Parser) parseObject() (*JsonValue, error) {
	m := map[string]*JsonValue{}
	state := ObjectDefault
	key := ""

	for {
		t, err := p.lexer.GetNextToken()
		if err != nil {
			return nil, err
		}

		switch state {
		case ObjectDefault:
			if t.Type() == lexer.TokenRightBrace {
				return NewJsonValueObject(m), nil
			}
			if t.Type() != lexer.TokenString {
				return nil, UnexpectedToken{token: t}
			}

			key = t.String()
			state = ObjectKey
		case ObjectKey:
			if t.Type() != lexer.TokenColon {
				return nil, UnexpectedToken{token: t}
			}

			state = ObjectColon
		case ObjectColon:
			v, err := p.parseValue(t)
			if err != nil {
				return nil, err
			}

			if key == "" {
				return nil, fmt.Errorf("invalid key")
			}

			m[key] = v
			state = ObjectValue
		case ObjectValue:
			if t.Type() == lexer.TokenRightBrace {
				return NewJsonValueObject(m), nil
			}
			if t.Type() != lexer.TokenComma {
				return nil, UnexpectedToken{token: t}
			}

			state = ObjectComma
		case ObjectComma:
			v, err := p.parseValue(t)
			if err != nil {
				return nil, err
			}

			if key == "" {
				return nil, fmt.Errorf("invalid key")
			}

			m[key] = v
			state = ObjectValue
		}
	}
}
