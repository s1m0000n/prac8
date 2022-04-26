package com.example.demo.app

import java.util.LinkedList
import java.util.Queue
import PriorityQueue


class CashDesk(private val maxQueueSize: Int): Comparable<CashDesk> {
    private var queue: Queue<Customer> = LinkedList()
    var servedCount = 0
    var total = 0
    val size: Int
        get() = queue.size
    fun step(time: Int): CashDesk {
        var timeLeft = time
        while (timeLeft >= 0) {
            var currentCustomer = queue.poll()
            if (currentCustomer != null) {
                currentCustomer = currentCustomer.cdStep(time)
                if (currentCustomer.timeAtCDCounter <= 0) {
                    total += currentCustomer.total
                    servedCount += 1
                    timeLeft = -currentCustomer.timeAtCDCounter
                } else {
                    queue.add(currentCustomer)
                }
            } else break
        }
        return this
    }
    fun add(customer: Customer): CashDesk {
        if (size < maxQueueSize) {
            queue.add(customer)
        }
        return this
    }
    val limitReached
        get() = size >= maxQueueSize
    override operator fun compareTo(other: CashDesk) =
        size - other.size
    fun resetStats(): CashDesk {
        servedCount = 0
        total = 0
        return this
    }
}

class CDQueue(count: Int, maxQueueSize: Int) {
    private var cashDesks = PriorityQueue<CashDesk>()
    init {
        for (i in 0 until count)
            cashDesks.add(CashDesk(maxQueueSize))
    }
    val servedCount: Int
        get() = cashDesks.mapList { it.servedCount }.sum()
    val total: Int
        get() = cashDesks.mapList { it.total }.sum()
    fun step(time: Int) {
        cashDesks = cashDesks.map { it.step(time) }
    }
    fun add(customer: Customer) =
        cashDesks.add(cashDesks.poll().add(customer))

    fun resetStats() {
        cashDesks = cashDesks.map { it.resetStats() }
    }
    fun peek() = cashDesks.peek()
    val listViz: List<String>
        get(): List<String> = cashDesks.mapList { "□" + "△ ".repeat(it.size) }
}

data class Params(
    var timeRangeAtCD: IntSampler = IntSampler(1, 7),
    var timeRangeBetweenCustomers: IntSampler = IntSampler(1, 3),
    var totalRange: IntSampler = IntSampler(100, 10000),
    var timeRangeWaiting: IntSampler = IntSampler(1, 7),
    var numWorkers: Int = 10,
    var maxQueueSize: Int = 10,
    var adsDailyExpenses7K: Int = 2,
    var profitPerPurchase: Double = 0.09,
    var salary: Int = 1500
)