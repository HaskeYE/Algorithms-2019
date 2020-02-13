package lesson7

import lesson6.knapsack.Item
import lesson6.knapsack.fillKnapsackGreedy
import org.junit.jupiter.api.Tag
import kotlin.test.Test
import kotlin.test.assertSame

class HeuristicsTestsKotlin : AbstractHeuristicsTests() {

    @Test
    @Tag("Impossible")
    fun testFillKnapsackCompareWithGreedyTest() {
        val items = listOf(Item(8, 10), Item(5, 12), Item(6, 8), Item(10, 15), Item(4, 2))
        assertSame(
            true,
            fillKnapsackHeuristics(30, items).cost >= fillKnapsackGreedy(30, items).cost
        )
    }


    @Test
    @Tag("Impossible")
    fun testFindVoyagingPathHeuristics() {
        findVoyagingPathHeuristics { findVoyagingPathHeuristics() }
    }
}