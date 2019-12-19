@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson5

import java.util.*

/**
 * Эйлеров цикл.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему любой Эйлеров цикл.
 * Если в графе нет Эйлеровых циклов, вернуть пустой список.
 * Соседние дуги в списке-результате должны быть инцидентны друг другу,
 * а первая дуга в списке инцидентна последней.
 * Длина списка, если он не пуст, должна быть равна количеству дуг в графе.
 * Веса дуг никак не учитываются.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Вариант ответа: A, E, J, K, D, C, H, G, B, C, I, F, B, A
 *
 * Справка: Эйлеров цикл -- это цикл, проходящий через все рёбра
 * связного графа ровно по одному разу
 */
//Сложность = O((к-ва вершин) * (к-во рёбер))
//Память = О(
//Попытка сделать максимально комплексно. Некоторые алгоритмы были вдохновлены статьями с Wiki
fun Graph.findEulerLoop(): List<Graph.Edge> {
    //Проведём проверки на необходимость поиска данного цикла
    if (vertices.isEmpty()) {
        return emptyList()
    }

    //Поиск вершин с чётным к-вом соседей для понимания картины переходов и
    //понимания возможности существования цикла
    // Сложность = O(к-ва вершин)
    val verticesWithOddNumberOfNeighbours = vertices.filter {
        getConnections(it).size % 2 == 1
    }

    //Существование Эйлерова цикла возможно только тогда, когда
    //к-во вершин с чётным к-вом соседей больше нуля
    if (verticesWithOddNumberOfNeighbours.isNotEmpty()) {
        return emptyList()
    }

    //Проверка графа на связность
    //Попробуем проитерироваться через весь граф и определить прошли ли мы все вершины
    // Сложность = O(к-ва вершин) для каждого прохода

    val iterationHistory = mutableSetOf<Graph.Vertex>()
    //Используем LinkedList т.к. итерируемся по нему последовательно
    val verticesToIterate = LinkedList<Graph.Vertex>()

    //Начинаем с первой вершины графа и идём далее по списку
    verticesToIterate.add(vertices.first())
    while (verticesToIterate.isNotEmpty()) {
        val nextVertex = verticesToIterate.removeFirst()
        iterationHistory.add(nextVertex)
        for (vertex in getNeighbors(nextVertex)) {
            if (!iterationHistory.contains(vertex)) {
                verticesToIterate.add(vertex)
            }
        }
    }

    //Итоговая проверка на равное количество вершин
    for (each in vertices) {
        if (each !in iterationHistory) {
            return emptyList()
        }
    }

    //Просмотренные циклы. Необходимое условие окончания поиска
    val deleted = mutableSetOf<Graph.Edge>()
    val result = Array<Graph.Edge>(edges.size) { edges.first() }
    var resultIndex = 0
    var cycle: List<Pair<Graph.Edge, Graph.Vertex>>?

    fun addAllCycles(vertex: Graph.Vertex) {
        do {
            //Сложность = O((к-ва вершин) * (к-во рёбер))

            cycle = cycleFromStartVertex(vertex) {
                it !in deleted
            }



            if (cycle != null) {
                //Сложность = O(к-ва вершин)
                for (way in cycle!!) {
                    deleted.add(way.first)
                }

                //Сложность = O((к-ва вершин) * (к-во рёбер))
                for (it in 0 until cycle!!.size - 1) {
                    result[resultIndex] = cycle!![it].first
                    resultIndex += 1

                    addAllCycles(cycle!![it].second)
                }

                //Рассчёт к-ва
                deleted.add(cycle!!.last().first)
                result[resultIndex] = cycle!!.last().first
                resultIndex++
            }
        } while (cycle != null)
    }

    addAllCycles(vertices.first())
    return result.toList()
}

