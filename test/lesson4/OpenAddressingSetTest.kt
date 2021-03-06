package lesson4

import org.junit.jupiter.api.Tag
import kotlin.test.Test
import kotlin.test.*
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

class OpenAddressingSetTest {

    @Test
    @Tag("Example")
    fun add() {
        val set = OpenAddressingSet<String>(16)
        assertTrue(set.isEmpty())
        set.add("Alpha")
        set.add("Beta")
        set.add("Omega")
        assertSame(3, set.size)
        assertTrue("Beta" in set)
        assertFalse("Gamma" in set)
        assertTrue("Omega" in set)
    }

    @Test
    @Tag("New")
    fun remove() {
        val set = OpenAddressingSet<String>(16)
        assertTrue(set.isEmpty())
        set.add("Alpha")
        set.add("Beta")
        set.add("Omega")
        set.remove("Beta")
        assertSame(2, set.size)
        assertSame(true, set.remove("Omega"))
        assertTrue("Alpha" in set)
        assertFalse("Gamma" in set)
        assertFalse("Beta" in set)
    }

    @Test
    @Tag("New")
    fun removeOther() {
        val set = OpenAddressingSet<String>(16)
        assertTrue(set.isEmpty())
        set.add("Alpha")
        set.add("Beta")
        set.add("Omega")
        set.remove("Omega")
        assertSame(false, set.remove("Nothing"))
    }

}