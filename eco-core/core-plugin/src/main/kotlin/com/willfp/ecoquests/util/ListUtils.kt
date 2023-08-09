package com.willfp.ecoquests.util

fun <T> Collection<T>.randomlyPick(amount: Int): List<T> {
    val list = this.toMutableList()
    val picked = mutableListOf<T>()

    repeat(amount) {
        val index = (0 until list.size).random()
        picked.add(list[index])
        list.removeAt(index)
    }

    return picked
}
