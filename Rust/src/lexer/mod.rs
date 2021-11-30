use std::char::decode_utf16;

#[derive(Debug, PartialEq)]
pub enum Token {
    LeftBrace,
    RightBrace,
    LeftBracket,
    RightBracket,
    Colon,
    Comma,
    String(Box<str>),
    Number(f64),
    Keyword(Box<str>),
}

#[derive(Debug)]
pub struct Lexer {
    chars: Vec<char>,
    position: usize,
}

#[derive(Debug, PartialEq)]
pub enum LexerError {
    InvalidCharacter(char),
    InvalidNumber,
    InvalidCodepoint(char),
    InvalidUnicode(Box<str>),
    InvalidKeyword(char),
    Eot,
}

impl Lexer {
    pub fn new(json: &str) -> Self {
        Self {
            chars: json.chars().collect(),
            position: 0,
        }
    }

    pub fn read_next_token(&mut self) -> Result<Token, LexerError> {
        let ch = self.next()?;

        match ch {
            '[' => Ok(Token::LeftBracket),
            ']' => Ok(Token::RightBracket),
            '{' => Ok(Token::LeftBrace),
            '}' => Ok(Token::RightBrace),
            ':' => Ok(Token::Colon),
            ',' => Ok(Token::Comma),
            ' ' | '\n' | '\r' | '\t' => self.read_next_token(),
            '-' | '0'..='9' => self.lex_number(ch),
            '"' => self.lex_string(),
            't' => self.lex_keyword("true"),
            'f' => self.lex_keyword("false"),
            'n' => self.lex_keyword("null"),
            _ => Err(LexerError::InvalidCharacter(ch)),
        }
    }

    pub fn is_eot(&self) -> bool {
        self.chars.len() <= self.position
    }

    fn current(&self) -> Result<char, LexerError> {
        if self.is_eot() {
            Err(LexerError::Eot)
        } else {
            Ok(self.chars[self.position])
        }
    }

    fn next(&mut self) -> Result<char, LexerError> {
        let ch = self.current();
        self.position += 1;
        ch
    }

    fn lex_number(&mut self, ch: char) -> Result<Token, LexerError> {
        let mut num = String::from(ch);

        enum State {
            InitZero,
            InitMinus,
            Digit,
            Point,
            PointDigit,
            Exp,
            ExpDigit,
        }

        let mut state: State = match ch {
            '-' => State::InitMinus,
            '0' => State::InitZero,
            _ => State::Digit,
        };

        loop {
            let ch = self.current();
            if ch.is_err() {
                break;
            }
            let ch = ch.unwrap();
            match state {
                State::InitMinus => match ch {
                    '1'..='9' => {
                        num.push(self.next()?);
                        state = State::Digit;
                    }
                    _ => break,
                },
                State::InitZero => match ch {
                    '.' => {
                        num.push(self.next()?);
                        state = State::Point;
                    }
                    'e' | 'E' => {
                        num.push(self.next()?);
                        state = State::Exp;
                    }
                    _ => break,
                },
                State::Digit => match ch {
                    '0'..='9' => {
                        num.push(self.next()?);
                        state = State::Digit;
                    }
                    'e' | 'E' => {
                        num.push(self.next()?);
                        state = State::Exp;
                    }
                    '.' => {
                        num.push(self.next()?);
                        state = State::Point;
                    }
                    _ => break,
                },
                State::Point => match ch {
                    '0'..='9' => {
                        num.push(self.next()?);
                        state = State::PointDigit;
                    }
                    _ => break,
                },
                State::PointDigit => match ch {
                    '0'..='9' => {
                        num.push(self.next()?);
                    }
                    'e' | 'E' => {
                        num.push(self.next()?);
                        state = State::Exp;
                    }
                    _ => break,
                },
                State::Exp => match ch {
                    '-' | '+' | '0'..='9' => {
                        num.push(self.next()?);
                        state = State::ExpDigit;
                    }
                    _ => break,
                },
                State::ExpDigit => match ch {
                    '0'..='9' => {
                        num.push(self.next()?);
                    }
                    _ => break,
                },
            }
        }

        if let Ok(n) = num.parse::<f64>() {
            Ok(Token::Number(n))
        } else {
            Err(LexerError::InvalidNumber)
        }
    }

