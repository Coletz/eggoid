package co.eggon.eggoid

class RealmPromise<T> {
    internal var action: ((T) -> Unit)? = null
    internal var error: ((Throwable) -> Unit)? = null

    infix fun then(promise: (T) -> Unit): RealmPromise<T> {
        action = promise
        return this
    }

    infix fun onError(promise: (Throwable) -> Unit): RealmPromise<T> {
        error = promise
        return this
    }
}