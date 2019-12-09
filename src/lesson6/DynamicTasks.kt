@file:Suppress("UNUSED_PARAMETER")

package lesson6

import kotlin.math.max

/**
 * Наибольшая общая подпоследовательность.
 * Средняя
 *
 * Дано две строки, например "nematode knowledge" и "empty bottle".
 * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
 * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
 * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
 * Если общей подпоследовательности нет, вернуть пустую строку.
 * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
 * При сравнении подстрок, регистр символов *имеет* значение.
 */
fun longestCommonSubSequence(first: String, second: String): String {
    // Использован алгоритм Хиршберга, позволяющий уменьшить затраты памяти
    //до минимальных равным затратам на меньшую из строк
    //Первые элементарные проверки
    if (first.isEmpty() || second.isEmpty()) {
        return ""
    }
    //Проверка на равенство
    //Сложность = O(длинны первого)
    if (second.length == 1) {
        for (it in first) {
            if (it == second.first()) {
                return it.toString()
            }
        }

        return ""
    }

    //Сложность = O(длинна первого + длинна второго)
    val secondLowerPart = second.substring(0 until second.length / 2)
    val secondUpperPart = second.substring(second.length / 2)
    val secondUpperPartReversed = secondUpperPart.reversed()
    val firstReversed = first.reversed()

    //Память: O(длинны первого)
    //Сложность = O(длинна первого * длинну второго)
    val lowerPartRow = longestCommonSubsequenceLengthsRun(first, secondLowerPart)
    val upperPartRow = longestCommonSubsequenceLengthsRun(firstReversed, secondUpperPartReversed)

    //Попытка найти длиннейшую подстроку, от которой будем отталкиваться
    var max = upperPartRow.last()
    var maxIndex = 0

    // Проверяем длинну
    if (lowerPartRow.last() > max) {
        max = lowerPartRow.last()
        maxIndex = first.length
    }

    //Проверка всех найденных элементов на полезность
    for (it in 1 until first.length) {
        val newer = lowerPartRow[it - 1] + upperPartRow[first.length - it - 1]

        if (newer > max) {
            max = newer
            maxIndex = it
        }
    }

    return longestCommonSubSequence(
        first.substring(0 until maxIndex),
        secondLowerPart
    ) + longestCommonSubSequence(
        first.substring(maxIndex),
        secondUpperPart
    )
}


//Дополнительный пробег обычной же версии longestCommonSubSequence, однако без запоминания
//Облегчённый алгоритм пробега для меньших подстрок
fun longestCommonSubsequenceLengthsRun(first: String, second: String): IntArray {
    //Сложность = O(длинны первого)
    if (first.isEmpty() || second.isEmpty()) {
        return IntArray(first.length) { 0 }
    }

    //Сложность = O(длинны первого)
    val row = IntArray(first.length)

    if (first.first() == second.first()) {
        row[0] = 1
    }

    // Первый пробег
    //Сложность = O(длинны первого)
    for (it in 1 until row.size) {
        if (first[it] == second.first()) {
            row[it] = 1
        } else {
            row[it] = row[it - 1]
        }
    }

    //Сложность = O(длинна первого * длинну второго)
    for (that in 1 until second.length) {
        //Сложность = O(длинны первого)
        val newRow = IntArray(first.length)
        if (first.first() == second[that]) {
            newRow[0] = 1
        } else {
            newRow[0] = row[0]
        }

        //Сложность = O(длинны первого)
        for (it in 1 until row.size) {
            if (first[it] == second[that]) {
                newRow[it] = row[it - 1] + 1
            } else {
                newRow[it] = max(newRow[it - 1], row[it])
            }
        }
        for (it in row.indices) {
            row[it] = newRow[it]
        }
    }
    return row
}


/**
 * Наибольшая возрастающая подпоследовательность
 * Сложная
 *
 * Дан список целых чисел, например, [2 8 5 9 12 6].
 * Найти в нём самую длинную возрастающую подпоследовательность.
 * Элементы подпоследовательности не обязаны идти подряд,
 * но должны быть расположены в исходном списке в том же порядке.
 * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
 * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
 * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
 */
fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    TODO()
}

/**
 * Самый короткий маршрут на прямоугольном поле.
 * Средняя
 *
 * В файле с именем inputName задано прямоугольное поле:
 *
 * 0 2 3 2 4 1
 * 1 5 3 4 6 2
 * 2 6 2 5 1 3
 * 1 4 3 2 6 2
 * 4 2 3 1 5 0
 *
 * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
 * В каждой клетке записано некоторое натуральное число или нуль.
 * Необходимо попасть из верхней левой клетки в правую нижнюю.
 * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
 * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
 *
 * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
 */
fun shortestPathOnField(inputName: String): Int {
    TODO()
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5