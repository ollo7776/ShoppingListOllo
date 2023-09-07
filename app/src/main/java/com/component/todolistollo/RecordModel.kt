package com.component.todolistollo

import java.util.*

class RecordModel (
    var id: Int = getAutoId(),
    var title: String = "",
    var strikeTrough: Int = 0,
    var recordMarked: Int = 0,
) {
    companion object {
        fun getAutoId(): Int {
            val random = Random()
            return random.nextInt(100)
        }
    }
}