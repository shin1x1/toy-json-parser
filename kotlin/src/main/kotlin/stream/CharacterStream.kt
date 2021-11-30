package stream

import java.io.InputStream

class CharacterStream(inputStream: InputStream) {
    private val reader = inputStream.reader()
    private var current: Int = read()
    private val readText = StringBuilder()

    var position: Int = 0
        private set

    companion object {
        operator fun invoke(json: String): CharacterStream {
            return CharacterStream(json.byteInputStream())
        }
    }

    private fun read(): Int = reader.read()

    fun peek(): Result<Char> {
        return when (isEot()) {
            true -> Result.failure(EotException())
            false -> Result.success(current.toChar())
        }
    }

    fun consume(): Result<Char> {
        val ch = peek()

        ch.onSuccess {
            current = read()
            readText.append(it)
            position++
        }

        return ch
    }

    fun isEot(): Boolean {
        return when (current) {
            -1 -> true
            else -> false
        }
    }

    fun readText() = readText.toString()
}
