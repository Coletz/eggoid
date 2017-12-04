package co.eggon.eggoid

import co.eggon.eggoid.extension.info
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.introspect.AnnotatedClass
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import com.fasterxml.jackson.databind.module.SimpleModule
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.realm.RealmObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class ServiceFactory(customReadTimeout: Long? = null, customWriteTimeout: Long? = null, customConnectionTimeout: Long? = null, retryOnConnectionFail: Boolean = false) {

    companion object {
        private val MISSING_INIT_MSG = "You must call ServiceFactory.init(\"https://your.url.com\") before using this function!"
        private val MISSING_RETROFIT_MSG = "You must create a ServiceFactory before using it!"
        private var address: String? = null
        private var logInterceptor: Boolean = false
        private var interceptors: ArrayList<Interceptor> = arrayListOf()
        private var networkInterceptors: ArrayList<Interceptor> = arrayListOf()
        private var factories: ArrayList<Converter.Factory> = arrayListOf()
        private var tag: String = "OkHttp"

        private var readTimeout = 30L
        private var writeTimeout = 30L
        private var connectionTimeout = 30L

        private val moduleList: ArrayList<Module>? = ArrayList()

        fun init(serverAddress: String, enableInterceptor: Boolean = logInterceptor, customTag: String = tag){
            address = serverAddress
            logInterceptor = enableInterceptor
            tag = customTag
        }

        fun addInterceptor(interceptor: Interceptor){
            interceptors.add(interceptor)
        }

        fun addNetworkInterceptor(interceptor: Interceptor){
            networkInterceptors.add(interceptor)
        }

        fun addConverterFactory(factory: Converter.Factory){
            factories.add(factory)
        }

        /**
         * This will automatically set the factory to JacksonConverterFactory
         **/

        fun addModule(vararg module: SimpleModule){
            module.forEach { moduleList?.add(it) }
        }
    }

    private var retrofit: Retrofit? = null

    /**
     * Constructor initializer
     **/
    init {
        address?.let {
            val client = OkHttpClient.Builder()
                .readTimeout(customReadTimeout ?: readTimeout, TimeUnit.SECONDS)
                .writeTimeout(customWriteTimeout ?: writeTimeout, TimeUnit.SECONDS)
                .connectTimeout(customConnectionTimeout ?: connectionTimeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
            if(logInterceptor){
                val bodyInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> message.info(tag) })
                bodyInterceptor.level = HttpLoggingInterceptor.Level.BODY
                client.addInterceptor(bodyInterceptor)
            }

            interceptors.forEach {
                client.addInterceptor(it)
            }

            networkInterceptors.forEach {
                client.addNetworkInterceptor(it)
            }

            val builder = Retrofit.Builder()
                    .baseUrl(it)
                    .client(client.build())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

            factories.forEach {
                builder.addConverterFactory(it)
            }

            retrofit = builder.build()
        } ?: throw Exception(MISSING_INIT_MSG)
    }

    fun <T : Any> with(clazz: KClass<T>): T {
        return address?.let {
            retrofit?.create(clazz.java) ?: throw Exception(MISSING_RETROFIT_MSG)
        } ?: throw Exception(MISSING_INIT_MSG)
    }

    object ConverterFactory {
        fun forJackson(withRealm: Boolean = true): Converter.Factory {
            val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            moduleList?.forEach {
                mapper.registerModule(it)
            }
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            if(withRealm){
                mapper.setAnnotationIntrospector(object : JacksonAnnotationIntrospector() {
                    override fun isIgnorableType(ac: AnnotatedClass?): Boolean? {
                        if (ac?.rawType == RealmObject::class.java)
                            return true
                        return super.isIgnorableType(ac)
                    }
                })
            }
            return JacksonConverterFactory.create(mapper)
        }

        fun forGson(): Converter.Factory {
            val gson = GsonBuilder()
            return GsonConverterFactory.create(gson.create())
        }
    }
}