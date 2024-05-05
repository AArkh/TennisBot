package tennis.bot.mobile.core

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject
import kotlin.random.Random

class CrashTestInterceptor @Inject constructor() : Interceptor { // basic implementation

	private val interceptor = HttpLoggingInterceptor { Log.d("NETWORK", it) }.apply {
		level = HttpLoggingInterceptor.Level.BODY
	}

	override fun intercept(chain: Interceptor.Chain): Response {
//		if (Random.nextFloat() < 0.3) { // 30% chance to fail
//			return Response.Builder()
//				.request(chain.request())
//				.protocol(Protocol.HTTP_1_1)
//				.code(chooseErrorCode()) // Randomly choose between 400 and 500
//				.message("Simulated error")
//				.body("Error content".toResponseBody(null))
//				.build()
//		}

		// Proceed with the actual network call
		return interceptor.intercept(chain)
	}

	private fun chooseErrorCode(): Int {
		// You can adjust the logic here to choose between different error codes if needed
		return if (Random.nextBoolean()) 500 else 400
	}
}