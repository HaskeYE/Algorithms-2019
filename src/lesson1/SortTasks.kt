@file:Suppress("UNUSED_PARAMETER")

package lesson1


import java.io.File
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.HashMap

/**
 * Сортировка времён
 *
 * Простая
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
 * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
 *
 * Пример:
 *
 * 01:15:19 PM
 * 07:26:57 AM
 * 10:00:03 AM
 * 07:56:14 PM
 * 01:15:19 PM
 * 12:40:31 AM
 *
 * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
 * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
 *
 * 12:40:31 AM
 * 07:26:57 AM
 * 10:00:03 AM
 * 01:15:19 PM
 * 01:15:19 PM
 * 07:56:14 PM
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */

class TimeAPM(str: String) :
    Comparable<TimeAPM> {
    internal var first: Int
    internal var second: Int
    internal var third: Int
    internal var partOfDay: String

    init {
        if ((str.trim().length != 11)
            || (str.equals(Regex("""[01][0-2]:[0-6][0-9]:[0-6][0-9] ((AM)|(PM))""")))
        ) throw java.lang.IllegalArgumentException()
        this.first = str.substring(0, 2).toInt()
        this.second = str.substring(3, 5).toInt()
        this.third = str.substring(6, 8).toInt()
        this.partOfDay = str.substring(9, 11)
    }

    //Smaller than new => 1
    //Bigger than new => 0
    override fun compareTo(time: TimeAPM): Int {
        if (this.partOfDay[0].toInt() < time.partOfDay[0].toInt()) return 1
        if (this.partOfDay[0].toInt() > time.partOfDay[0].toInt()) return 0
        else {
            if (this.first < time.first) return 1
            if (this.first > time.first) return 0
            if (this.second < time.second) return 1
            if (this.second < time.second) return 1
            if (this.third < time.third) return 1
            if (this.third > time.third) return 1
        }
        return 0
    }

    public fun writeBack(): String =
        String.format("%02d:%02d:%02d %s", first, second, third, partOfDay)
}


private val random = java.util.Random(Calendar.getInstance().timeInMillis)

public fun partitionAPM(elements: MutableList<TimeAPM>, min: Int, max: Int): Int {
    val x = elements[min + random.nextInt(max - min + 1)]
    var left = min
    var right = max
    while (left <= right) {
        while (elements[left].compareTo(x) == 0) {
            left++
        }
        while (elements[right].compareTo(x) == 1) {
            right--
        }
        if (left <= right) {
            val temp = elements[left]
            elements[left] = elements[right]
            elements[right] = temp
            left++
            right--
        }
    }
    return right
}

private fun quickSortAPM(elements: MutableList<TimeAPM>, min: Int, max: Int) {
    if (min < max) {
        val border = partitionAPM(elements, min, max)
        quickSortAPM(elements, min, border)
        quickSortAPM(elements, border + 1, max)
    }
}


fun sortTimes(inputName: String, outputName: String) {
    if (java.io.File(inputName).readText() == "")
        java.io.File(outputName).writeText("") else {
        val times = java.io.File(inputName).readLines().map { it.trim() }
        val decTimes = times.map { TimeAPM(it) }
        quickSortAPM(decTimes.toMutableList(), 0, times.size - 1)
        val outputStream = File(outputName).bufferedWriter()
        for (time in decTimes) {
            outputStream.write(time.writeBack())
            outputStream.newLine()
        }
        outputStream.close()
    }
}

