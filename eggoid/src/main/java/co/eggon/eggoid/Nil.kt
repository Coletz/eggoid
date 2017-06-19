package co.eggon.eggoid

class Nil(private vararg val objs: Any?){

    infix fun <R> let(block: (Nil) -> R): R? {
        return if(objs.all { it != null }){
            block(this)
        } else {
            null
        }
    }

    operator fun component1(): Any? = objs[0]
    operator fun component2(): Any? = objs[1]
    operator fun component3(): Any? = objs[2]
    operator fun component4(): Any? = objs[3]
    operator fun component5(): Any? = objs[4]
    operator fun component6(): Any? = objs[5]
    operator fun component7(): Any? = objs[6]
    operator fun component8(): Any? = objs[7]
    operator fun component9(): Any? = objs[8]
    operator fun component10(): Any? = objs[9]
    operator fun component11(): Any? = objs[10]
    operator fun component12(): Any? = objs[11]
    operator fun component13(): Any? = objs[12]
    operator fun component14(): Any? = objs[13]
    operator fun component15(): Any? = objs[14]
    operator fun component16(): Any? = objs[15]
    operator fun component17(): Any? = objs[16]
    operator fun component18(): Any? = objs[17]
    operator fun component19(): Any? = objs[18]
    operator fun component20(): Any? = objs[19]
}