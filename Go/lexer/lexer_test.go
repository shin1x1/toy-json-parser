package lexer

import (
	"fmt"
	"reflect"
	"testing"
)

func TestLexer_GetNextToken(t *testing.T) {
	sut := NewLexer(NewScanner("[]{}:,123,true,false,null,\"\\\"あa\""))

	tests := []struct {
		want *Token
	}{
		{
			NewToken(TokenLeftBracket),
		},
		{
			NewToken(TokenRightBracket),
		},
		{
			NewToken(TokenLeftBrace),
		},
		{
			NewToken(TokenRightBrace),
		},
		{
			NewToken(TokenColon),
		},
		{
			NewToken(TokenComma),
		},
		{
			NewTokenNumber(123),
		},
		{
			NewToken(TokenComma),
		},
		{
			NewToken(TokenTrue),
		},
		{
			NewToken(TokenComma),
		},
		{
			NewToken(TokenFalse),
		},
		{
			NewToken(TokenComma),
		},
		{
			NewToken(TokenNull),
		},
		{
			NewToken(TokenComma),
		},
		{
			NewTokenString("\"あa"),
		},
	}
	for i, tt := range tests {
		t.Run(fmt.Sprintf("%d", i), func(t *testing.T) {
			got, err := sut.GetNextToken()
			if !reflect.DeepEqual(got, tt.want) || err != nil {
				t.Errorf("GetNextToken() got = %v, want %v", got, tt.want)
			}
		})
	}

	_, err := sut.GetNextToken()
	if _, ok := err.(EotError); !ok {
		t.Errorf("GetNextToken() should be EOT")
	}
}

func TestLexer_GetNextToken_lexCodepoint(t *testing.T) {
	sut := NewLexer(NewScanner("\"\\\"\\/\\b\\f\\n\\t\\u3042\""))

	want := NewTokenString("\"/\b\f\n\tあ")
	got, err := sut.GetNextToken()
	if !reflect.DeepEqual(got, want) || err != nil {
		t.Errorf("GetNextToken() got = %v, want %v", got, want)
	}

	_, err = sut.GetNextToken()
	if _, ok := err.(EotError); !ok {
		t.Errorf("GetNextToken() should be EOT")
	}
}
