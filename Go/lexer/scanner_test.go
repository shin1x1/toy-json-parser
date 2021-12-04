package lexer

import (
	"io"
	"testing"
)

func TestScanner_peek(t *testing.T) {
	var got, want rune
	var err error

	sut := NewScannerString("12")

	got, err = sut.peek()
	want = '1'
	if got != want || err != nil {
		t.Errorf("peek() got = %v, want %v", got, want)
	}

	got, err = sut.consume()
	want = '1'
	if got != want || err != nil {
		t.Errorf("peek() got = %v, want %v", got, want)
	}

	got, err = sut.peek()
	want = '2'
	if got != want || err != nil {
		t.Errorf("peek() got = %v, want %v", got, want)
	}

	got, err = sut.consume()
	want = '2'
	if got != want || err != nil {
		t.Errorf("peek() got = %v, want %v", got, want)
	}

	_, err = sut.peek()
	if err != io.EOF {
		t.Errorf("peek() should be EotError, but got = %v", err)
	}

	_, err = sut.consume()
	if err != io.EOF {
		t.Errorf("consume() should be EotError, but got = %v", err)
	}
}
