import java.util.LinkedList
import kotlin.random.Random
import java.util.Queue
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun Double.format(digits: Int) = "%.${digits}f".format(this)

class IntSampler(val min: Int, val max: Int) {
    fun next() = Random.nextInt(min, max)
    override fun toString() = "[$min, $max]"
}

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
    fun step(time: Int, additionalCustomersPart: Double) {
        customers = customers.map { it.waitStep(time) }
        while (customers.size > 0 && customers.peek().isLeaving()) {
            customers.poll()
            lostCount += 1
        }
        var time = (time.toDouble() * (additionalCustomersPart + 1)).roundToInt()
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
    var cashDesks = PriorityQueue<CashDesk>()
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

data class DailyStats(
    val servedCustomers: Int,
    val lostCustomers: Int,
    val total: Int,
    val adsExpenses: Int,
    val profitPerPurchase: Double,
    val salary: Int,
    val numWorkers: Int
) {
    fun toList(): List<String> {
        val allCustomers = servedCustomers + lostCustomers
        val rawProfit = total * profitPerPurchase
        val salaryExpenses = salary * numWorkers
        val allExpenses = salaryExpenses + adsExpenses
        val pureProfit = rawProfit - allExpenses
        return listOf(
            "Всего $allCustomers клиентов",
            "Обслужено $servedCustomers клиентов",
            "Потеряно $lostCustomers клиентов",
            "Чистая прибыль ${pureProfit.format(2)} рублей",
            "Прибыль ${rawProfit.format(2)} рублей",
            "Всего расходы $allExpenses рублей",
            "Расходы на рекламу: $adsExpenses рублей",
            "Расходы на зарплату: $salaryExpenses рублей"
        )
    }
}

fun List<Number>.mean() = sumOf { it.toDouble() } / size

fun List<Number>.variance(): Double {
    val meanValue = mean()
    return map { (it.toDouble() -  meanValue).pow(2) }.mean()
}

data class Stats(
    val numDays: Int,

    val allCustomers: Int,
    val servedCustomers: Int,
    val percentSavedCustomers: Double,
    val lostCustomers: Int,
    val percentLostCustomers: Double,

    val allExpenses: Int,
    val adsExpenses: Int,
    val salaryExpenses: Int,

    val rawProfit: Double,
    val rawProfitMean: Double,
    val rawProfitVariance: Double,
    val rawProfitSTD: Double,

    val pureProfit: Double,
    val pureProfitMean: Double,
    val pureProfitVariance: Double,
    val pureProfitSTD: Double,
) {
    companion object {
        fun fromDaily(daily: Iterable<DailyStats>): Stats {
            val servedCustomers = daily.sumOf { it.servedCustomers }
            val lostCustomers = daily.sumOf { it.lostCustomers }
            val allCustomers = servedCustomers + lostCustomers
            val allCustomersDouble = allCustomers.toDouble()
            val adsExpenses = daily.sumOf { it.adsExpenses }
            val salaryExpenses = daily.sumOf { it.salary * it.numWorkers }
            val pureProfitDaily = daily.map { it.total * it.profitPerPurchase - it.adsExpenses - it.salary * it.numWorkers }
            val pureProfitVariance = pureProfitDaily.variance()
            val rawProfitDaily = daily.map { it.total * it.profitPerPurchase }
            val rawProfit = rawProfitDaily.sum()
            val pureProfit = rawProfit - adsExpenses - salaryExpenses
            val numDays = daily.toList().size
            val rawProfitVariance = rawProfitDaily.variance()
            return Stats(
                numDays = numDays,

                allCustomers = allCustomers,
                servedCustomers = servedCustomers,
                lostCustomers = lostCustomers,
                percentSavedCustomers = servedCustomers.toDouble() / allCustomersDouble,
                percentLostCustomers = lostCustomers.toDouble() / allCustomersDouble,

                allExpenses = adsExpenses + salaryExpenses,
                adsExpenses = adsExpenses,
                salaryExpenses = salaryExpenses,

                rawProfit = rawProfit,
                rawProfitMean = rawProfit / numDays,
                rawProfitVariance = rawProfitVariance,
                rawProfitSTD = sqrt(rawProfitVariance),

                pureProfit = pureProfit,
                pureProfitMean = pureProfit / numDays,
                pureProfitVariance = pureProfitVariance,
                pureProfitSTD = sqrt(pureProfitVariance),
            )
        }
    }

    override fun toString(): String {
        val percentServed = (servedCustomers.toDouble() / allCustomers.toDouble()) * 100
        val percentLost = (lostCustomers.toDouble() / allCustomers.toDouble()) * 100
        return """
            [$numDays whole days] Customers: $allCustomers = served $servedCustomers ($percentServed %) + lost $lostCustomers ($percentLost %)
            | Profit: $pureProfit (pure) = raw $rawProfit - ads $adsExpenses - salaries $salaryExpenses
            | Pure Profit Daily: mean = $pureProfitMean, var = $pureProfitVariance, std = $pureProfitSTD
            | Raw Profit Daily: mean = $rawProfitMean, var = $rawProfitVariance, std = $rawProfitSTD
        """.trimIndent()
    }

    fun toList(): List<String> {
        if (numDays == 0) return listOf("Нет данных")
        val percentServed = (servedCustomers.toDouble() / allCustomers.toDouble()) * 100
        val percentLost = (lostCustomers.toDouble() / allCustomers.toDouble()) * 100
        return listOf(
            "Прошло дней: $numDays",
            "Всего клиентов: $allCustomers",
            "Обслужено $servedCustomers (${percentServed.format(2)} %) клиентов",
            "Потеряно $lostCustomers (${percentLost.format(2)} %) клиентов",
            "Чистая прибыль: ${pureProfit.format(2)} рублей",
            "Прибыль: ${rawProfit.format(2)} рублей",
            "Всего расходы $allExpenses рублей",
            "Расходы на рекламу: $adsExpenses рублей",
            "Расходы на зарплату: $salaryExpenses рублей",
            "Чистая прибыль по дням в среднем: ${pureProfitMean.format(2)}",
            "Прибыль по дням в среднем: ${rawProfitMean.format(2)}"
        )
    }
}



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
    private var additionalCustomersPart = 0.0
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
            additionalCustomersPart = params.adsDailyExpenses7K.toDouble() / 10
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
        queue.step(time, additionalCustomersPart)
    }
}