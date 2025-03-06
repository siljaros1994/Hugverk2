package `is`.hbv601.hugverk2.ui

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader.Factory
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import java.io.InputStream
import java.net.CookieManager

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val okHttpClient = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .build()
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            Factory(okHttpClient)
        )
    }
}