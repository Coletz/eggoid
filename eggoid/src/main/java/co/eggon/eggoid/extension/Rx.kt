package co.eggon.eggoid.extension

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody

/**
 * Observable utilities
 **/
fun <T> Observable<T>.network(bag: CompositeDisposable, onNext: Consumer<T>, onError: Consumer<Throwable>, onComplete: Action? = null, onSubscribe: Consumer<Disposable>? = null): Disposable {
    return onUi().async().let {
        onSubscribe?.let {
            subscribe(onNext, onError, onComplete, it)
        } ?: onComplete?.let {
            subscribe(onNext, onError, it)
        } ?: run {
            subscribe(onNext, onError)
        }.also { bag.add(it) }
    }
}

fun <T> Observable<T>.network(bag: CompositeDisposable, onNext: ((T) -> Unit), onError: ((Throwable) -> Unit), onComplete: (() -> Unit)? = null, onSubscribe: ((Disposable) -> Unit)? = null): Disposable {
    return onUi().async().let { obs ->
        onSubscribe?.let {
            obs.subscribe(onNext, onError, onComplete, it)
        } ?: onComplete?.let {
            obs.subscribe(onNext, onError, it)
        } ?: run {
            obs.subscribe(onNext, onError)
        }.also { bag.add(it) }
    }
}

/**
 * Execute the "subscribe" block (Conusmer) on the main thread, aka UI thread
 **/
fun <T> Observable<T>.onUi(): Observable<T> = observeOn(AndroidSchedulers.mainThread())

/**
 * Execute the observable on a new thread.
 * Note: you can't execute any graphical change from thread different from the UI thread
 **/
fun <T> Observable<T>.async(): Observable<T> = subscribeOn(Schedulers.newThread())

/**
 * Create a request with text/plain media type
 **/
fun String.plainTextRequest(): RequestBody = RequestBody.create(MediaType.parse("text/plain"), this)