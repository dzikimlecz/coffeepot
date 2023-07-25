package me.dzikimlecz.coffeepot

private typealias Listener<T> = (T, T) -> Unit

interface Observable<T> {

    val value: T
    fun registerListener(listener: Listener<T>)

    fun dropListeners()
}


class MutableObservable<T>(value: T) : Observable<T> {
    override var value: T = value
        set(new) {
            if (new == field) {
                return
            }
            val old = field
            field = new
            for (f in changeListeners) {
                f(old, new)
            }
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

    override fun registerListener(listener: Listener<T>) {
        changeListeners += listener
    }

    override fun dropListeners() = changeListeners.clear()

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

fun<E> observableListOf(): ObservableList<E> =
    observableMutableListOf()

fun<E> observableListOf(vararg elements: E): ObservableList<E> =
    observableMutableListOf<E>().apply { addAll(elements) }

fun<E> observableMutableListOf(): ObservableMutableList<E> =
    ObservableArrayList()

fun<E> observableMutableListOf(vararg elements: E) =
    observableMutableListOf<E>().apply { addAll(elements) }

interface ObservableList<E> : List<E> {

    override fun subList(fromIndex: Int, toIndex: Int): ObservableList<E>
    fun dropAllListeners()
    fun dropOnAddedListeners()
    fun dropOnRemovedListeners()
    fun dropOnClearedListeners()
    fun registerOnElementAdded(listener: (E) -> Unit)
    fun registerOnElementRemoved(listener: (E) -> Unit)
    fun registerOnCleared(listener: () -> Unit)

}

interface ObservableMutableList<E> : MutableList<E>, ObservableList<E> {
    override fun subList(fromIndex: Int, toIndex: Int): ObservableMutableList<E>
}

class ObservableArrayList<E> : ObservableMutableList<E> {

    private val container = ArrayList<E>()
    override val size: Int
        get() = container.size

    override fun contains(element: E): Boolean =
        element in container


    override fun containsAll(elements: Collection<E>): Boolean =
        elements.containsAll(elements)


    override fun get(index: Int): E =
        container[index]


    override fun indexOf(element: E): Int =
        container.indexOf(element)

    override fun isEmpty(): Boolean =
        container.isEmpty()

    override fun iterator(): MutableIterator<E> = listIterator()

    override fun lastIndexOf(element: E): Int =
        container.lastIndexOf(element)


    override fun add(element: E): Boolean =
        container.add(element).also { fireAdded(element) }


    override fun add(index: Int, element: E) =
        container.add(index, element).also { fireAdded(element) }

    override fun addAll(index: Int, elements: Collection<E>): Boolean =
        container.addAll(index, elements).also {
            if (it) {
                elements.forEach(::fireAdded)
            }
        }

    override fun addAll(elements: Collection<E>): Boolean =
        container.addAll(elements).also {
            if (it) {
                elements.forEach(::fireAdded)
            }
        }

    override fun clear() {
        val copy = container.toList()
        container.clear()
        for (e in copy) {
            fireRemoved(e)
        }
        fireCleared()
    }

    override fun listIterator(): MutableListIterator<E> =
        container.listIterator()  // FIXME: does not fire listeners on change

    override fun listIterator(index: Int): MutableListIterator<E> =
        container.listIterator(index) // FIXME: does not fire listeners on change

    override fun remove(element: E): Boolean =
        container.remove(element).also {
            if (it) {
                fireRemoved(element)
            }
        }


    override fun removeAll(elements: Collection<E>): Boolean =
        container.removeAll(elements.toSet()).also {
            if (it) {
                elements.forEach(::fireRemoved)
            }
        }

    override fun removeAt(index: Int): E =
        container.removeAt(index).also {
            fireRemoved(it)
        }

    override fun retainAll(elements: Collection<E>): Boolean {
        val removed = container - elements.toSet()
        return container.retainAll(elements).also {
            removed.forEach(::fireRemoved)
        }
    }

    override fun set(index: Int, element: E): E {
        val old = container[index]
        container[index] = element
        fireRemoved(old)
        fireAdded(element)
        return old
    }

    override fun subList(
        fromIndex: Int, toIndex: Int
    ): ObservableMutableList<E> {
        val new = ObservableArrayList<E>()
        new.container.addAll(container.subList(fromIndex, toIndex))
        return new
    }

    private val onAddedListeners = mutableListOf<(E) -> Unit>()
    private val onRemovedListeners = mutableListOf<(E) -> Unit>()
    private val onClearedListeners = mutableListOf<() -> Unit>()

    private fun fireAdded(e: E) {
        for (listener in onAddedListeners) {
            listener(e)
        }
    }
    private fun fireRemoved(e: E) {
        for (listener in onRemovedListeners) {
            listener(e)
        }
    }

    private fun fireCleared() {
        for (listener in onClearedListeners) {
            listener()
        }
    }

    override fun dropAllListeners() {
        dropOnAddedListeners()
        dropOnRemovedListeners()
        dropOnClearedListeners()
    }

    override fun dropOnAddedListeners() {
        onAddedListeners.clear()
    }

    override fun dropOnRemovedListeners() {
        onRemovedListeners.clear()
    }

    override fun dropOnClearedListeners() {
        onClearedListeners.clear()
    }

    override fun registerOnElementAdded(listener: (E) -> Unit) {
        onAddedListeners += listener
    }

    override fun registerOnElementRemoved(listener: (E) -> Unit) {
        onRemovedListeners += listener
    }

    override fun registerOnCleared(listener: () -> Unit) {
        onClearedListeners += listener
    }
}
