package lesson6.knapsack

import java.util.*

data class Fill(var cost: Int, var items: Set<Item>) {
    operator fun plus(fill: Fill) = Fill(cost + fill.cost, items + fill.items)

    fun remove(item: Item) {
        this.cost - item.cost
        this.items - item
    }

    fun add(item: Item) {
        this.cost = this.cost + item.cost
        this.items = this.items + item
    }

    fun getLoad(): Int {
        var load = 0
        for (item in this.items)
            load += item.weight
        return load
    }

    fun addAll(items: Collection<Item>) = items.forEach { this.add(it) }

    constructor(cost: Int, vararg items: Item) : this(cost, items.toSet())

    constructor(item: Item) : this(item.cost, item)
}

data class LoadCount(val load: Int, val count: Int)

data class Item(val cost: Int, val weight: Int)

fun fillKnapsackDynamic(
    load: Int, items: List<Item>,
    storage: HashMap<LoadCount, Fill> = hashMapOf()
): Fill {
    if (load <= 0 || items.isEmpty()) return Fill(0, emptySet())
    val loadCount = LoadCount(load, items.size)
    return storage.getOrPut(loadCount) {
        val itemsWithoutLast = items.subList(0, items.size - 1)
        val fillWithoutLast = fillKnapsackDynamic(load, itemsWithoutLast, storage)
        val last = items.last()
        if (last.weight > load) fillWithoutLast
        else {
            val fillWithLast = fillKnapsackDynamic(load - last.weight, itemsWithoutLast, storage) + Fill(last)
            if (fillWithLast.cost > fillWithoutLast.cost) fillWithLast
            else fillWithoutLast
        }
    }
}

private tailrec fun fillKnapsackGreedySorted(load: Int, items: List<Item>, baseFill: Fill = Fill(0)): Fill {
    if (load <= 0 || items.isEmpty()) return baseFill
    val itemsWithoutLast = items.subList(0, items.size - 1)
    val last = items.last()
    return fillKnapsackGreedySorted(
        if (last.weight > load) load else load - last.weight, itemsWithoutLast,
        if (last.weight > load) baseFill else baseFill + Fill(last)
    )
}

fun fillKnapsackGreedy(load: Int, items: List<Item>): Fill {
    val sorted = items.sortedWith(Comparator { o1, o2 ->
        (o1.cost.toDouble() / o1.weight).compareTo(o2.cost.toDouble() / o2.weight)
    })
    return fillKnapsackGreedySorted(load, sorted)
}

fun main() {
    val items = listOf(Item(8, 10), Item(5, 12), Item(6, 8), Item(10, 15), Item(4, 2))
    println(fillKnapsackDynamic(30, items))
    println(fillKnapsackGreedy(30, items))
}