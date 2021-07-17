package com.singularitycoder.recurringtasks1

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.singularitycoder.recurringtasks1.databinding.ActivityMainBinding
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// https://stackoverflow.com/questions/11434056/how-to-run-a-method-every-x-seconds
// https://stackoverflow.com/questions/1877417/how-to-set-a-timer-in-android
// https://stackoverflow.com/questions/14376470/scheduling-recurring-task-in-android
class MainActivity : AppCompatActivity() {

    // Use Alarm Manager to run code at a specific time, even if your app is not currently running. Alarm Manager holds a CPU wake lock as long as the alarm receiver's onReceive() method is executing. So phone wont sleep until broadcast's onReceive returns.
    // Use handler.postAtTime(runnable, System.currentTimeMillis() + interval) to run something at a particular time in the future.

    companion object {
        private const val REPEAT_DURATION_IN_MILLIS = 5000L // 5 sec
        private const val DELAY_IN_MILLIS = 2000L // 2 sec
        private val MAX_DURATION_IN_MILLIS = TimeUnit.MINUTES.toMillis(1) // 60 sec
    }

    private lateinit var binding: ActivityMainBinding

    val viewModel by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTimer.setOnClickListener {
            binding.tvResult.text = ""
            updateTimeEvery5SecondsWithTimer()
        }

        binding.btnCountDownTimer.setOnClickListener {
            binding.tvResult.text = ""
            getDataEvery5SecondWithCountDownTimer()
        }

        binding.btnHandler.setOnClickListener {
            binding.tvResult.text = ""
            getDataEvery5SecondWithHandler()
        }

        binding.btnScheduledThreadPoolExecutor.setOnClickListener {
            binding.tvResult.text = ""
            getDataEvery5SecondWithScheduledThreadPoolExecutor()
        }

        binding.btnObservableInterval.setOnClickListener {
            binding.tvResult.text = ""
            getDataEvery5SecondWithObservableInterval()
        }

        binding.btnObservableTimer.setOnClickListener {
            binding.tvResult.text = ""
            getDataEvery5SecondWithObservableTimer()
        }

//        if (!(this::viewModel.getDelegate() as Lazy<*>).isInitialized()) return

        viewModel.timerFlag.observe(this, androidx.lifecycle.Observer {
            val currentDateTime = SimpleDateFormat("dd MMMM yyyy, hh:mm:ss a", Locale.ROOT).format(Date())
            binding.tvResult.text = "${binding.tvResult.text} ${currentDateTime.plus(": Got Data \n\n")}"
        })
    }

    private fun updateTimeEvery5SecondsWithTimer() {
        // Timer creates new thread, so its heavy. For repeating tasks use Timer().scheduleAtFixedRate(). For single run after delay use Timer().schedule()
        Timer().scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    // background thread operation. Gets called every "REPEAT_DURATION_IN_MILLIS" duration.
                    viewModel.setTimerFlag(true)
                }
            },
            DELAY_IN_MILLIS,    // On Click it waits for "DELAY_IN_MILLIS" duration and then starts fetching data every "REPEAT_DURATION_IN_MILLIS" duration
            REPEAT_DURATION_IN_MILLIS   // Repeat duration
        )

        // Handle this in onDestroy() - Timer().cancel()
    }

    private fun getDataEvery5SecondWithCountDownTimer() {
        // This is good for controlling how long we want to repeat something "MAX_DURATION_IN_MILLIS" duration. It stops after that
        object : CountDownTimer(MAX_DURATION_IN_MILLIS, REPEAT_DURATION_IN_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                // This method is called every "REPEAT_DURATION_IN_MILLIS" duration. This method stops getting called after "MAX_DURATION_IN_MILLIS" is over.
                viewModel.setTimerFlag(true)
                println("${(MAX_DURATION_IN_MILLIS - millisUntilFinished) / 1000} sec elapsed")
            }

            override fun onFinish() {
                // This method is called after "MAX_DURATION_IN_MILLIS" duration is complete
                Snackbar.make(binding.root, "Finished recurring task!", Snackbar.LENGTH_SHORT).show()
            }
        }.start()
    }

    private fun getDataEvery5SecondWithHandler() {
        // Starts after assigned delay
        val handler = Handler()

        // Recursive Func
        val runnable1 = Runnable {
            viewModel.setTimerFlag(true)
            getDataEvery5SecondWithHandler()
        }

        // Recursive Runnable
        val runnable2 = object : Runnable {
            override fun run() {
                viewModel.setTimerFlag(true)
                handler.postDelayed(this, REPEAT_DURATION_IN_MILLIS)
            }
        }

        handler.postDelayed(runnable2, REPEAT_DURATION_IN_MILLIS)

        // Handle this in onDestroy() - handler.removeCallbacks(runnable2)
    }

    private fun getDataEvery5SecondWithScheduledThreadPoolExecutor() {
        Executors.newSingleThreadScheduledExecutor().apply {
            scheduleAtFixedRate(
                Runnable {
                    // Background thread operation
                    viewModel.setTimerFlag(true)
                },
                DELAY_IN_MILLIS,    // Starts operation after this delay only in the beginning
                REPEAT_DURATION_IN_MILLIS,  // Repeat operation duration
                TimeUnit.MILLISECONDS
            )
        }
    }

    private fun getDataEvery5SecondWithObservableInterval() {
        // Starts after "REPEAT_DURATION_IN_MILLIS" and polls every "REPEAT_DURATION_IN_MILLIS" duration
        val intervalObservable = Observable.create<Unit> { it: ObservableEmitter<Unit> -> viewModel.setTimerFlag(true) }
        Observable.interval(REPEAT_DURATION_IN_MILLIS, TimeUnit.MILLISECONDS)
            .flatMap(Function<Long, ObservableSource<out Any>> { it: Long -> intervalObservable }) // poll data here
            .subscribeOn(Schedulers.newThread()) // poll data on background thread
            .observeOn(AndroidSchedulers.mainThread()) // populate data on main thread
            .subscribe() // add UI code here

        // Handle this in onDestroy() - subscription.unsubscribe()
    }

    @SuppressLint("CheckResult")
    private fun getDataEvery5SecondWithObservableTimer() {
        // Does the job after "REPEAT_DURATION_IN_MILLIS" duration and sends the result on main thread. One time task.
        Observable.timer(REPEAT_DURATION_IN_MILLIS, TimeUnit.MILLISECONDS)
            .map(Function { it: Long -> viewModel.setTimerFlag(true) })
            .subscribeOn(Schedulers.newThread()) // poll data on background thread
            .observeOn(AndroidSchedulers.mainThread()) // populate data on main thread
            .subscribe(Consumer { it: Any? -> Snackbar.make(binding.root, "Finished task!", Snackbar.LENGTH_SHORT).show() }) // add UI code here

        // Handle this in onDestroy() - subscription.unsubscribe()
    }
}