package lexer

type TokenType int8

const (
	_ TokenType = iota
	TokenLeftBracket
	TokenRightBracket
	TokenLeftBrace
	TokenRightBrace
	TokenColon
	TokenComma
	TokenNull
	TokenTrue
	TokenFalse
	TokenNumber
	TokenString
)

type Token struct {
	tokenType   TokenType
	stringValue string
	numberValue float64
}

func NewToken(tokenType TokenType) *Token {
	return &Token{tokenType: tokenType}
}

func NewTokenString(value string) *Token {
	return &Token{tokenType: TokenString, stringValue: value}
}

func NewTokenNumber(value float64) *Token {
	return &Token{tokenType: TokenNumber, numberValue: value}
}

func (t Token) Type() TokenType {
	return t.tokenType
}

func (t Token) Number() float64 {
	return t.numberValue
}

func (t Token) String() string {
	return t.stringValue
}
