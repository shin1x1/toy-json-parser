package lexer

import "fmt"

type Scanner struct {
	json     []rune
	position int
}

func NewScanner(json string) *Scanner {
	return &Scanner{json: []rune(json)}
}

func (s *Scanner) peek() (rune, error) {
	if s.isEot() {
		return 0, EotError{}
	}

	return s.json[s.position], nil
}

func (s *Scanner) consume() (rune, error) {
	c, err := s.peek()
	if err != nil {
		return c, err
	}

	s.position = s.position + 1

	return c, nil
}

func (s *Scanner) isEot() bool {
	return s.position >= len(s.json)
}

type EotError struct {
}

func (e EotError) Error() string {
	return fmt.Sprintf("end of string")
}
