package lesson4


class OpenAddressingSet<T : Any>(private val bits: Int) : AbstractMutableSet<T>() {
    init {
        require(bits in 2..31)
    }

    //Метка для удалённых элементов
    val AVAILABLE: Any? = (null)

    private val capacity = 1 shl bits

    private val storage = Array<Any?>(capacity) { null }

    override var size: Int = 0

    private fun T.startingIndex(): Int {
        return hashCode() and (0x7FFFFFFF shr (31 - bits))
    }

    override fun contains(element: T): Boolean {
        var index = element.startingIndex()
        var current = storage[index]
        while (current != null) {
            if (current == element) {
                return true
            }
            index = (index + 1) % capacity
            current = storage[index]
        }
        return false
    }

    override fun add(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = startingIndex
        var current = storage[index]
        while (current != null || current != AVAILABLE) {
            if (current == element) {
                return false
            }
            index = (index + 1) % capacity
            check(index != startingIndex) { "Table is full" }
            current = storage[index]
        }
        storage[index] = element
        size++
        return true
    }

    /**
     * Для этой задачи пока нет тестов, но вы можете попробовать привести решение и добавить к нему тесты
     */
    //Сложность: О(к-ва элементов)
    //Память: О(к-ва элементов)
    override fun remove(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = element.startingIndex()
        val current = storage[index]

        while (current != null) {
            if (current == element) {
                storage[index] = AVAILABLE
                size -= 1
                return true
            }
            index = (index + 1) % capacity
            if (index == startingIndex) return false
        }
        return false
    }

    /**
     * Для этой задачи пока нет тестов, но вы можете попробовать привести решение и добавить к нему тесты
     */
    override fun iterator(): MutableIterator<T> = SetIterator()

    inner class SetIterator internal constructor() : MutableIterator<T> {
        private var currentIndex = 0
        private var nextIndex = 1

        private var current: T? = null

        override fun hasNext(): Boolean = nextIndex < capacity

        override fun next(): T {
            if (nextIndex < capacity) {
                nextIndex++
                currentIndex++
                if (storage[currentIndex] == AVAILABLE || storage[currentIndex] == null) return next()
            }
            return storage[currentIndex] as T
        }

        override fun remove() {
            if (current != null || current != AVAILABLE) {
                storage[currentIndex] = AVAILABLE
                size -= 1
            }
        }
    }
}