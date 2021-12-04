package main

import (
	"bufio"
	"fmt"
	"github.com/shin1x1/toy-json-parser/Go/lexer"
	"github.com/shin1x1/toy-json-parser/Go/parser"
	"io"
	"math"
	"os"
)

func main() {
	var reader io.Reader

	if len(os.Args) <= 1 {
		reader = os.Stdin
	} else {
		f, err := os.Open(os.Args[1])
		if err != nil {
			panic(err)
		}

		reader = bufio.NewReader(f)
	}

	p := parser.NewParser(lexer.NewLexer(lexer.NewScanner(reader)))
	v, err := p.Parse()
	if err != nil {
		panic(err)
	}

	fmt.Println(dump(v))
}

func dump(v *parser.JsonValue) string {
	output := ""
	switch v.Type() {
	case parser.ValueTypeArray:
		output = ""
		for _, e := range v.Array() {
			if len(output) > 0 {
				output = output + ","
			}
			output = output + dump(e)
		}
		output = "[" + output + "]"
	case parser.ValueTypeObject:
		output = ""
		for k, e := range v.Object() {
			if len(output) > 0 {
				output = output + ","
			}
			output = output + fmt.Sprintf("\"%s\":%s", k, dump(e))
		}
		output = "{" + output + "}"
	case parser.ValueTypeTrue:
		output = "true"
	case parser.ValueTypeFalse:
		output = "false"
	case parser.ValueTypeNull:
		output = "null"
	case parser.ValueTypeString:
		output = fmt.Sprintf("\"%s\"", v.String())
	case parser.ValueTypeNumber:
		output = dumpNumber(v.Number())
	}

	return output
}

func dumpNumber(f float64) string {
	if f == math.Trunc(f) {
		return fmt.Sprintf("%d", int(f))
	}

	return fmt.Sprintf("%f", f)
}
