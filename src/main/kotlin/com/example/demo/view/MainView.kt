package com.example.demo.view

import com.example.demo.app.IntSampler
import com.example.demo.app.MainController
import com.example.demo.app.Styles
import javafx.scene.control.TextField
import tornadofx.*

class MainView : View("Моделирование работы супермаркета") {
    private val intFieldErrorMessage = "Значение поля должно быть целым числом"
    private var timeRangeAtCDMinField = TextField()
    private var timeRangeAtCDMaxField = TextField()
    private var timeRangeBCMinField = TextField()
    private var timeRangeBCMaxField = TextField()
    private var totalRangeMinField = TextField()
    private var totalRangeMaxField = TextField()
    private var timeRangeWaitingMinField = TextField()
    private var timeRangeWaitingMaxField = TextField()
    private var numWorkersField = TextField()
    private var maxQueueSizeField = TextField()
    var adsDailyExpensesField = TextField()
    var profitPerPurchaseField = TextField()
    var salaryField = TextField()
    var stepSizeField = TextField()
    var controller = MainController()
    override val root =
        vbox {
            label(title) {
                vboxConstraints {
                    marginTop = 10.0
                    marginLeft = 10.0
                    marginRight = 10.0
                }
                style { fontSize = 16.pt }
            }
            // Step setting & button
            hbox {
                vboxConstraints {
                    marginTop = 10.0
                    marginBottom = 20.0
                    marginLeft = 10.0
                    marginRight = 10.0
                }
                button("Шаг") {
                    hboxConstraints { marginRight = 20.0 }
                    action {
                        when (val stepSize = stepSizeField.text?.toIntOrNull()) {
                            null -> error("Значение в поле \"размер поля\" должно быть целым числом")
                            else -> controller.step(stepSize)
                        }
                    }
                    style { fontSize = 12.pt }
                }
                label("Размер шага (в минутах)") {
                    hboxConstraints { marginRight = 10.0 }
                    style { fontSize = 12.pt }
                }
                stepSizeField = textfield("100")
            }
            // Modelling params
            vbox {
                hbox {
                    vbox {
                        hboxConstraints { marginRight = 10.0 }
                        label("Мин. время на кассе")
                        timeRangeAtCDMinField = textfield("1")
                    }
                    vbox {
                        hboxConstraints { marginRight = 10.0 }
                        label("Макс. время на кассе")
                        timeRangeAtCDMaxField = textfield("7")
                    }
                    vbox {
                        hboxConstraints { marginRight = 10.0 }
                        label("Число касс")
                        numWorkersField = textfield("10")
                    }
                    vbox {
                        hboxConstraints { marginRight = 10.0 }
                        label("Макс. размер очереди")
                        maxQueueSizeField = textfield("10")
                    }
                }
                hbox {
                    vbox {
                        hboxConstraints { marginRight = 10.0 }
                        label("Мин. время между новыми клиентами")
                        timeRangeBCMinField= textfield("1")
                    }
                    vbox {
                        hboxConstraints { marginRight = 10.0 }
                        label("Макс. время между новыми клиентами")
                        timeRangeBCMaxField = textfield("3")
                    }
                    vbox {
                        hboxConstraints { marginRight = 10.0 }
                        label("Ежеднев. затраты на рекламу")
                        adsDailyExpensesField = textfield("14000")
                    }
                }
                hbox {
                    vbox {
                        hboxConstraints { marginRight = 10.0 }
                        label("Мин. сумма чека")
                        totalRangeMinField = textfield("100")
                    }
                    vbox {
                        hboxConstraints { marginRight = 10.0 }
                        label("Макс. сумма чека")
                        totalRangeMaxField = textfield("10000")
                    }
                    vbox {
                        hboxConstraints { marginRight = 10.0 }
                        label("Мин. время ожидания")
                        timeRangeWaitingMinField = textfield("1")
                    }
                    vbox {
                        hboxConstraints { marginRight = 10.0 }
                        label("Макс. время ожидания")
                        timeRangeWaitingMaxField = textfield("7")
                    }
                }
                hbox {
                    vbox {
                        hboxConstraints { marginRight = 10.0 }
                        label("Процент прибыли с покупки")
                        profitPerPurchaseField = textfield("9")
                    }
                    vbox {
                        hboxConstraints { marginRight = 10.0 }
                        label("Зарплата одного продавца")
                        salaryField = textfield("1500")
                    }
                }
                button("Изменить параметры (с перезапуском эксперимента)") {
                    // Validation & applying params
                    action {
                        when (val newValue = timeRangeAtCDMinField.text?.toIntOrNull()) {
                            null -> error(intFieldErrorMessage)
                            else -> controller.updateParams {
                                timeRangeAtCD = IntSampler(newValue, timeRangeAtCD.max)
                            }
                        }
                        when (val newValue = timeRangeAtCDMaxField.text?.toIntOrNull()) {
                            null -> error(intFieldErrorMessage)
                            else -> controller.updateParams {
                                timeRangeAtCD = IntSampler(timeRangeAtCD.min, newValue)
                            }
                        }
                        when (val newValue = timeRangeBCMinField.text?.toIntOrNull()) {
                            null -> error(intFieldErrorMessage)
                            else -> controller.updateParams {
                                timeRangeBetweenCustomers = IntSampler(newValue, timeRangeBetweenCustomers.max)
                            }
                        }
                        when (val newValue = timeRangeBCMaxField.text?.toIntOrNull()) {
                            null -> error(intFieldErrorMessage)
                            else -> controller.updateParams {
                                timeRangeBetweenCustomers = IntSampler(timeRangeBetweenCustomers.min, newValue)
                            }
                        }
                        when (val newValue = totalRangeMinField.text?.toIntOrNull()) {
                            null -> error(intFieldErrorMessage)
                            else -> controller.updateParams {
                                totalRange = IntSampler(newValue, totalRange.max)
                            }
                        }
                        when (val newValue = totalRangeMaxField.text?.toIntOrNull()) {
                            null -> error(intFieldErrorMessage)
                            else -> controller.updateParams {
                                totalRange = IntSampler(totalRange.min, newValue)
                            }
                        }
                        when (val newValue = timeRangeWaitingMinField.text?.toIntOrNull()) {
                            null -> error(intFieldErrorMessage)
                            else -> controller.updateParams {
                                timeRangeWaiting = IntSampler(newValue, timeRangeWaiting.max)
                            }
                        }
                        when (val newValue = timeRangeWaitingMaxField.text?.toIntOrNull()) {
                            null -> error(intFieldErrorMessage)
                            else -> controller.updateParams {
                                timeRangeWaiting = IntSampler(timeRangeWaiting.min, newValue)
                            }
                        }
                        when (val newValue = numWorkersField.text?.toIntOrNull()) {
                            null -> error(intFieldErrorMessage)
                            else -> controller.updateParams { numWorkers = newValue }
                        }
                        when (val newValue = maxQueueSizeField.text?.toIntOrNull()) {
                            null -> error(intFieldErrorMessage)
                            else -> controller.updateParams { maxQueueSize = newValue }
                        }
                        when (val newValue = adsDailyExpensesField.text?.toIntOrNull()) {
                            null -> error(intFieldErrorMessage)
                            else -> controller.updateParams { adsDailyExpenses7K = newValue / 7000 }
                        }
                        when (val newValue = profitPerPurchaseField.text?.toIntOrNull()) {
                            null -> error(intFieldErrorMessage)
                            else -> controller.updateParams { profitPerPurchase = newValue.toDouble() / 100 }
                        }
                        when (val newValue = salaryField.text?.toIntOrNull()) {
                            null -> error(intFieldErrorMessage)
                            else -> controller.updateParams { salary = newValue }
                        }

                    }
                    vboxConstraints {
                        marginTop = 5.0
                    }
                    style { fontSize = 12.pt }
                }
                vboxConstraints {
                    marginLeft = 10.0
                    marginRight = 10.0
                }
            }
            // Stats
            hbox {
                vboxConstraints {
                    marginBottom = 20.0
                    marginLeft = 10.0
                    marginRight = 10.0
                }
                vbox {
                    label("Статистика за всё время") {
                        addClass(Styles.heading)
                        style { fontSize = 12.pt }
                    }
                    listview(controller.stats) {
                        style { minWidth = 400.px }
                    }
                }
                vbox {
                    hboxConstraints { marginLeft = 10.0 }
                    label("Статистика за предыдущий день") {
                        addClass(Styles.heading)
                        style { fontSize = 12.pt }
                    }
                    listview(controller.statsDaily) {style { minWidth = 400.px }}
                }
            }
            // Awaiting customers queue
            vbox {
                hbox {
                    vboxConstraints {
                        marginLeft = 10.0
                        marginRight = 10.0
                    }
                    label("Очередь") {
                        style { fontSize = 12.pt }
                    }
                    label {
                        bind(controller.queueSize)
                        style { fontSize = 12.pt }
                    }
                }
                text {
                    vboxConstraints {
                        marginLeft = 10.0
                        marginRight = 10.0
                        marginBottom = 20.0
                    }
                    bind(controller.queueViz)
                    style {
                        fontSize = 12.pt
                        wrapText = true
                        wrappingWidth = 500.0
                    }
                }
            }
            // Cash desks visualization
            vbox {
                label("Кассы") {
                    vboxConstraints {
                        marginBottom = 20.0
                        marginLeft = 10.0
                        marginRight = 10.0
                    }
                    style { fontSize = 12.pt }
                }
                listview(controller.cashDesksViz) {
                    style { fontSize = 12.pt }
                }
                vboxConstraints {
                    marginLeft = 10.0
                    marginRight = 10.0
                    marginBottom = 10.0
                }
            }
        }
}