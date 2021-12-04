package lexer

import "testing"

func TestScanner_peek(t *testing.T) {
	var got, want rune
	var err error

	sut := NewScanner("12")

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

	got, err = sut.peek()
	if err == nil {
		t.Errorf("peek() error got = %v", err)
	}

	got, err = sut.consume()
	if err == nil {
		t.Errorf("peek() error got = %v", err)
	}
}
