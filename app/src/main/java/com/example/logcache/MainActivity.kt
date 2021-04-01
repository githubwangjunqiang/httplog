package com.example.logcache

import android.content.Context
import android.hardware.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.xq.app.cachelog.LogCacheManager
import kotlinx.coroutines.GlobalScope
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
}