//Функция нахождения любого цикла начиная с данной вершины(была вынесена из вышестоящего решения)
//Либо находит цикл, либо выдаёт null
fun Graph.cycleFromStartVertex(
    vertex: Graph.Vertex,
    addOrNot: (Graph.Edge) -> Boolean = { true }
): List<Pair<Graph.Edge, Graph.Vertex>>? {
    //Для проверки на повторы????
    val historyHere = mutableSetOf<Graph.Vertex>()


    //Начинаем итерироваться, используя стек в качестве пула для вершин с их данными
    //Сложность = O(к-ва вершин)
    val stack = LinkedList<Triple<Graph.Edge, Graph.Vertex, Int>>()
    for (edge in getConnections(vertex))
        if (addOrNot(edge.value)) {
            stack.add(Triple(edge.value, edge.key, 0))
        }
    //Создаём некий искуственно расширяемый "бакет" для удобства итерации
    val bucket = ArrayList<Pair<Graph.Edge, Graph.Vertex>>()
    fun addToBucket(edge: Graph.Edge, vertex: Graph.Vertex, index: Int) {
        if (bucket.size > index) {
            bucket[index] = edge to vertex
        } else {
            bucket.add(edge to vertex)
        }
    }

    //Сложность = O((к-ва вершин) * (к-во рёбер))
    while (stack.isNotEmpty()) {
        val (nextEdge, nextVertex, nextIndex) = stack.removeLast()
        addToBucket(nextEdge, nextVertex, nextIndex)
        historyHere.add(nextVertex)

        //Сложность = O(к-ва вершин)
        for (edge in getConnections(nextVertex)) {
            if (edge.key == vertex) {
                if (nextIndex != 0 && addOrNot(edge.value)) {
                    addToBucket(edge.value, edge.key, nextIndex + 1)
                    //Выводим по частям в соответствии индексам
                    return bucket.slice(0..nextIndex + 1)
                }
            } else if (historyHere.contains(edge.key) && addOrNot(edge.value)) {
                stack.add(Triple(edge.value, edge.key, nextIndex + 1))
            }
        }
    }
    //null при отсутствии поводов инициации return до этого
    return null
}

/**
 * Минимальное остовное дерево.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему минимальное остовное дерево.
 * Если есть несколько минимальных остовных деревьев с одинаковым числом дуг,
 * вернуть любое из них. Веса дуг не учитывать.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ:
 *
 *      G    H
 *      |    |
 * A -- B -- C -- D
 * |    |    |
 * E    F    I
 * |
 * J ------------ K
 */
fun Graph.minimumSpanningTree(): Graph {
    TODO()
}

/**
 * Максимальное независимое множество вершин в графе без циклов.
 * Сложная
 *
 * Дан граф без циклов (получатель), например
 *
 *      G -- H -- J
 *      |
 * A -- B -- D
 * |         |
 * C -- F    I
 * |
 * E
 *
 * Найти в нём самое большое независимое множество вершин и вернуть его.
 * Никакая пара вершин в независимом множестве не должна быть связана ребром.
 *
 * Если самых больших множеств несколько, приоритет имеет то из них,
 * в котором вершины расположены раньше во множестве this.vertices (начиная с первых).
 *
 * В данном случае ответ (A, E, F, D, G, J)
 *
 * Если на входе граф с циклами, бросить IllegalArgumentException
 *
 * Эта задача может быть зачтена за пятый и шестой урок одновременно
 */
fun Graph.largestIndependentVertexSet(): Set<Graph.Vertex> {
    //Простейшие проверки
    if (vertices.isEmpty()) {
        return emptySet()
    }

    // Проверка на наличие циклов
    //Сложность = O((к-ва вершин) * (к-во рёбер))
    for (vertex in vertices) {
        val cycle = cycleFromStartVertex(vertex)

        if (cycle != null) {
            throw IllegalArgumentException("Cycle found")
        }
    }

    //Проверка графа на связность
    //Попробуем проитерироваться через весь граф и определить прошли ли мы все вершины
    // Сложность = O(к-ва вершин) для каждого прохода

    val iterationHistory = mutableSetOf<Graph.Vertex>()
    //Используем LinkedList т.к. итерируемся по нему последовательно
    val verticesToIterate = LinkedList<Graph.Vertex>()

    //Начинаем с первой вершины графа и идём далее по списку
    verticesToIterate.add(vertices.first())
    while (verticesToIterate.isNotEmpty()) {
        val nextVertex = verticesToIterate.removeFirst()
        iterationHistory.add(nextVertex)
        for (vertex in getNeighbors(nextVertex)) {
            if (!iterationHistory.contains(vertex)) {
                verticesToIterate.add(vertex)
            }
        }
    }

    //Итоговая проверка на равное количество вершин
    for (vertex in vertices) {
        if (vertex !in iterationHistory) {
            return emptySet()
        }
    }

    val vertexHis = mutableSetOf<Graph.Vertex>()
    //Сложность = O(к-ва вершин)
    val result = mutableSetOf<Graph.Vertex>()

    //Сложность = O((к-вo вершин)^2)
    //Затраты памяти = О(к-ва вершин)
    for (startVertex in vertices) {
        if (startVertex !in vertexHis) {
            //Сложность = O(к-ва вершин)
            //Поиск групп независимых вершин, начиная с данной стартовой и их объединение
            //Написана вспомогательная ф-ция ниже
            result.addAll(tryingToFindIndependent(vertexHis, startVertex))
        }
    }

    return result
}

