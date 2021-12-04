package parser

import (
	"fmt"
	"github.com/shin1x1/toy-json-parser/Go/lexer"
	"reflect"
	"testing"
)

func TestParser_parse(t *testing.T) {
	json := "[true,false,null,\"a\",{\"key\": 1\n\r\t}]"
	sut := NewParser(lexer.NewLexer(lexer.NewScanner(json)))

	tests := []struct {
		want *JsonValue
	}{
		{
			NewJsonValueArray([]*JsonValue{
				NewJsonValue(ValueTypeTrue),
				NewJsonValue(ValueTypeFalse),
				NewJsonValue(ValueTypeNull),
				NewJsonValueString("a"),
				NewJsonValueObject(map[string]*JsonValue{
					"key": NewJsonValueNumber(1),
				}),
			}),
		},
	}
	for i, tt := range tests {
		t.Run(fmt.Sprintf("%d", i), func(t *testing.T) {
			got, err := sut.Parse()
			if !reflect.DeepEqual(got, tt.want) || err != nil {
				t.Errorf("GetNextToken() got = %+v, want %+v", got, tt.want)
			}
		})
	}

	_, err := sut.Parse()
	if _, ok := err.(lexer.EotError); !ok {
		t.Errorf("Parse() should return error")
	}
}
