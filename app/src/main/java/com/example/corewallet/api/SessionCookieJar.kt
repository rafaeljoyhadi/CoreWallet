package com.example.corewallet.api

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

class SessionCookieJar(private val context: Context) : CookieJar {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("session_cookies", Context.MODE_PRIVATE)
    private val cookieStore: ConcurrentHashMap<String, List<Cookie>> = ConcurrentHashMap()

    init {
        loadCookies()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        val validCookies = cookies.filter { it.expiresAt > System.currentTimeMillis() || it.persistent }
        if (validCookies.isNotEmpty()) {
            cookieStore[host] = validCookies
            for (cookie in validCookies) {
                Log.d("SessionCookieJar", "[SAVE] Saved cookie for $host: $cookie")
            }
            saveCookies()
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host
        Log.d("SessionCookieJar", "[LOAD] Loading cookies for $url (host: $host)")
        val cookies = cookieStore[host]?.filter { cookie ->
            (cookie.expiresAt > System.currentTimeMillis() || cookie.persistent) &&
                    cookie.matches(url)
        } ?: emptyList()

        for (cookie in cookies) {
            Log.d("SessionCookieJar", "[LOAD] Sending: $cookie")
        }
        return cookies
    }

    private fun saveCookies() {
        val editor = sharedPreferences.edit()
        cookieStore.forEach { (host, cookies) ->
            val cookieStrings = cookies.map { it.toString() }.toSet()
            editor.putStringSet(host, cookieStrings)
        }
        editor.apply()
        Log.d("SessionCookieJar", "[SAVE] Persisted cookies to SharedPreferences")
    }

    private fun loadCookies() {
        sharedPreferences.all.forEach { (host, value) ->
            if (value is Set<*>) {
                val cookies = value.mapNotNull { cookieStr ->
                    val dummyUrl = HttpUrl.Builder().scheme("http").host(host).build()
                    Cookie.parse(dummyUrl, cookieStr as String)?.takeIf { it.expiresAt > System.currentTimeMillis() }
                }
                if (cookies.isNotEmpty()) {
                    cookieStore[host] = cookies
                    Log.d("SessionCookieJar", "[LOAD] Loaded cookies for $host: $cookies")
                }
            }
        }
    }

    fun clearCookies() {
        cookieStore.clear()
        sharedPreferences.edit().clear().apply()
        Log.d("SessionCookieJar", "[CLEAR] Cookies cleared")
    }
}