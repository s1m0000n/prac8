package com.example.demo.app

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import tornadofx.*

class MainController: Controller() {
    val stats = FXCollections.observableArrayList("Нет данных")
    val statsDaily = FXCollections.observableArrayList("Нет данных")
    var model = Model(
        Params(
            timeRangeBetweenCustomers = IntSampler(1, 5),
            timeRangeAtCD = IntSampler(10, 20)
        )
    )
    val queueSize = SimpleStringProperty("")
    val queueViz = SimpleStringProperty("")
    val cashDesksViz = FXCollections.observableArrayList("Нет данных")

    fun step(time: Int) {
        model.step(time)
        stats.clear()
        stats.addAll(model.stats.toList())

        if (model.dailyStats.isNotEmpty()) {
            statsDaily.clear()
            statsDaily.addAll(model.dailyStats.last().toList())
        }
        queueSize.set(" (" + model.queue.size.toString() + " клиентов)")
        var queueVizAcc = ""
        for (i in 0 until model.queue.size) {
            queueVizAcc += "△ "
        }
        queueViz.set(queueVizAcc)
        cashDesksViz.clear()
        cashDesksViz.addAll(model.cashDesks.listViz)
    }
    fun updateParams(params_fun: Params.() -> Unit) {
        val newParams = model.params.copy()
        params_fun(newParams)
        model = Model(newParams)
    }
}