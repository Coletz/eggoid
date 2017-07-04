package co.eggon.eggoid


class Nil<out A, out B>(val a: A?, val b: B?){
    data class Data2<out A, out B>(val a: A, val b: B)

    infix inline fun <R> let(block: (Data2<A, B>) -> R): R? {
        if(listOf(a, b).all { it != null }){
            return block(Data2(a!!, b!!))
        } else {
            return null
        }
    }
}

class Nil3<out A, out B, out C>(val a: A?, val b: B?, val c: C?){
    data class Data3<out A, out B, out C>(val a: A, val b: B, val c: C)

    infix inline fun <R> let(block: (Data3<A, B, C>) -> R): R? {
        if(listOf(a, b, c).all { it != null }){
            return block(Data3(a!!, b!!, c!!))
        } else {
            return null
        }
    }
}

class Nil4<out A, out B, out C, out D>(val a: A?, val b: B?, val c: C?, val d: D?){
    data class Data4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D)

    infix inline fun <R> let(block: (Data4<A, B, C, D>) -> R): R? {
        if(listOf(a, b, c, d).all { it != null }){
            return block(Data4(a!!, b!!, c!!, d!!))
        } else {
            return null
        }
    }
}