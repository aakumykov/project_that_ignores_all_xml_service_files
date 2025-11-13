package com.github.aakumykov.project_that_ignores_all_xml_service_files

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.github.aakumykov.project_that_ignores_all_xml_service_files.databinding.ActivityMainBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    private fun logD(text: String) { Log.d(TAG, text) }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        runCoroutines()
    }

    private var currentJob: Job? = null

    private fun runCoroutines() {
        logD("")
        logD("----------- runCoroutines() -----------")
        lifecycleScope.launch {
//            delayWithIndex(1, 1000)
//            delayWithIndex(2, 1000)
//            delayWithIndex(3, 1000)

//            simpleDelay(1000)
//            simpleDelay(1000)
//            simpleDelay(1000)

            delay(1000)
            delay(1000)
            delay(1000)
        }
        logD("---------------------------------------")
    }

    suspend fun simpleDelay(ms: Int) {
        return suspendCoroutine { continuation ->
            TimeUnit.MILLISECONDS.sleep(ms.toLong())
            continuation.resume(Unit)
        }
    }

    /**
     * @return Количество миллисекунд фактического ожидания.
     */
    private suspend fun delayWithIndex(index: Int, timeoutMs: Int): Int {
        val funName = "Ожидание($index)"
        logD(funName)
        return suspendCancellableCoroutine { cancellableContinuation ->
            cancellableContinuation.invokeOnCancellation { throwable ->
                cancellableContinuation.resumeWithException(throwable ?: Exception("$funName отменено"))
            }
            repeat(timeoutMs) { ms ->
                if (cancellableContinuation.isActive) {
                    TimeUnit.MILLISECONDS.sleep(1)
                } else {
                    cancellableContinuation.resume(ms)
                }
            }
        }
    }
}