package com.github.aakumykov.project_that_ignores_all_xml_service_files

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.github.aakumykov.project_that_ignores_all_xml_service_files.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    private fun logD(text: String) { Log.d(TAG, text) }
    private fun logW(text: String) { Log.w(TAG, text) }
    private fun logE(text: String) { Log.e(TAG, text) }

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

        binding.button1.setOnClickListener { runCoroutines() }
        binding.cancelButton.setOnClickListener { cancelCurrentJob() }

        runCoroutines()
    }

    private var currentJob: Job? = null

    private fun runCoroutines() {
        logD("")
        logD("----------- runCoroutines() -----------")

        val eh = CoroutineExceptionHandler { context, throwable ->
            logE(throwable.message ?: throwable.javaClass.simpleName)
        }

        val d = Dispatchers.IO

        currentJob = lifecycleScope.launch (eh + d) {
//            delayWithIndex(1, 1000)
//            delayWithIndex(2, 1000)
//            delayWithIndex(3, 1000)

            simpleDelay("Ожидание-1()", 1)
            simpleDelay("Ожидание-2()", 1)
            simpleDelay("Ожидание-3()", 1)

//            delay(1000)
//            delay(1000)
//            delay(1000)
        }
        logD("---------------------------------------")
    }

    private fun cancelCurrentJob() {
        currentJob?.cancel(CancellationException("Отменено пользователем"))
            ?: run { "Нет текущей задачи".also {
                logW(it)
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            } }
    }

    suspend fun simpleDelay(comment: String, sec: Int) {
        logD("${comment} ($sec сек)")

        return suspendCancellableCoroutine { continuation ->

            val context = continuation.context
            val job = context.job

            logD("перед repeat{continuation-${continuation.hashCode()},context-${context.hashCode()},job-${job.hashCode()}}")
            repeat(sec * 5) {
                logD("continuation: isActive=${continuation.isActive}, isCompleted=${continuation.isCompleted}, isCancelled=${continuation.isCancelled}")
                if (continuation.isActive) TimeUnit.MILLISECONDS.sleep(500)
                else continuation.resume(Unit)
            }
            logD("после repeat{continuation-${continuation.hashCode()},context-${context.hashCode()},job-${job.hashCode()}}")

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