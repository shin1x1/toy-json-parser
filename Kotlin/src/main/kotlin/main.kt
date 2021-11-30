import lexer.Lexer
import parser.Parser
import stream.CharacterStream
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val input = when (args.size) {
        0 -> System.`in`
        else -> File(args[0]).inputStream()
    }

    input.use {
        val lexer = Lexer(CharacterStream(it))
        val json = Parser(lexer).parse()

        println(json)

        json.onFailure { exitProcess(1) }
    }
}
