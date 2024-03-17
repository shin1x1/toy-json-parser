package stream

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

class CharacterStreamTest {
    @Test
    fun consume() {
        val sut = CharacterStream("true")

        assertSame('t', sut.consume().getOrThrow())
        assertSame('r', sut.consume().getOrThrow())
        assertSame('u', sut.consume().getOrThrow())
        assertSame('e', sut.consume().getOrThrow())
        assertIs<Exception>(sut.consume().exceptionOrNull())
    }

    @Test
    fun peek() {
        val stream = "true".byteInputStream()
        val sut = CharacterStream(stream)

        assertSame('t', sut.peek().getOrThrow())
        assertSame('t', sut.peek().getOrThrow())
    }

    @Test
    fun isEot() {
        val sut = CharacterStream("a")

        assertFalse(sut.isEot())
        sut.consume()
        assertTrue(sut.isEot())
    }

    @Test
    fun position() {
        val sut = CharacterStream("ab")

        assertSame(0, sut.position)
        sut.consume()
        assertSame(1, sut.position)
        sut.consume()
        assertSame(2, sut.position)

        // If it is reached the eot, the position do not increment.
        sut.consume()
        assertSame(2, sut.position)
    }

    @Test
    fun readText() {
        val sut = CharacterStream("[1!4")

        sut.consume() // [
        sut.consume() // 1
        sut.consume() // !
        assertEquals("[1!", sut.readText())
    }
}
