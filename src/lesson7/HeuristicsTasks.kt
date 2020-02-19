@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson7

import lesson5.Graph
import lesson5.Path
import lesson6.knapsack.Fill
import lesson6.knapsack.Item
import lesson6.knapsack.fillKnapsackDynamic
import lesson6.knapsack.fillKnapsackGreedy
import java.util.concurrent.ThreadLocalRandom


// Примечание: в этом уроке достаточно решить одну задачу

/**
 * Решить задачу о ранце (см. урок 6) любым эвристическим методом
 *
 * Очень сложная
 *
 * load - общая вместимость ранца, items - список предметов
 *
 * Используйте parameters для передачи дополнительных параметров алгоритма
 * (не забудьте изменить тесты так, чтобы они передавали эти параметры)
 */

/** Попытка решить задачу, используя генетический алгоритм
 * Попробую отбирать случайные предметы в рюкзак. Дано будет 6 попыток что-либо положить
 * Далее population подобных рюкзаков будут выбраны в качестве начальной популяции
 * После этого начнётся скрещивание, условием окончания которого будет являться превышение или равенство одним из
 * рюкзаков стоимости рюкзака, укомплектованного жадным алгоритмом
 * Скрещиваться будут соседние особи
 * Скрещивание - заполнение рюкзака динамически от элементов двух родителей*/
//Затраты памяти: О(длинны items)
fun fillKnapsackHeuristics(load: Int, items: List<Item>, population: Int): Fill {
    var knaps = mutableListOf<Fill>()
    val greedy = fillKnapsackGreedy(load, items)
    var i = 0
    while (i < population) knaps[i] = randomItems(load, items)
    while (knaps.size > 1) {
        for (j in 0..(knaps.size - 2)) {
            val newItemList = knaps[j].items.toList() + knaps[j + 1].items
            knaps[j] = fillKnapsackDynamic(load, newItemList)
            if (knaps[j].cost >= greedy.cost) return knaps[j]
        }
    }
    return knaps[0]
}

//Вариант, если для алгоритма потребуется обоих родителей выбирать случайно
//Для такого варианта будет выбрано (populationHere/2) пар родителей и они будут скрещены
fun randomParents(populationHere: Int): Pair<Int, Int> {
    val first = ThreadLocalRandom.current().nextInt(0, populationHere - 1)
    var second = first
    while (first == second)
        second = ThreadLocalRandom.current().nextInt(0, populationHere - 1)
    return Pair(first, second)
}

fun randomItems(load: Int, items: List<Item>): Fill {
    var steps = 0
    val knap = Fill(0, emptySet())
    while (steps <= 6) {
        val randomItem = items[ThreadLocalRandom.current().nextInt(0, items.size - 1)]
        if (randomItem.weight <= load - knap.getLoad()) knap.add(randomItem)
        steps++
    }
    return if (knap.cost == 0) randomItems(load, items) else knap
}

/**
 * Решить задачу коммивояжёра (см. урок 5) методом колонии муравьёв
 * или любым другим эвристическим методом, кроме генетического и имитации отжига
 * (этими двумя методами задача уже решена в под-пакетах annealing & genetic).
 *
 * Очень сложная
 *
 * Граф передаётся через получатель метода
 *
 * Используйте parameters для передачи дополнительных параметров алгоритма
 * (не забудьте изменить тесты так, чтобы они передавали эти параметры)
 */
fun Graph.findVoyagingPathHeuristics(vararg parameters: Any): Path {
    TODO()
}

