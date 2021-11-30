use crate::lexer::Lexer;
use crate::parser::Parser;
use std::io::Read;

mod lexer;
mod parser;

fn main() {
    let mut buffer = String::new();
    let mut stdin = std::io::stdin();

    match stdin.read_to_string(&mut buffer) {
        Ok(_) => (),
        Err(e) => panic!("{:?}", e),
    }

    let mut parser = Parser::new(Lexer::new(&buffer));
    match parser.parse() {
        Ok(json) => println!("{:?}", json),
        Err(e) => panic!("{:?}", e),
    }
}
