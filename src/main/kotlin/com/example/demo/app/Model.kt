package com.example.demo.app


class Model(
    var params: Params
) {
    val cashDesks = CDQueue(params.numWorkers, params.maxQueueSize)
    val queue = CustomersQueue(
        timeRangeBetweenCustomers = params.timeRangeBetweenCustomers,
        totalRange = params.totalRange,
        timeRangeWaiting = params.timeRangeWaiting,
        timeRangeAtCD = params.timeRangeAtCD,
        adsDailyExpenses7K = params.adsDailyExpenses7K
    )
    private var daysSinceStart = 0
    private var timeUntilDayEnd = 1440
    val dailyStats = mutableListOf<DailyStats>()
    val stats
        get() = Stats.fromDaily(dailyStats)
    fun step(time: Int) {
        cashDesks.step(time)
        while (!queue.isEmpty && !cashDesks.peek().limitReached)
            cashDesks.add(queue.poll())

        timeUntilDayEnd -= time
        if (timeUntilDayEnd <= 0) {
            timeUntilDayEnd = 1440
            daysSinceStart += 1
            dailyStats.add(DailyStats(
                servedCustomers = cashDesks.servedCount,
                lostCustomers = queue.lostCount,
                total = cashDesks.total,
                adsExpenses = params.adsDailyExpenses7K * 7000,
                profitPerPurchase = params.profitPerPurchase,
                salary = params.salary,
                numWorkers = params.numWorkers
            ))
            cashDesks.resetStats()
            queue.resetStats()
        }
        queue.step(time)
    }
}