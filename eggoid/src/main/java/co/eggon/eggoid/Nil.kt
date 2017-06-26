package co.eggon.eggoid


class Nil<A, B>(val a: A?, val b: B?) {
    operator fun component1(): A = a!!
    operator fun component2(): B = b!!

    infix fun let(callback: (Nil<A, B>) -> Unit): Nil<A, B> {
        val objs = listOf(a, b)
        if(objs.all { it != null }){
            callback(this)
        }
        return this
    }

    infix fun empty(callback: () -> Unit) {
        if(listOf(a, b).all { it != null }){
            callback()
        }
    }
}

class Nil3<A, B, C>(val a: A?, val b: B?, val c: C?) {
    operator fun component1(): A = a!!
    operator fun component2(): B = b!!
    operator fun component3(): C = c!!

    infix fun let(callback: (Nil3<A, B, C>) -> Unit): Nil3<A, B, C> {
        if(listOf(a, b, c).all { it != null }){
            callback(this)
        }
        return this
    }

    infix fun empty(callback: () -> Unit) {
        val objs = listOf(a, b)
        if(objs.all { it != null }){
            callback()
        }
    }
}

class Nil4<A, B, C, D>(val a: A?, val b: B?, val c: C?, val d: D?) {
    operator fun component1(): A = a!!
    operator fun component2(): B = b!!
    operator fun component3(): C = c!!
    operator fun component4(): D = d!!

    infix fun let(callback: (Nil4<A, B, C, D>) -> Unit): Nil4<A, B, C, D> {
        if(listOf(a, b, c, d).all { it != null }){
            callback(this)
        }
        return this
    }

    infix fun empty(callback: () -> Unit) {
        val objs = listOf(a, b)
        if(objs.all { it != null }){
            callback()
        }
    }
}