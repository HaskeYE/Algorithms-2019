@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson7

import lesson5.Graph
import lesson5.Path
import lesson6.knapsack.Fill
import lesson6.knapsack.Item
import lesson6.knapsack.fillKnapsackDynamic
import lesson6.knapsack.fillKnapsackGreedy


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

/** Попытка решить задачу, используя алгоритм "Tabu List".
Будет выбрано решение, используя fillGreedy, то есть набраны условно самые полезные предметы.
Далее буду заменять данные предметы более лёгкими, попутно занося заменённые предметы в tabuList.

Новые элементы будут искаться способами динамического программирования.

TabuList отдельно реализован не будет для экономии памяти - элементы из items
просто будут убираться после использования*/
//Затраты памяти: О(длинны items)
fun fillKnapsackHeuristics(load: Int, items: List<Item>, vararg parameters: Any): Fill {
    var itemsTabu = items.toMutableList()
    var knap = fillKnapsackGreedy(load, items)
    itemsTabu.removeAll(knap.items)
    val itemsToReplace = knap.items

    for (item in itemsToReplace) {
        val oldKnap = knap
        knap.remove(item)
        val newLoad = load - knap.getLoad()
        val knapNewDyn = fillKnapsackDynamic(newLoad, itemsTabu)
        if (oldKnap.cost < (knap + knapNewDyn).cost) {
            knap += knapNewDyn
            itemsTabu.removeAll(knapNewDyn.items)
        } else knap = oldKnap
    }

    return knap
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

