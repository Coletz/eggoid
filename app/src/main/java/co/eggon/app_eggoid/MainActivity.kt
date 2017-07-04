package co.eggon.app_eggoid

import android.app.Activity
import android.os.Bundle
import co.eggon.eggoid.Nil3
import co.eggon.eggoid.extension.error

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var x: Int? = null
        var y: Int? = null
        var z: Int = 3

        Nil3(x, y, z) let { (a, b, c) ->
            (a + b + c).error()
        } ?: run {
            "NOPE".error()
        }
    }
}