//Вспомогательная ф-ция для поисковика множества независимых вершин
fun Graph.tryingToFindIndependent(
    vertexHis: MutableSet<Graph.Vertex>,
    startVertex: Graph.Vertex
): MutableSet<Graph.Vertex> {
    //  Попробуем получить необходимый результат.
    //  Суть алгоритма такова, что будут проведены две попытки try -
    //каждая для своего типа граней.
    //  Заведём переменную-счётчик, которая будет позволять отбирать
    //вершины по очереди сначала для первой попытки, потом для второй.
    //  То есть, условно поделим все вершины на "чётные" и "нечётные" и
    //определим одни в одну, другие в другую попытку и сравним их

    val firstTry = mutableSetOf<Graph.Vertex>()
    val secondTry = mutableSetOf<Graph.Vertex>()

    //Сложность = O(к-ва вершин)
    val queue = LinkedList<Pair<Graph.Vertex, Boolean>>()
    queue.add(startVertex to true)

    //Сложность = O(к-ва вершин)
    while (queue.isNotEmpty()) {
        val nextVertex = queue.removeFirst().first
        val counter = queue.removeFirst().second
        vertexHis.add(nextVertex)

        if (counter) {
            firstTry.add(nextVertex)
        } else {
            secondTry.add(nextVertex)
        }

        for (vertex in getNeighbors(nextVertex)) {
            if (vertex !in vertexHis) {
                queue.add(vertex to !counter)
            }
        }
    }

    return if (firstTry.size >= secondTry.size) {
        firstTry
    } else {
        secondTry
    }
}

/**
 * Наидлиннейший простой путь.
 * Сложная
 *
 * Дан граф (получатель). Найти в нём простой путь, включающий максимальное количество рёбер.
 * Простым считается путь, вершины в котором не повторяются.
 * Если таких путей несколько, вернуть любой из них.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ: A, E, J, K, D, C, H, G, B, F, I
 */
fun Graph.longestSimplePath(): Path {
    //Простейшие проверки
    if (vertices.isEmpty()) {
        return Path()
    }

    // Проверка на наличие циклов
    //Сложность = O((к-ва вершин) * (к-во рёбер))
    for (vertex in vertices) {
        val cycle = cycleFromStartVertex(vertex)

        if (cycle != null) {
            throw IllegalArgumentException("Cycle found")
        }
    }

    //Проверка графа на связность
    //Попробуем проитерироваться через весь граф и определить прошли ли мы все вершины
    // Сложность = O(к-ва вершин) для каждого прохода

    val iterationHistory = mutableSetOf<Graph.Vertex>()
    //Используем LinkedList т.к. итерируемся по нему последовательно
    val verticesToIterate = LinkedList<Graph.Vertex>()

    //Начинаем с первой вершины графа и идём далее по списку
    verticesToIterate.add(vertices.first())
    while (verticesToIterate.isNotEmpty()) {
        val nextVertex = verticesToIterate.removeFirst()
        iterationHistory.add(nextVertex)
        for (vertex in getNeighbors(nextVertex)) {
            if (!iterationHistory.contains(vertex)) {
                verticesToIterate.add(vertex)
            }
        }
    }

    //Итоговая проверка на равное количество вершин
    for (vertex in vertices) {
        if (vertex !in iterationHistory) {
            return Path()
        }
    }

    //Поиск необходимого пути
    //Сложность = O((к-вo вершин)^2)
    //Затраты памяти = О(к-ва вершин)
    val vertexHis = mutableSetOf<Graph.Vertex>()
    //Сложность = O(к-ва вершин)
    var result = Path()

    //Сложность = O((к-вo вершин)^2)
    //Затраты памяти = О(к-ва вершин)
    for (startVertex in vertices) {
        if (startVertex !in vertexHis) {
            //Сложность = O(к-ва вершин)
            //Поиск групп независимых вершин, начиная с данной стартовой и их объединение
            //Написана вспомогательная ф-ция ниже
            val newResult = findLongestVoyagingPath(Path(startVertex)) ?: Path()
            if (newResult.length > result.length) result = newResult
        }
    }

    return result
}

fun Graph.findLongestVoyagingPath(
    currentPath: Path = Path(vertices.first()),
    bestPath: Path? = null
): Path? {
    var best = bestPath
    for (next in getNeighbors(currentPath.vertices.last())) {
        if (next !in currentPath) continue
        val nextPath = Path(currentPath, this, next)
        if (best != null && best.length <= nextPath.length && !nextPath.isLoop(this)) {
            best = nextPath
            findLongestVoyagingPath(nextPath, best) ?: continue
        }
    }
    return best
}