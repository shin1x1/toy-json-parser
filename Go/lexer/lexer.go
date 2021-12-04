package lexer

import (
	"fmt"
	"strconv"
	"strings"
	"unicode/utf16"
)

type Lexer struct {
	scanner *Scanner
}

func NewLexer(scanner *Scanner) *Lexer {
	return &Lexer{scanner: scanner}
}

func NewLexerWithString(s string) *Lexer {
	return &Lexer{scanner: NewScanner(s)}
}

func (l *Lexer) GetNextToken() (*Token, error) {
	r, err := l.scanner.consume()
	if err != nil {
		return nil, err
	}

	if r == ' ' || r == '\r' || r == '\t' || r == '\n' {
		return l.GetNextToken()
	}

	switch r {
	case '[':
		return NewToken(TokenLeftBracket), nil
	case ']':
		return NewToken(TokenRightBracket), nil
	case '{':
		return NewToken(TokenLeftBrace), nil
	case '}':
		return NewToken(TokenRightBrace), nil
	case ':':
		return NewToken(TokenColon), nil
	case ',':
		return NewToken(TokenComma), nil
	case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9':
		return l.lexNumber(r)
	case '"':
		return l.lexString()
	case 't':
		return l.lexToken("true", TokenTrue)
	case 'f':
		return l.lexToken("false", TokenFalse)
	case 'n':
		return l.lexToken("null", TokenNull)
	}

	return nil, fmt.Errorf("Unexpected character:%c", r)
}

func (l *Lexer) lexNumber(first rune) (*Token, error) {
	rs := []rune{first}
	numerics := "0123456789-+e."

	for {
		r, err := l.scanner.peek()
		if err != nil {
			break
		}

		if !strings.Contains(numerics, string(r)) {
			break
		}

		_, _ = l.scanner.consume()
		rs = append(rs, r)
	}

	f, err := strconv.ParseFloat(string(rs), 64)
	if err != nil {
		return nil, err
	}
	return NewTokenNumber(f), nil
}

func (l *Lexer) lexString() (*Token, error) {
	rs := []rune{}
	backslash := false

	for {
		r, err := l.scanner.consume()
		if err != nil {
			return nil, err
		}

		if !backslash {
			if r == '"' {
				return NewTokenString(string(rs)), nil
			}

			if r == '\\' {
				backslash = true
				continue
			}

			backslash = false
			rs = append(rs, r)
		} else {
			backslash = false

			switch r {
			case '"':
				rs = append(rs, '"')
			case '\\':
				rs = append(rs, '\\')
			case '/':
				rs = append(rs, '/')
			case 'b':
				rs = append(rs, '\b')
			case 'f':
				rs = append(rs, '\f')
			case 'n':
				rs = append(rs, '\n')
			case 'r':
				rs = append(rs, '\r')
			case 't':
				rs = append(rs, '\t')
			case 'u':
				ur, err := l.lexCodepoint()
				if err != nil {
					return nil, err
				}

				rs = append(rs, ur)
			default:
				return nil, fmt.Errorf("unexpected character:%c", r)
			}
		}
	}
}

func (l *Lexer) lexCodepoint() (rune, error) {
	cp := uint16(0)
	for i := 0; i < 4; i++ {
		r, err := l.scanner.consume()
		if err != nil {
			return 0, err
		}

		if !isHex(r) {
			return 0, fmt.Errorf("invalid codepoint:%c", r)
		}

		cp = cp + uint16(int(r-'0')*pow(16, 3-i))
	}

	rs := utf16.Decode([]uint16{cp})

	return rs[0], nil
}

func isHex(r rune) bool {
	return strings.IndexRune("0123456789ABCDEF", r) != -1
}

func pow(x, y int) int {
	r := 1
	for i := 0; i < y; i++ {
		r = r * x
	}

	return r
}

func (l *Lexer) lexToken(literal string, tokenType TokenType) (*Token, error) {
	lrs := []rune(literal)[1:]
	for _, lr := range lrs {
		r, err := l.scanner.consume()
		if err != nil {
			return nil, err
		}

		if r != lr {
			return nil, fmt.Errorf("unexpected literal:%c", r)
		}
	}

	return NewToken(tokenType), nil
}