    fn lex_string(&mut self) -> Result<Token, LexerError> {
        let mut string = String::from("");

        loop {
            let ch = self.next()?;
            if ch == '"' {
                return Ok(Token::String(string.into_boxed_str()));
            }

            if ch != '\\' {
                string.push(ch);
                continue;
            }

            let ch = self.next()?;
            match ch {
                '"' => string.push('"'),
                '\\' => string.push('\\'),
                '/' => string.push('/'),
                'b' => string.push('\x08'),
                'f' => string.push('\x0C'),
                'n' => string.push('\n'),
                'r' => string.push('\r'),
                't' => string.push('\t'),
                'u' => {
                    let mut codepoint: [u16; 4] = [0, 0, 0, 0];
                    let mut i = 0;
                    while i < 4 {
                        let ch = self.next()?;

                        if !ch.is_ascii_hexdigit() {
                            return Err(LexerError::InvalidCodepoint(ch));
                        }
                        codepoint[i] = ch as u16 - '0' as u16;
                        i += 1;
                    }

                    let code: u16 =
                        codepoint[0] << 12 | codepoint[1] << 8 | codepoint[2] << 4 | codepoint[3];

                    string.push(match decode_utf16(vec![code]).last().unwrap() {
                        Ok(c) => Ok(c as char),
                        Err(e) => Err(LexerError::InvalidUnicode(e.to_string().into_boxed_str())),
                    }?);
                }
                _ => string.push(ch),
            }
        }
    }

    fn lex_keyword(&mut self, keyword: &str) -> Result<Token, LexerError> {
        for k in keyword.chars().skip(1) {
            let ch = self.next()?;
            if ch != k {
                return Err(LexerError::InvalidKeyword(ch));
            }
        }

        Ok(Token::Keyword(keyword.to_owned().into_boxed_str()))
    }
}

#[test]
fn test_get_next_token() {
    let mut lexer = Lexer::new("[]\n{\r}123 1.0e+4\t:\"あ12\"true ,");
    assert_eq!(lexer.is_eot(), false);
    assert_eq!(lexer.read_next_token(), Ok(Token::LeftBracket));
    assert_eq!(lexer.read_next_token(), Ok(Token::RightBracket));
    assert_eq!(lexer.read_next_token(), Ok(Token::LeftBrace));
    assert_eq!(lexer.read_next_token(), Ok(Token::RightBrace));
    assert_eq!(lexer.read_next_token(), Ok(Token::Number(123.0)));
    assert_eq!(lexer.read_next_token(), Ok(Token::Number(10000.0)));
    assert_eq!(lexer.read_next_token(), Ok(Token::Colon));
    assert_eq!(
        lexer.read_next_token(),
        Ok(Token::String("あ12".to_owned().into_boxed_str()))
    );
    assert_eq!(
        lexer.read_next_token(),
        Ok(Token::Keyword("true".to_owned().into_boxed_str()))
    );
    assert_eq!(lexer.read_next_token(), Ok(Token::Comma));
    assert_eq!(lexer.is_eot(), true);
}

#[test]
fn test_lex_number() {
    let mut lexer = Lexer::new("123.e");
    assert_eq!(lexer.read_next_token(), Ok(Token::Number(123.0)));
    assert_eq!(
        lexer.read_next_token(),
        Err(LexerError::InvalidCharacter('e'))
    );

    let mut lexer = Lexer::new("01");
    assert_eq!(lexer.read_next_token(), Ok(Token::Number(0.0)));
    assert_eq!(lexer.read_next_token(), Ok(Token::Number(1.0)));
    assert_eq!(lexer.is_eot(), true);

    let mut lexer = Lexer::new("1e2");
    assert_eq!(lexer.read_next_token(), Ok(Token::Number(100.0)));
    assert_eq!(lexer.is_eot(), true);
}

#[test]
fn test_lex_string() {
    let mut lexer = Lexer::new(r#""123\u3042\r\n \t\"""#);
    assert_eq!(
        lexer.read_next_token(),
        Ok(Token::String("123あ\r\n \t\"".to_owned().into_boxed_str()))
    );
    assert_eq!(lexer.is_eot(), true);
}

#[test]
fn test_lex_string_invalid_codepoint() {
    let mut lexer = Lexer::new(r#""\u123z""#);
    assert_eq!(
        lexer.read_next_token(),
        Err(LexerError::InvalidCodepoint('z'))
    );
}

#[test]
fn test_lex_keyword() {
    let mut lexer = Lexer::new("true false null");
    assert_eq!(
        lexer.read_next_token(),
        Ok(Token::Keyword("true".to_owned().into_boxed_str()))
    );
    assert_eq!(
        lexer.read_next_token(),
        Ok(Token::Keyword("false".to_owned().into_boxed_str()))
    );
    assert_eq!(
        lexer.read_next_token(),
        Ok(Token::Keyword("null".to_owned().into_boxed_str()))
    );
    assert_eq!(lexer.is_eot(), true);
}
