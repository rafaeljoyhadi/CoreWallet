import android.content.Context
import android.util.Log
import okhttp3.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class SessionCookieJar(private val context: Context) : CookieJar {

    private val sharedPreferences = context.getSharedPreferences("session_cookies", Context.MODE_PRIVATE)

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val editor = sharedPreferences.edit()
        val cookieStrings = HashSet<String>()

        for ((index, cookie) in cookies.withIndex()) {
            val cookieString = cookie.toString()
            Log.d("SessionCookieJar", "[SAVE] Saved cookie: $cookieString")
            cookieStrings.add(cookieString)
        }

        editor.putStringSet("cookies", cookieStrings)
        editor.apply()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookiesSet = sharedPreferences.getStringSet("cookies", emptySet())
        val cookies = mutableListOf<Cookie>()

        Log.d("SessionCookieJar", "[LOAD] Loading cookies for $url")

        for (cookieStr in cookiesSet!!) {
            val cookie = Cookie.parse(url, cookieStr)
            if (cookie != null) {
                cookies.add(cookie)
                Log.d("SessionCookieJar", "[LOAD] Sending: $cookieStr")
            }
        }

        return cookies
    }

    fun clearCookies() {
        sharedPreferences.edit().remove("cookies").apply()
        Log.d("SessionCookieJar", "[CLEAR] Cookies cleared")
    }
}