/**
 * Сортировка адресов
 *
 * Средняя
 *
 * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
 * где они прописаны. Пример:
 *
 * Петров Иван - Железнодорожная 3
 * Сидоров Петр - Садовая 5
 * Иванов Алексей - Железнодорожная 7
 * Сидорова Мария - Садовая 5
 * Иванов Михаил - Железнодорожная 7
 *
 * Людей в городе может быть до миллиона.
 *
 * Вывести записи в выходной файл outputName,
 * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
 * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
 *
 * Железнодорожная 3 - Петров Иван
 * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
 * Садовая 5 - Сидоров Петр, Сидорова Мария
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun sortAddresses(inputName: String, outputName: String) {
    if (java.io.File(inputName).readText() == "")
        java.io.File(outputName).writeText("") else {
        val strings = java.io.File(inputName).readLines().map { it.trim() }
        val splitted = strings[0].split(" - ")
        val set = sortedSetOf(splitted[1])
        val map = sortedMapOf(Pair(splitted[0], set))
        for (line in strings) {
            if (!line.equals(Regex("""[А-Я][а-я]+ [А-Я][а-я]+ - ([А-Я][а-я]+ )+[0-9]+""")))
                throw IllegalArgumentException()
            val spl = line.split(" - ")
            val adress = spl[0]
            val name = spl[1]
            if (map.containsKey(adress)) map[adress]!!.add(name)
            else map[adress] = sortedSetOf(name)
        }
        val outputStream = File(outputName).bufferedWriter()
        for (entry in map.entries) {
            outputStream.write(entry.key)
            outputStream.write(" - ")
            for (name in entry.value) {
                outputStream.write(name)
                outputStream.write(", ")
            }
            outputStream.newLine()
        }
        outputStream.close()
    }
}

/**
 * Сортировка температур
 *
 * Средняя
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
 * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
 * Например:
 *
 * 24.7
 * -12.6
 * 121.3
 * -98.4
 * 99.5
 * -12.6
 * 11.0
 *
 * Количество строк в файле может достигать ста миллионов.
 * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
 * Повторяющиеся строки сохранить. Например:
 *
 * -98.4
 * -12.6
 * -12.6
 * 11.0
 * 24.7
 * 99.5
 * 121.3
 */
fun sortTemperatures(inputName: String, outputName: String) {
    if (java.io.File(inputName).readText() == "")
        java.io.File(outputName).writeText("") else {
        val temps = java.io.File(inputName).readLines().map { it.trim() }
        val nums = DoubleArray(temps.size)
        for (i in 0 until temps.size) {
            if (!temps[i].equals(Regex("""[-0-9][0-9]+\.[0-9]+""")))
                throw IllegalArgumentException()
            val parts = temps[i].split(".")
            nums[i] = parts[0].toDouble() +
                    parts[1].toDouble() / (Math.pow(10.0, parts[1].length.toDouble()))
            if (nums[i] !in (-273.0..500.0)) throw IllegalArgumentException()
        }
        nums.sort()
        val outputStream = File(outputName).bufferedWriter()
        for (temp in nums) {
            outputStream.write(temp.toString())
            outputStream.newLine()
        }
        outputStream.close()
    }
}

/**
 * Сортировка последовательности
 *
 * Средняя
 * (Задача взята с сайта acmp.ru)
 *
 * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
 *
 * 1
 * 2
 * 3
 * 2
 * 3
 * 1
 * 2
 *
 * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
 * а если таких чисел несколько, то найти минимальное из них,
 * и после этого переместить все такие числа в конец заданной последовательности.
 * Порядок расположения остальных чисел должен остаться без изменения.
 *
 * 1
 * 3
 * 3
 * 1
 * 2
 * 2
 * 2
 */
fun sortSequence(inputName: String, outputName: String) {
    if (java.io.File(inputName).readText() == "")
        java.io.File(outputName).writeText("") else {
        val numerals = java.io.File(inputName).readLines().map { it.trim().toInt() }
        val times = HashMap<Int, Int>()
        var desired = Pair(0, 0)
        for (numeral in numerals) {
            if (times[numeral] == null)
                times[numeral] = 1 else
                times[numeral] = times[numeral]!! + 1
            if (((times[numeral]!! >= desired.second) && (numeral < desired.first)) ||
                (times[numeral]!! > desired.second)
            ) desired = Pair(numeral, times[numeral]!!)
        }
        val desiredNum = desired.first
        val outputStream = File(outputName).bufferedWriter()
        for (numeral in numerals)
            if (numeral != desiredNum) {
                outputStream.write(numeral)
                outputStream.newLine()
            }
        for (i in 1..desired.second)
            outputStream.write(desiredNum)
        outputStream.close()
    }
}

/**
 * Соединить два отсортированных массива в один
 *
 * Простая
 *
 * Задан отсортированный массив first и второй массив second,
 * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
 * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
 *
 * first = [4 9 15 20 28]
 * second = [null null null null null 1 3 9 13 18 23]
 *
 * Результат: second = [1 3 4 9 9 13 15 20 23 28]
 */
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    TODO()
}

