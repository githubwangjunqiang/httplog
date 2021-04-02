package com.xq.app.cachelog.utils

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.xq.app.cachelog.LogCacheManager
import com.xq.app.cachelog.entiy.LogHttpCacheData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

/**
 * @data 2021/3/31
 * @user Android - 小强
 * @mailbox 980766134@qq.com
 */
/**
 * 获取 loadHttpLogData 类型
 */
fun Cursor.loadHttpLogData(): LogHttpCacheData {
    var data = LogHttpCacheData(loadLong(LogHttpCacheData.logId_key))
    data.userId = loadString(LogHttpCacheData.userId_key)
    data.url = loadString(LogHttpCacheData.url_key)
    data.durration = loadString(LogHttpCacheData.durration_key)
    data.returnHeader = loadString(LogHttpCacheData.returnHeader_key)
    data.returnHttpCode = loadString(LogHttpCacheData.returnHttpCode_key)
    data.sendHead = loadString(LogHttpCacheData.sendHead_key)
    data.sendParameter = loadString(LogHttpCacheData.sendParameter_key)
    data.returnString = loadString(LogHttpCacheData.returnString_key)
    data.customMessage = loadString(LogHttpCacheData.customMessage_key)
    return data
}

/**
 * 获取 long 类型
 */
fun Cursor.loadLong(columnName: String): Long {
    val columnIndex = getColumnIndex(columnName)
    if (columnIndex != -1) {
        return getLongOrNull(columnIndex) ?: 0
    }
    return 0
}

/**
 * 获取 String 类型
 */
fun Cursor.loadString(columnName: String): String? {
    val columnIndex = getColumnIndex(columnName)
    if (columnIndex != -1) {
        return getStringOrNull(columnIndex)
    }
    return null
}

/**
 * 格式化时间
 */
fun Long?.format(): String {
    return SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(this ?: 0)
}

/**
 * 字符串 土司
 */
fun String?.show() {
    val string = this ?: ""
    GlobalScope.launch(Dispatchers.Main) {
        Toast.makeText(LogCacheManager.context, string, Toast.LENGTH_SHORT).show()
    }
}


/**
 * 开启软键盘
 */
fun EditText.openKeyBord() {
    this.isFocusable = true;
    this.setFocusableInTouchMode(true);
    //请求获得焦点
    this.requestFocus();
    val imm: InputMethodManager = this.context
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.RESULT_SHOWN)
    imm.toggleSoftInput(
        InputMethodManager.SHOW_FORCED,
        InputMethodManager.HIDE_IMPLICIT_ONLY
    )
}

/**
 * 关闭软键盘
 */
fun EditText.closeKeyBord() {
    val imm: InputMethodManager = this.context
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0);
}

/**
 * 关闭软键盘
 */
fun Activity.closeKeyBord() {
    val imm: InputMethodManager = this
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.window.decorView.windowToken, 0);
}

/**
 * 软键盘是否弹出
 */
fun Activity.isSoftInputShow(): Boolean {

    // 虚拟键盘隐藏 判断view是否为空
    val view: View? = window.peekDecorView()
    view?.let {
        // 隐藏虚拟键盘
        val inputmanger = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //       inputmanger.hideSoftInputFromWindow(view.getWindowToken(),0);
        return inputmanger.isActive && this.window.currentFocus != null
    }
    return false
}

/**
 * px 转换 dp
 */
fun Float.dp(): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        LogCacheManager.context?.resources?.displayMetrics
    )
}