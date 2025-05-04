package com.doubletapp_hw

enum class HabitPriority(val labelResId: Int) {
    LOW(R.string.low),
    MID(R.string.mid),
    HIGH(R.string.hight);
}

enum class HabitType(val labelResId: Int) {
    POSITIVE(R.string.positive),
    NEGATIVE(R.string.negative);
}

enum class SortingType(val labelResId: Int) {
    NAME(R.string.name),
    DATE(R.string.date),
    PRIORITY(R.string.priority)
}