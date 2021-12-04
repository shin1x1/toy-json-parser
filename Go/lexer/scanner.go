package lexer

import (
	"bufio"
	"fmt"
	"io"
	"strings"
	"unicode"
)

type Scanner struct {
	reader  *bufio.Reader
	current rune
	error   error
}

func NewScanner(reader io.Reader) *Scanner {
	s := &Scanner{reader: bufio.NewReader(reader)}
	s.read()

	return s
}

func NewScannerString(s string) *Scanner {
	return NewScanner(strings.NewReader(s))
}

func (s *Scanner) read() {
	r, size, err := s.reader.ReadRune()
	if s.current == unicode.ReplacementChar && size == 1 {
		err = fmt.Errorf("invalid unicode")
	}

	s.current = r
	s.error = err
}

func (s *Scanner) peek() (rune, error) {
	return s.current, s.error
}

func (s *Scanner) consume() (rune, error) {
	c, err := s.peek()
	if err != nil {
		return c, err
	}

	s.read()

	return c, nil
}

type EotError struct {
}

func (e EotError) Error() string {
	return fmt.Sprintf("end of string")
}
