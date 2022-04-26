package com.example.demo.app

import PriorityQueue

class Customer(val total: Int, val waitingTime: Int, val timeAtCD: Int): Comparable<Customer> {
    private var waitingTimeCounter = waitingTime
    var timeAtCDCounter = timeAtCD
    fun isLeaving() = waitingTimeCounter <= 0
    fun waitStep(time: Int): Customer {
        waitingTimeCounter -= time
        return this
    }
    fun cdStep(time: Int): Customer {
        timeAtCDCounter -= time
        return this
    }
    override operator fun compareTo(other: Customer) = waitingTimeCounter - other.waitingTimeCounter
}

class CustomersQueue(
    val timeRangeBetweenCustomers: IntSampler,
    val totalRange: IntSampler,
    val timeRangeWaiting: IntSampler,
    val timeRangeAtCD: IntSampler,
    val adsDailyExpenses7K: Int
) {
    var timeUntilNewCustomer = timeRangeBetweenCustomers.next()
    var customers = PriorityQueue<Customer>()
    var lostCount: Int = 0
    private fun addSampled() = add(Customer(
        total = totalRange.next(),
        waitingTime = timeRangeWaiting.next(),
        timeAtCD = timeRangeAtCD.next()
    ))
    fun step(time: Int) {
        customers = customers.map { it.waitStep(time) }
        while (customers.size > 0 && customers.peek().isLeaving()) {
            customers.poll()
            lostCount += 1
        }

        var added = 0
        if (time <= timeRangeBetweenCustomers.max) {
            timeUntilNewCustomer -= time
            if (timeUntilNewCustomer <= 0) {
                addSampled()
                timeUntilNewCustomer = timeRangeBetweenCustomers.next()
                added += 1
            }
        } else {
            var diff = time
            while (diff >= 0) {
                addSampled()
                diff -= timeRangeBetweenCustomers.next()
                added += 1
            }
        }
        for (i in 0 until (added * adsDailyExpenses7K * 0.1).toInt())
            addSampled()
    }
    private fun add(customer: Customer) = customers.add(customer)
    val size
        get() = customers.size
    val isEmpty
        get() = size == 0
    fun peek() = customers.peek()
    fun poll() = customers.poll()
    fun resetStats() {
        lostCount = 0
    }
}