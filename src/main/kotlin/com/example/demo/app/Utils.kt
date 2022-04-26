package com.example.demo.app

import kotlin.random.Random

fun Double.format(digits: Int) = "%.${digits}f".format(this)

class IntSampler(val min: Int, val max: Int) {
    fun next() = Random.nextInt(min, max)
}