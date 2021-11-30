package lexer

import org.junit.Assert.*
import org.junit.Test

class ScannerTest:
  @Test def run(): Unit =
    val sut = Scanner("123")

    assertFalse(sut.isEot())
    assertEquals(Some('1'), sut.peek())
    assertEquals(Some('1'), sut.consume())

    assertFalse(sut.isEot())
    assertEquals(Some('2'), sut.peek())
    assertEquals(Some('2'), sut.consume())

    assertFalse(sut.isEot())
    assertEquals(Some('3'), sut.peek())
    assertEquals(Some('3'), sut.consume())

    assertTrue(sut.isEot())
    assertEquals(None, sut.peek())
    assertEquals(None, sut.consume())

  @Test def isEot(): Unit =
    val sut = Scanner("")

    assertTrue(sut.isEot())
