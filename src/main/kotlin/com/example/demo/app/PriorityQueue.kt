import java.math.BigDecimal
import java.math.BigInteger

class PriorityQueue<T: Comparable<T>> {
    private val data = mutableListOf<T>()
    val size
        get() = data.size
    val isEmpty
        get() = size == 0
    private fun addIndex(value: T): Int {
        var low = 0
        var high = size
        while (low < high) {
            val mid = (low + high) / 2
            if (data[mid] < value) low = mid + 1
            else high = mid
        }
        return low
    }
    fun add(value: T): T {
        data.add(addIndex(value), value)
        return value
    }
    fun poll() = data.removeAt(0)
    fun peek() = data[0]
    fun peekUpdate(f: (T) -> T) = add(f(poll()))
    fun <T2: Comparable<T2>> map(f: (T) -> T2) = fromIterable(data.map(f))
    fun <T2> mapList(f: (T) -> T2) = data.map(f)
    fun toList() = data.toList()
    companion object {
        fun <T: Comparable<T>> fromIterable(iter: Iterable<T>): PriorityQueue<T> {
            val instance = PriorityQueue<T>()
            for (element in iter) instance.add(element)
            return instance
        }
    }
}