package com.example.logcache

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.hardware.*
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.UserManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.xq.app.cachelog.LogCacheManager
import com.xq.app.cachelog.utils.format
import com.xq.app.cachelog.utils.show
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request


class MainActivity : AppCompatActivity(), SensorEventListener2 {
    private var accelerometerValues: FloatArray? = FloatArray(3)
    private var magneticFieldValues: FloatArray? = FloatArray(3)
    private var r: FloatArray = FloatArray(9)
    private var values: FloatArray = FloatArray(3)


    private var img: ImageView? = null
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mMagneticField: Sensor? = null
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addNetworkInterceptor(LogCacheInterceptor())
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.decorView?.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN and
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE and
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        setContentView(R.layout.activity_main)

        LogCacheManager.initContext(this, "12345")
        img = findViewById(R.id.imageview)


        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticField = mSensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


//        lifecycleScope.launchWhenResumed {
//            while (isActive){
//                delay(60)
////                img?.rotationX = values[0]
////                img?.rotationY = values[1]
//                img?.rotation =  -values[0]
//                Log.d("12345", "onSensorChanged-0:${values[0]} ")
//                Log.d("12345", "onSensorChanged-1:${values[1]} ")
//                Log.d("12345", "onSensorChanged-2:${values[2]} ")
//            }
//        }

        img?.setOnClickListener {
            GlobalScope.launch {
                val newCall = okHttpClient.newCall(
                    Request.Builder()
                        .addHeader("cockroach", "requestHeader")
                        .url("http://test.qushiwan.cn/imoney/app/api/system/v2/getVersionInfo")
                        .post(
                            FormBody.Builder()
                                .add("channelCode", "itest")
                                .build()
                        )
                        .build()
                )
                val execute = newCall.execute()
                val string = execute.body?.string() ?: "返回是空的"
                Log.d("12345", "请求接口返回了$string")

            }
        }


    }

    override fun onResume() {
        super.onResume()


        mSensorManager?.let {
            mAccelerometer?.run {
                it.registerListener(this@MainActivity, this, SensorManager.SENSOR_DELAY_NORMAL)
            }
            mMagneticField?.run {
                it.registerListener(this@MainActivity, this, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    //sensor data
    override fun onPause() {
        super.onPause()
        mSensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        if (sensorEvent?.sensor?.type === Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = sensorEvent?.values
        }
        if (sensorEvent?.sensor?.type === Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues = sensorEvent?.values
        }
        SensorManager.getRotationMatrix(r, null, accelerometerValues, magneticFieldValues)
        SensorManager.getOrientation(r, values)
        values[0] = Math.toDegrees(values[0].toDouble()).toFloat();


    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onFlushCompleted(sensor: Sensor?) {
    }

    override fun onDestroy() {
        Log.d("12345", "onDestroy: MainActivity")
        super.onDestroy()
    }

    fun doClickLog(view: View) {

        LogCacheManager.showLogActivity(this)
    }

    fun doClickProvider(view: View) {

        startActivity(Intent(this, ContentProviderActivity::class.java))
    }

    private val myCoroutine = MyCo()

    fun testYourOwnCoroutine(view: View) {


        val launch = myCoroutine.launch {
            Log.d("12345", "testYourOwnCoroutine:start-5000 ,th=${Thread.currentThread().name}")
            delay(5000)
            Log.d("12345", "testYourOwnCoroutine:5000 ,th=${Thread.currentThread().name}")
        }
        val launch1 = myCoroutine.launch {
            Log.d("12345", "testYourOwnCoroutine:start-7000 ,th=${Thread.currentThread().name}")
            delay(7000)
            Log.d("12345", "testYourOwnCoroutine:7000  ,th=${Thread.currentThread().name}")
        }
        val launch2 = myCoroutine.launch {
            Log.d("12345", "testYourOwnCoroutine:start-3000 ,th=${Thread.currentThread().name}")
            delay(3000)
            Log.d("12345", "testYourOwnCoroutine:3000  ,th=${Thread.currentThread().name}")
        }

        Log.d("12345", "testYourOwnCoroutine:over  ,th=${Thread.currentThread().name}")
        launch.cancel()
        Log.d("12345", "testYourOwnCoroutine:cancel  ,th=${Thread.currentThread().name}")


    }

    fun doClickFull(view: View) {
        startActivity(Intent(this, FullscreenActivity::class.java))
    }

    fun doClickuserstats(view: View) {
        if (!havePermissionForTest(this)) {
            goToSettingIntent(this)
            return
        }
        val usermananger: UserManager = this.getSystemService(Context.USER_SERVICE) as UserManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val userUnlocked = usermananger.isUserUnlocked
            Log.d("12345", "userUnlocked: ${userUnlocked}")
            Log.d("12345", "isSystemUser: ${usermananger.isSystemUser}")
        }


        val currentTimeMillis = System.currentTimeMillis()

        val systemService: UsageStatsManager = this
            .getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val queryEvents =
            systemService.queryEvents(1619077116000, 1619077147000)
//            systemService.queryEvents(currentTimeMillis - 1000 * 60 * 60, currentTimeMillis)


        if (!queryEvents.hasNextEvent()) {
            "没有查询到任何数据".show()
            return
        }
        val eventOut = UsageEvents.Event()

        var time = 0L
        var startTime = 1619077116000
        var pauseType = 0


        while (queryEvents.getNextEvent(eventOut)) {
            val packageName = eventOut.packageName
            val timeStamp = eventOut.timeStamp
            val eventType = eventOut.eventType
            if ("com.maxgames.stickwarlegacy" != eventOut.packageName) {
                Log.d("12345", "包名不对:${eventOut.packageName} ")
                continue
            }
            when (eventType) {
                UsageEvents.Event.ACTIVITY_PAUSED -> {
                    if (pauseType == eventType) {
                        continue
                    }
                    time += timeStamp - startTime
                    pauseType = eventType
//                    showPause = true
//
//                    if (timeResumed < timesmorning) {
//                        allTime -= (timesmorning - timeResumed)
//                    }
//                    if (allTime < 0) {
//                        allTime = 0
//                    }
                }
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    startTime = timeStamp
                    pauseType = eventType
                }
            }
            Log.d(
                "12345",
                "doClickuserstats:${packageName}--${timeStamp.format()}:{${timeStamp}}--${eventType} "
            )
            Log.d("12345", "time:${time} ")
        }





        Log.d("12345", "alltime:${time} ")
    }

    fun havePermissionForTest(context: Context): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val packageManager = context.packageManager
                val applicationInfo = packageManager.getApplicationInfo(context.packageName, 0)
                val appOpsManager =
                    context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val mode = appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid,
                    applicationInfo.packageName
                )
                mode == AppOpsManager.MODE_ALLOWED
            } else {
                true
            }
        } catch (e: Exception) {
            true
        }
    }

    fun goToSettingIntent(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            try {
                val intent = Intent(Settings.ACTION_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e1: java.lang.Exception) {
                e1.printStackTrace()
                e1.message.show()
            }
        }
    }
}