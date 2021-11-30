import lexer.Scanner
import parser.Parser

import java.io.FileInputStream
import scala.io.{Source, StdIn}

object Main:
  def main(args: Array[String]): Unit =
    val input = args.length match
      case 1 => System.in
      case _ => FileInputStream(args(1))

    println(Parser.parse(Scanner(input)))
