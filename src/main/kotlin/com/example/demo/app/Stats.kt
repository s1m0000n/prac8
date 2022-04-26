package com.example.demo.app

import kotlin.math.pow
import kotlin.math.sqrt

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
    val percentServedCustomers: Double,
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
                percentServedCustomers = (servedCustomers.toDouble() / allCustomersDouble) * 100,
                percentLostCustomers = (lostCustomers.toDouble() / allCustomersDouble) * 100,

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
        return """
            [$numDays whole days] Customers: $allCustomers = served $servedCustomers ($percentServedCustomers %) + lost $lostCustomers ($percentLostCustomers %)
            | Profit: $pureProfit (pure) = raw $rawProfit - ads $adsExpenses - salaries $salaryExpenses
            | Pure Profit Daily: mean = $pureProfitMean, var = $pureProfitVariance, std = $pureProfitSTD
            | Raw Profit Daily: mean = $rawProfitMean, var = $rawProfitVariance, std = $rawProfitSTD
        """.trimIndent()
    }

    fun toList(): List<String> {
        if (numDays == 0) return listOf("Нет данных")
        return listOf(
            "Прошло дней: $numDays",
            "Всего клиентов: $allCustomers",
            "Обслужено $servedCustomers (${percentServedCustomers.format(2)} %) клиентов",
            "Потеряно $lostCustomers (${percentLostCustomers.format(2)} %) клиентов",
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