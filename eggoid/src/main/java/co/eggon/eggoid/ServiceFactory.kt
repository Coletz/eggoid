package co.eggon.eggoid

import co.eggon.eggoid.extension.error
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.introspect.AnnotatedClass
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import com.fasterxml.jackson.databind.module.SimpleModule
import io.realm.RealmObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class ServiceFactory {

    companion object {
        private val MISSING_INIT_MSG = "You must call ServiceFactory.init(\"https://your.url.com\") before using this function!"
        private val MISSING_RETROFIT_MSG = "You must create a ServiceFactory before using it!"
        private var address: String? = null
        private var logInterceptor: Boolean = false
        private var connectionInterceptor: Boolean = false
        private var tag: String = "OkHttp"
        private var converter: Boolean = true

        private val moduleList = ArrayList<Module>()

        fun init(serverAddress: String, enableInterceptor: Boolean = logInterceptor, customTag: String = tag, enableJsonConverter: Boolean = converter, closeConnectionInterceptor: Boolean = connectionInterceptor){
            address = serverAddress
            logInterceptor = enableInterceptor
            tag = customTag
            converter = enableJsonConverter
            connectionInterceptor = closeConnectionInterceptor
        }

        fun addModule(vararg module: SimpleModule){
            module.forEach { moduleList.add(it) }
        }
    }

    internal var retrofit: Retrofit? = null

    init {
        if(address == null){

        } else {
            val bodyInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> message.error(tag) })
            bodyInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
            if(logInterceptor){
                client.addInterceptor(bodyInterceptor)
            }

            if(connectionInterceptor){
                client.addNetworkInterceptor {
                    val request = it.request().newBuilder().addHeader("Connection", "close").build()
                    it.proceed(request)
                }
            }

            val builder = Retrofit.Builder()
                    .baseUrl(address)
                    .client(client.build())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            if(converter){
                val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                moduleList.forEach {
                    mapper.registerModule(it)
                }
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                mapper.setAnnotationIntrospector(object : JacksonAnnotationIntrospector() {
                    override fun isIgnorableType(ac: AnnotatedClass?): Boolean? {
                        if (ac?.rawType == RealmObject::class.java)
                            return true
                        return super.isIgnorableType(ac)
                    }
                })
                builder.addConverterFactory(JacksonConverterFactory.create(mapper))
            }
            retrofit = builder.build()
        }
    }

    fun <T : Any> with(clazz: KClass<T>): T {
        if(address == null){
            throw Exception(MISSING_INIT_MSG)
        } else {
            return retrofit?.create(clazz.java) ?: throw Exception(MISSING_RETROFIT_MSG)
        }
    }
}