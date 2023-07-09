package me.dzikimlecz.coffeepot

private typealias Listener<T> = (T, T) -> Unit
class MutableObservable<T>(value: T) {
    var value: T = value
        set(new) {
            if (new == field) {
                return
            }
            for (f in changeListeners) {
                f(field, new)
            }
            field = new
        }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MutableObservable<*>

        return value == other.value
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }

    private val changeListeners: MutableList<Listener<T>> = mutableListOf()

    fun registerListener(listener: Listener<T>) {
        changeListeners += listener
    }

    fun dropListeners() = changeListeners.clear()

    private var boundTo: MutableObservable<T>? = null
    val isBound = boundTo != null

    fun bind(other: MutableObservable<T>) {
        other.registerListener { _, new ->
            if(this.boundTo === other) {
                this.value = new
            }
        }
    }

    fun unbind() {
        boundTo = null
    }
}