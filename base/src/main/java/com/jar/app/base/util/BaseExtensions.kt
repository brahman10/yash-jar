package com.jar.app.base.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.LauncherApps
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.StyleSpan
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.text.HtmlCompat
import androidx.core.text.isDigitsOnly
import androidx.core.view.forEach
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.R
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.base.ui.activity.BaseActivity
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import java.io.File
import java.math.RoundingMode
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*
import com.jar.app.base.util.Color as CustomColor


fun getSecondAndMillisecondFormat(endTimeTime: Long, startTime: Long): Float {
    return SimpleDateFormat("ss.SSS", Locale.getDefault()).format(Date(endTimeTime - startTime)).toFloat()
}

fun ViewGroup.isFormValid(vararg excludedIds: Int): Boolean {
    var isFormValid = true
    forEach {
        if (it is EditText) {
            if (it.text.isNullOrBlank() && !excludedIds.contains(it.id))
                isFormValid = false
        }
    }
    return isFormValid
}

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}

fun Boolean?.orTrue(): Boolean {
    return this ?: true
}

fun Boolean?.toYesOrNo(): String {
    return if (this.orFalse()) "Yes" else "No"
}

fun Long.epochToDate(): Date {
    return Date(this)
}

fun Date.getShortMonth(): String {
    val calender = Calendar.getInstance()
    calender.time = this
    return calender.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())!!
}

fun View.setPlotlineViewTag(tag: String?) {
    tag?.let {
        this.tag = BaseConstants.PLOTLINE_CONSTANT + it
    }
}

fun Date.getFormattedDate(format: String = "dd/MM/yyyy"): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.format(this)
}

fun Long.getDateMonthNameAndYear(format: String = "dd MMMM yyyy"): String {
    val formatter = DateTimeFormatter.ofPattern(format)
    val firstDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault())
    return firstDate.format(formatter)
}

fun TextView.setHtmlText(text: String) {
    this.text = HtmlCompat.fromHtml(
        text.replace("\\n", "<br/>"),
        HtmlCompat.FROM_HTML_MODE_LEGACY
    )
}

fun Long.getDateShortMonthNameAndYear(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val firstDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault())
    return firstDate.format(formatter)
}

fun String.getMonthNameDateFromDDMMYYYY(): String {
    return try {
        val date =
            LocalDate.parse(this, DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()))
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        formatter.format(date)
    } catch (exception: Exception) {
        this
    }
}

fun Long.getElapsedTimeInMonths(): Long {
    return ChronoUnit.MONTHS.between(
        Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()),
        Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault())
    )
}

fun Long.convertEpochTime(): Pair<Long, Long> {
    val diffInDays = ChronoUnit.DAYS.between(
        Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()),
        Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault())
    )
    val diffInMonths = ChronoUnit.MONTHS.between(
        Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()),
        Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault())
    )
    return Pair(diffInDays, diffInMonths)//, diffInYears)
}

fun Long.getElapsedTimeInDays(): Long {
    return ChronoUnit.DAYS.between(
        Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()),
        Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault())
    )
}


fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun EditText.setOnImeActionDoneListener(onDone: () -> Unit) {
    setOnEditorActionListener { _, i, _ ->
        if (i == EditorInfo.IME_ACTION_DONE) {
            onDone.invoke()
            return@setOnEditorActionListener true
        }
        return@setOnEditorActionListener false
    }
}

fun String.getOtp(length: Int?): String? {
    val regex = "\\b\\d{${length}}\\b".toRegex()
    return regex.find(this)?.value
}

/**
 * Also add the @param packageName to <queries></queries> in AndroidManifest.xml
 * to handle Android 11 package visibility
 */
fun Context.isPackageInstalled(packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun CoroutineScope.doRepeatingTask(
    repeatInterval: Long = 2000,
    action: () -> Unit = {},
) = this.launch {
    while (isActive) {
        delay(repeatInterval)
        withContext(Dispatchers.Main) {
            action()
        }
    }
}

/**
 * Computes the next time interval for an exponential timer increase.
 *
 * @param baseTime The base time in milliseconds, which acts as the initial delay and multiplier.
 * @param attempt The current attempt number (0 for the first attempt, 1 for the second, etc.).
 * @return The computed time interval in milliseconds for the given attempt.
 */
fun Long.exponentialTimer(baseTime: Long, attempt: Int) =
    baseTime * 2.0.pow(attempt.toDouble()).toLong()


//fun CharSequence?.isValidPhoneNumber() =
//    !this.isNullOrBlank() && this.length == 10 && this.isDigitsOnly() && this[0].digitToInt() > 5 && !this.areAllCharsSame()

fun CharSequence.areAllCharsSame() = this.matches("^(.)\\1*$".toRegex())

fun String.formatPhoneNumber(): String {
    if (this.isEmpty()) return this
    val phone = this.replace(Regex("[-_,.() ]"), "")
    if (!phone.startsWith("+") && phone.length == 10)
        return "+91$phone"
    return phone
}


fun Context.readTextFromAsset(fileName: String): String {
    return assets.open(fileName).bufferedReader().use {
        it.readText()
    }
}

suspend fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    this.let { file: File ->
        return withContext(Dispatchers.IO) {
            if (file.exists().not())
                file.createNewFile()
            outputStream().use { out ->
                bitmap.compress(format, quality, out)
                out.flush()
            }
        }
    }
}

fun Float.addPercentage(percentage: Float): Float {
    return (this * (1 + (percentage / 100)))
}

fun Float.reducePercentage(percentage: Float): Float {
    return (this * (1 - (percentage / 100)))
}

fun Float.calculatePercentage(percentage: Float): Float {
    return this * (percentage / 100)
}

fun String?.toFloatOrZero(): Float {
    return try {
        this?.let { java.lang.Float.parseFloat(it) } ?: run { 0f }
    } catch (ignore: NumberFormatException) {
        0f
    }
}

fun String?.toLongOrZero(): Long {
    return try {
        this?.let { java.lang.Long.parseLong(it) } ?: run { 0L }
    } catch (ignore: NumberFormatException) {
        0L
    }
}

fun String?.toIntOrZero(): Int {
    return try {
        this?.let { Integer.parseInt(it) } ?: run { 0 }
    } catch (ignore: NumberFormatException) {
        0
    }
}

fun JSONObject.toMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    val keys = this.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        val value = this.get(key)
        if (value is JSONObject) {
            map[key] = value.toMap()
        } else {
            map[key] = value
        }
    }
    return map
}

inline fun <reified T : RecyclerView.ViewHolder> RecyclerView.forEachVisibleHolder(
    action: (T) -> Unit
) {
    for (i in 0 until childCount) {

        action(getChildViewHolder(getChildAt(i)) as T)
    }
}

inline fun <reified T : RecyclerView.ViewHolder> RecyclerView.forEachVisibleHolderUntil(
    from: Int,
    until: Int,
    action: (T) -> Unit
) {
    val till = if (until <= childCount) until else childCount
    for (i in from until till) {
        action(getChildViewHolder(getChildAt(i)) as T)
    }
}

inline fun <reified T : RecyclerView.ViewHolder> RecyclerView.forEachIndexedVisibleHolder(
    action: (T, pos: Int) -> Unit
) {
    for (i in 0 until childCount) {
        action(getChildViewHolder(getChildAt(i)) as T, i)
    }
}

fun RecyclerView.LayoutManager.findViewWithTag(tag: String): View? {
    for (i in 0 until childCount) {
        val childView = getChildAt(i)
        if (childView?.tag == tag) {
            return childView
        }
    }
    return null
}

fun View.getParentCoordinates(): Pair<Int, Int>? {
    val parentView = parent as? ViewGroup
    return if (parentView != null) {
        parentView.indexOfChild(this)
        val parentX = parentView.left
        val parentY = parentView.top
        val childX = left
        val childY = top
        Pair(parentX + childX, parentY + childY)
    } else {
        null
    }
}

fun Context.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else {
        0
    }
}

val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.dpToPx() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.pxToDp() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Bundle.putValue(key: String, value: Any) {
    when (value) {
        is Int -> this.putInt(key, value)
        is Boolean -> this.putBoolean(key, value)
        is Double -> this.putDouble(key, value)
        is Float -> this.putFloat(key, value)
        is Parcelable -> this.putParcelable(key, value)
        else -> this.putString(key, value.toString())
    }
}

fun Context.hasLocationPermission(): Boolean {
    val requiredPermissionFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
    val requiredPermissionCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION
    val checkFineLocation = checkCallingOrSelfPermission(requiredPermissionFineLocation)
    val checkCoarseLocation = checkCallingOrSelfPermission(requiredPermissionCoarseLocation)
    return checkFineLocation == PackageManager.PERMISSION_GRANTED && checkCoarseLocation == PackageManager.PERMISSION_GRANTED
}

fun RecyclerView.addItemDecorationIfNoneAdded(vararg itemDecoration: RecyclerView.ItemDecoration) {
    if (this.itemDecorationCount == 0) {
        itemDecoration.forEach {
            this.addItemDecoration(it)
        }
    }
}

fun View.shakeAnimation() {
    val shakeAnimation = AnimationUtils.loadAnimation(context, R.anim.shake)
    this.startAnimation(shakeAnimation)
}

fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow {
        val listener = doOnTextChanged { text, _, _, _ -> trySend(text) }
        awaitClose { removeTextChangedListener(listener) }
    }
}

fun String.isValidEmail(): Boolean {
    return !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun CharSequence?.isValidUpiAddress(): Boolean =
    if (this.isNullOrBlank()) false else this.contains("[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}".toRegex())

fun CoroutineScope.countDownTimer(
    totalMillis: Long,
    intervalInMillis: Long = 1000,
    onInterval: (millisLeft: Long) -> Unit = {},
    onFinished: () -> Unit = {},
    onPaused: () -> Boolean = { false },
) = this.launch(Dispatchers.IO) {
    var total = totalMillis
    while (isActive) {
        if (!onPaused()) {
            if (total > 0) {
                withContext(Dispatchers.Main) {
                    onInterval(total)
                }
                delay(intervalInMillis)
                total -= intervalInMillis
            } else {
                withContext(Dispatchers.Main) {
                    onFinished()
                    cancel("Task Completed")
                }
            }
        }
    }
}

fun Long.milliSecondsToCountDown(showZero: Boolean = false): String {
    val seconds = this / 1000
    val hour = seconds / 3600
    val min = (seconds / 60) % 60
    val sec = seconds % 60
    val min0 = if (min < 10) "0" else ""
    val sec0 = if (sec < 10) "0" else ""
    val hourStr = when (hour) {
        0L -> {
            if (showZero) "00:" else ""
        }

        in 1..9 -> "0$hour:"
        else -> "$hour:"
    }
    return "$hourStr$min0$min:$sec0$sec"
}

fun Long.secondsToCountDown(): Triple<Long, Long, Long> {
    val seconds = this / 1000
    val hour = seconds / 3600
    val min = (seconds / 60) % 60
    val sec = seconds % 60
    return Triple(hour, min, sec)
}

fun Long.secondsToMillis() = this * 1000

fun Context.getPhonePeVersionCode(phonePePackageName: String): Int? {
    val packageInfo: PackageInfo?
    val phonePeVersionCode: Long
    try {
        packageInfo = this.applicationContext.packageManager.getPackageInfo(
            phonePePackageName,
            PackageManager.GET_ACTIVITIES
        )
        phonePeVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else
            packageInfo.versionCode.toLong()
    } catch (e: PackageManager.NameNotFoundException) {
        return null
    } catch (e: Exception) {
        return null
    }

    if (packageInfo == null) {
        return null
    }

    if (phonePeVersionCode > 94033) {
        return phonePeVersionCode.toInt()
    }
    return null
}

fun Context.openWhatsapp(number: String, message: String? = "") {
    try {
        this.startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                "http://api.whatsapp.com/send?phone=$number &text=" + encodeUrl(message.orEmpty())
            )
        })
    } catch (e: Exception) {
        e.printStackTrace()
        showToast(getString(R.string.core_ui_whatsapp_not_installed), Toast.LENGTH_LONG)
    }
}

fun Context.shareOnWhatsapp(packageName: String?, message: String, image: File? = null) {
    //If image file not null send image WA message with image file
    image?.let {
        try {
            val authority =
                "${this.applicationContext.packageName}${BaseConstants.FILE_PROVIDER_AUTHORITY}"
            FileProvider.getUriForFile(this, authority, it)?.let { uri ->
                val sendIntent = Intent()
                    .setAction(Intent.ACTION_SEND)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setDataAndType(uri, this.contentResolver.getType(uri))
                    .putExtra(Intent.EXTRA_STREAM, uri)
                    .putExtra(Intent.EXTRA_TEXT, message)

                if (!packageName.isNullOrEmpty()) sendIntent.`package` = packageName
                val shareIntent = Intent.createChooser(sendIntent, getString(R.string.app_name))

                startActivity(shareIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this.applicationContext, "Some error occurred", Toast.LENGTH_LONG)
                .show()
        }
    } ?: kotlin.run {
        //If image file is null send normal WA message
        try {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, message)
            sendIntent.type = "text/plain"
            if (!packageName.isNullOrEmpty()) sendIntent.`package` = packageName
            val shareIntent = Intent.createChooser(sendIntent, getString(R.string.app_name))
            startActivity(shareIntent)
        } catch (e: Exception) {
            showToast(getString(R.string.core_ui_whatsapp_not_installed))
        }
    }
}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.applicationContext, message, duration).show()
}

fun isChromeInstalled(context: Context): Boolean {
    return context.isPackageInstalled("com.android.chrome")
}

fun BaseFragment<*>.openUrlInChromeTab(url: String, title: String, showToolbar: Boolean) {
    if (isChromeInstalled(requireContext())) {
        try {
            val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(CustomColor.BG_COLOR)
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_arrow_back)?.let {
                DrawableCompat.setTint(it, Color.WHITE)
                builder.setCloseButtonIcon(it.toBitmap())
            }
            builder.setShowTitle(true)
            builder.setShareState(CustomTabsIntent.SHARE_STATE_OFF)

            val customTabsIntent: CustomTabsIntent = builder.build()
            customTabsIntent.intent.setPackage("com.android.chrome")
            customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
        } catch (e: Exception) {
            val value = false
            val encodedUrl = encodeUrl(url)
            navigateTo(
                "android-app://com.jar.app/webView/${BaseConstants.WebViewFlowType.NO_CHROME_TAB_INSTALLED}/$value/$encodedUrl/${title.ifEmpty { "Jar" }}/$showToolbar",
                true
            )
        }
    } else {
        val value = false
        val encodedUrl = encodeUrl(url)
        navigateTo(
            "android-app://com.jar.app/webView/${BaseConstants.WebViewFlowType.NO_CHROME_TAB_INSTALLED}/$value/$encodedUrl/${title.ifEmpty { "Jar" }}/$showToolbar",
            true
        )
    }
}


fun BaseDialogFragment<*>.openUrlInChromeTab(url: String, title: String, showToolbar: Boolean) {
    if (isChromeInstalled(requireContext())) {
        try {
            val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(CustomColor.BG_COLOR)
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_arrow_back)?.let {
                DrawableCompat.setTint(it, Color.WHITE)
                builder.setCloseButtonIcon(it.toBitmap())
            }
            builder.setShowTitle(true)
            builder.setShareState(CustomTabsIntent.SHARE_STATE_OFF)

            val customTabsIntent: CustomTabsIntent = builder.build()
            customTabsIntent.intent.setPackage("com.android.chrome")
            customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
        } catch (e: Exception) {
            val value = false
            val encodedUrl = encodeUrl(url)
            navigateTo(
                "android-app://com.jar.app/webView/${BaseConstants.WebViewFlowType.NO_CHROME_TAB_INSTALLED}/$value/$encodedUrl/${title.ifEmpty { "Jar" }}/$showToolbar",
                true
            )
        }
    } else {
        val value = false
        val encodedUrl = encodeUrl(url)
        navigateTo(
            "android-app://com.jar.app/webView/${BaseConstants.WebViewFlowType.NO_CHROME_TAB_INSTALLED}/$value/$encodedUrl/${title.ifEmpty { "Jar" }}/$showToolbar",
            true
        )
    }
}

fun BaseActivity<*>.openUrlInChromeTab(url: String, title: String, showToolbar: Boolean) {
    if (isChromeInstalled(this)) {
        try {
            val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(CustomColor.BG_COLOR)
            AppCompatResources.getDrawable(this, R.drawable.ic_arrow_back)?.let {
                DrawableCompat.setTint(it, Color.WHITE)
                builder.setCloseButtonIcon(it.toBitmap())
            }
            builder.setShowTitle(true)
            builder.setShareState(CustomTabsIntent.SHARE_STATE_OFF)

            val customTabsIntent: CustomTabsIntent = builder.build()
            customTabsIntent.intent.setPackage("com.android.chrome")
            customTabsIntent.launchUrl(this, Uri.parse(url))
        } catch (e: Exception) {
            val value = false
            navigateTo(
                navController,
                "android-app://com.jar.app/webView/${BaseConstants.WebViewFlowType.NO_CHROME_TAB_INSTALLED}/$value/$url/${title.ifEmpty { "Jar" }}/$showToolbar",
                true
            )
        }
    } else {
        val value = false
        navigateTo(
            navController,
            "android-app://com.jar.app/webView/${BaseConstants.WebViewFlowType.NO_CHROME_TAB_INSTALLED}/$value/$url/${title.ifEmpty { "Jar" }}/$showToolbar",
            true
        )
    }
}

fun Context.copyToClipboard(message: String, toastMessage: String?) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(getString(R.string.app_name), message)
    clipboardManager.setPrimaryClip(clipData)
    toastMessage?.takeIf { it.isNotEmpty() }?.let {
        showToast(it)
    }
}

fun Context.copyToClipboard(message: String) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(getString(R.string.app_name), message)
    clipboardManager.setPrimaryClip(clipData)
}

fun Double.volumeToString(digitsAfterDecimal: Int = 4): String =
    String.format("%.${digitsAfterDecimal}f", this)

fun Double.amountToString(digitsAfterDecimal: Int = 2): String =
    String.format("%.${digitsAfterDecimal}f", this)

fun Float.volumeToString(digitsAfterDecimal: Int = 4): String =
    String.format("%.${digitsAfterDecimal}f", this)

//Removes trailing 0's from volume. Eg if volume is 1.000 it will return 1
//And 1.5000 will be returned as 1.5
//this%1 is used to check for decimal
//using %1$.{roundUpTo}f to format float without the E notation.
fun Float.volumeToStringWithoutTrailingZeros(roundUpTo: Int = 4): String {
    val value = String.format("%1$.${roundUpTo}f", this)
    return if (this % 1 == 0.0f) this.toInt()
        .toString() else if (value.contains('.')) value.replace("0*$".toRegex(), "") else value
}

fun Float.amountToString(digitsAfterDecimal: Int = 2): String =
    String.format("%.${digitsAfterDecimal}f", this)

fun CharSequence.hasMoreSpacesThanAlphabets(): Boolean {
    val spaces = this.count { it == ' ' }
    return spaces >= this.length - spaces
}

fun CharSequence.hasMoreThanXRepeatingChars(maxCount: Int = 5): Boolean {
    var lastChar = Char.MIN_VALUE
    var repeatCount = 0
    run loop@{
        this.forEach {
            if (repeatCount >= maxCount)
                return@loop
            if (it == lastChar)
                repeatCount++
            else
                repeatCount = 0
            lastChar = it
        }
    }
    return repeatCount >= maxCount
}

fun decodeUrl(encoded: String): String {
    val data = encoded.replace("%(?![0-9a-fA-F]{2})".toRegex(), "%25")
    return URLDecoder.decode(data, "utf-8")
}

fun encodeUrl(value: String): String {
    return URLEncoder.encode(value, "utf-8")
}

fun NavController.isFragmentInBackStack(destinationId: Int) =
    try {
        getBackStackEntry(destinationId)
        true
    } catch (e: Exception) {
        false
    }

fun NavController.isPresentInBackStack(destinationId: Int) =
    try {
        backQueue.find { (it.destination.id == destinationId) }?.let {
            true
        } ?: run { false }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

val Float.dp: Float
    get() {
        val displayMetrics = Resources.getSystem().displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, displayMetrics)
    }

val Float.sp: Float
    get() {
        val displayMetrics = Resources.getSystem().displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, displayMetrics)
    }

//internal val Int.dp: Float
//    get() {
//        val displayMetrics = Resources.getSystem().displayMetrics
//        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), displayMetrics)
//    }

internal val Int.sp: Float
    get() {
        val displayMetrics = Resources.getSystem().displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), displayMetrics)
    }

fun Context.getLocalString(@StringRes id: Int, locale: Locale = Locale.ENGLISH): String {
    val newConfiguration: Configuration = with(Configuration(resources.configuration)) {
        setLocale(locale)
        this
    }
    return createConfigurationContext(newConfiguration).getString(id)
}

fun Context.openAppInfo() {
    val launcher = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    val intent: Intent? = packageManager.getLaunchIntentForPackage(packageName)
    launcher.startAppDetailsActivity(
        intent?.component,
        android.os.Process.myUserHandle(),
        null,
        null
    )
}

fun String.asInitials(limit: Int = 2, skipSpecialChars: Boolean = false): String {
    val buffer = StringBuffer()
    trim().split(" ").filter {
        it.isNotEmpty()
    }.joinTo(
        buffer = buffer,
        limit = limit,
        separator = "",
        truncated = "",
    ) { s ->
        if (skipSpecialChars)
            if (s.firstOrNull()?.isLetter() == true) s.first().uppercase() else ""
        else s.first().uppercase()
    }
    return buffer.toString()
}

fun String.spaceBeforeUpperCaseChar(): String {
    if (this.isEmpty()) return this
    val result = StringBuilder()
    result.append(this[0])
    for (i in 1 until this.length) {
        if (Character.isUpperCase(this[i])) result.append(" ")
        result.append(this[i])
    }
    return result.toString()
}

fun TextView.setGradientTextColor(vararg colorRes: Int) {
    val matrix = Matrix()
    val floatArray = ArrayList<Float>(colorRes.size)
    for (i in colorRes.indices) {
        floatArray.add(i, i.toFloat() / (colorRes.size - 1))
    }
    val textShader: Shader = LinearGradient(
        0f,
        0f,
        0f,
        this.height.toFloat(),
        colorRes.map { ContextCompat.getColor(context, it) }.toIntArray(),
        floatArray.toFloatArray(),
        Shader.TileMode.CLAMP
    )
    this.paint.shader = textShader
//    val animator= ObjectAnimator.ofFloat(this, "", 0f, width.toFloat())
//    doOnPreDraw {
//        matrix.setTranslate((2 * (width / 2)).toFloat(), 0f)
//        textShader.setLocalMatrix(matrix)
//    }
}

fun String?.nullSafe() = this ?: ""

fun Context.getFormattedTextForOneIntegerValue(@StringRes id: Int, value: Int): Spanned {
    val text = getString(id).replace("%d", "$value")
    return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun String.getFormattedTextForOneIntegerValue(value: Int): Spanned {
    val text = this.replace("%d", "$value")
    return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun Context.getFormattedTextForOneFloatValueUptoOnePlace(
    @StringRes id: Int,
    value: Float
): Spanned {
    val text = getString(id).replace("%.1f", "$value")
    return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun Context.getFormattedTextForOneStringValue(@StringRes id: Int, value: String): Spanned {
    val text = getString(id).replace("%s", "$value")
    return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun String.getFormattedTextForOneStringValue(value: String): Spanned {
    val text = this.replace("%s", "$value")
    return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun Context.getFormattedTextForXStringValues(@StringRes id: Int, values: List<String>): Spanned {
    var newText = getString(id)
    values.forEach {
        newText = newText.replaceFirst("%s", "$it")
    }
    return HtmlCompat.fromHtml(newText, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun String.getFormattedTextForXStringValues(values: List<String>): Spanned {
    var newText = this
    values.forEach {
        newText = newText.replaceFirst("%s", "$it")
    }
    return HtmlCompat.fromHtml(newText, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun Context.hasSmsPermission(): Boolean {
    val requiredPermission = android.Manifest.permission.READ_SMS
    val checkVal = checkCallingOrSelfPermission(requiredPermission)
    return checkVal == PackageManager.PERMISSION_GRANTED
}

fun Context.hasContactPermission(): Boolean {
    val requiredPermission = android.Manifest.permission.READ_CONTACTS
    val checkVal = checkCallingOrSelfPermission(requiredPermission)
    return checkVal == PackageManager.PERMISSION_GRANTED
}

fun String.mask(dotsCount: Int, takeLastCharCount: Int): String {
    if (this.isEmpty()) return ""
    val masked = StringBuilder()
    masked.append(".".repeat(dotsCount))
    masked.append(this.takeLast(takeLastCharCount))
    return masked.toString()
}

fun String.convertISO8601ToStringDDMMMMYY(): String? {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy", Locale.ENGLISH)
    val date = LocalDate.parse(this, inputFormatter)
    return outputFormatter.format(date) // prints this format 10-04-2018
}

fun Int.getDayOfMonthAndItsSuffix(): String {
    return this.toString() + if (this in 1..31) {
        if (this in 11..13) {
            "th"
        } else when (this % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    } else {
        "Wrong date provided"
    }
}

fun String.capitaliseFirstChar(): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun Int.getFormattedAmount(maximumFractionDigits: Int = 2): String {
    //Returns something like 12,34,567
    val formatter = NumberFormat.getNumberInstance(Locale("en", "in"))
    formatter.maximumFractionDigits = maximumFractionDigits
    formatter.minimumFractionDigits = 0
    return formatter.format(this)
}

fun Double.roundOffDecimal(): Double {
    return try {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        df.format(this).toDouble()
    } catch (e: Exception) {
        this
    }
}

fun Float.getFormattedAmount(
    maximumFractionDigits: Int = 2,
    shouldRemoveTrailingZeros: Boolean = false
): String {
    //Returns something like 12,34,567
    val formatter = NumberFormat.getNumberInstance(Locale("en", "in"))
    formatter.maximumFractionDigits = maximumFractionDigits
    formatter.minimumFractionDigits = 0
    return if (shouldRemoveTrailingZeros) {
        val value = String.format("%.${maximumFractionDigits}f", this, Locale("en", "in"))
        //Removes trailing 0's from amount. Eg if amount is 1.000 it will return 1
        //And 1.5000 will be returned as 1.5
        //this%1 is used to check for decimal
        val valueWithoutTrailingZeros =
            if (this % 1 == 0.0f) this.toInt().toString()
            else if (value.contains('.')) value.replace("0*$".toRegex(), "")
            else value
        formatter.format(valueWithoutTrailingZeros.toFloat())
    } else {
        formatter.format(this)
    }
}

fun Double.getFormattedAmount(): String {
    //Returns something like 12,34,567
    val formatter = NumberFormat.getNumberInstance(Locale("en", "in"))
    formatter.maximumFractionDigits = 2
    formatter.minimumFractionDigits = 0
    return formatter.format(this)
}

fun String.getMaskedString(
    start: Int,
    end: Int,
    replacement: CharSequence = "*"
): String {
    return try {
        check(this.length > start && this.length > end && end > start)
        val sb = StringBuilder()
        sb.appendRange(this, 0, start)
        repeat((start..end).count()) {
            sb.append(replacement)
        }
        sb.appendRange(this, end + 1, length)
        sb.toString()
    } catch (ex: Exception) {
        ""
    }
}

//PMT formula from Excel, adopted in kotlin
fun getEmiAmount(roi: Double, tenure: Int, amount: Double): Double {
    val r: Double = roi / (12 * 100)//Annual interest to monthly interest, % value to real value
    val t: Double = (tenure.toDouble() / 12 * 12)
    return amount * r / (1 - Math.pow(1 + r, -t))
}

fun formatValue(valueInRupees: Int): String {
    return when {
        valueInRupees >= 100000 -> "${String.format("%.2f", valueInRupees.toFloat() / 100000)}L"
        valueInRupees >= 1000 -> "${String.format("%.2f", valueInRupees.toFloat() / 1000)}K"
        else -> "$valueInRupees"
    }
}

fun formatVolume(valueInGm: Float): String {
    return "${String.format("%.2f", valueInGm)} g"
}

@SuppressLint("QueryPermissionsNeeded")
fun openUrlInChromeTabOrExternalBrowser(context: Context, url: String, shouldShowTitle: Boolean = true) {
    if (url.isNotEmpty()) {
        if (isChromeInstalled(context)) {
            try {
                val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(CustomColor.BG_COLOR)
                AppCompatResources.getDrawable(context, R.drawable.ic_arrow_back)?.let {
                    DrawableCompat.setTint(it, Color.WHITE)
                    builder.setCloseButtonIcon(it.toBitmap())
                }
                builder.setShowTitle(shouldShowTitle)
                builder.setShareState(CustomTabsIntent.SHARE_STATE_OFF)

                val customTabsIntent: CustomTabsIntent = builder.build()
                customTabsIntent.intent.setPackage("com.android.chrome")
                customTabsIntent.launchUrl(context, Uri.parse(url))
            } catch (e: Exception) {
                val webpage: Uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, webpage)
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                }
            }
        } else {
            val webpage: Uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }
    }
}

fun String.bold(string: String, startIndex: Int, endIndex: Int): SpannableString {
    val spannableString = SpannableString(string)
    spannableString.setSpan(
        StyleSpan(Typeface.BOLD),
        startIndex,
        endIndex,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableString
}

fun ConnectivityManager.isWifiEnabled(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val network = this.activeNetwork
        val networkCapabilities = this.getNetworkCapabilities(network)
        networkCapabilities != null && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    } else {
        val networkInfo = this.activeNetworkInfo
        networkInfo != null && networkInfo.isConnected && networkInfo.type == ConnectivityManager.TYPE_WIFI
    }
}

fun ConnectivityManager.currentInternetSpeed(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val network = this.activeNetwork
        val networkCapabilities = this.getNetworkCapabilities(network)
        return "${networkCapabilities?.linkDownstreamBandwidthKbps} kbps"
    } else {
        "Below API level 23"
    }
}

fun Context.isGestureNavigationEnabled(): Boolean {
    val resources = this.resources
    val resourceId = resources.getIdentifier(
        "config_navBarInteractionMode", "integer", "android"
    )
    return resourceId > 0 && resources.getInteger(resourceId) == 2
}

fun Float.isNumberNegative() = this <= 0f

suspend fun View.animateViewVisibility(visible: Boolean, duration: Long = 500) {
    withContext(Dispatchers.Main) {
        if (visible) {
            this@animateViewVisibility.visibility = View.VISIBLE
        }
        this@animateViewVisibility.animate()
            .alpha(if (visible) 1.0f else 0.0f)
            .setDuration(duration)
            .withEndAction {
                this@animateViewVisibility.visibility = if (visible) View.VISIBLE else View.GONE
            }
            .start()
        delay(duration)
    }
}

fun Float.toPositive() = -1 * this

fun String.getAppNameFromPkgName(packageManager: PackageManager): String? {
    return try {
        val info =
            packageManager.getApplicationInfo(this, PackageManager.GET_META_DATA)
        packageManager.getApplicationLabel(info) as String
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        ""
    }
}

fun String.getAppIconFromPkgName(packageManager: PackageManager): Drawable? {
    return try {
        packageManager.getApplicationIcon(this)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        null
    }
}

fun Long.millisToHoursMinutesSeconds(): Triple<Int, Int, Int> {
    val seconds = (this / 1000).toInt()
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return Triple(hours, minutes, seconds)
}

fun View.shouldDisableClick(shouldDisable: Boolean) {
    this.isEnabled = shouldDisable.not()
    this.isClickable = shouldDisable.not()
}

/**
 * This Extension function will take Number(int,float,double) as an input
 * and will return a formatted String corresponding to that number.
 * Ex:- input is 1200 ---- output :- 1.2k
 **/
fun Number.formatNumber(): String {
    val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
    val numValue = this.toLong()
    val value = floor(log10(numValue.toDouble())).toInt()
    val base = value / 3
    return if (value >= 3 && base < suffix.size) {
        DecimalFormat("#0.0").format(
            numValue / 10.0.pow((base * 3).toDouble())
        ) + suffix[base]
    } else {
        DecimalFormat("#,##0").format(numValue)
    }
}

fun Context.getAppVersionCode(): Int {
    val version = PackageInfoCompat.getLongVersionCode(
        packageManager.getPackageInfo(packageName, 0)
    )
    return if (Build.VERSION.SDK_INT >= 28) (version and 0x00000000ffffffff).toInt() else version.toInt()
}

fun Context.openShareIntentChooser(message: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
    }
    val chooser = Intent.createChooser(intent, message)
    try {
        startActivity(chooser)
    } catch (e: ActivityNotFoundException) {
        showToast(getString(R.string.sharing_app_not_installed))
    }
}

fun Context.shareOnEmailApp(message: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // only email apps should handle this
        putExtra(Intent.EXTRA_TEXT, message)
    }
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        showToast(getString(R.string.sharing_app_not_installed))
    }
}

fun Context.shareOnDefaultSmsApp(message: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("smsto:")  // This ensures only SMS apps respond
        putExtra("sms_body", message)
    }
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        showToast(getString(R.string.sharing_app_not_installed))
    }
}

fun Context.openAppInPlayStore(url: String) {
    try {
        this.startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        })
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun <T> LiveData<T>.asSingleLiveEvent(): SingleLiveEvent<T> {
    val singleLiveEvent = SingleLiveEvent<T>()
    singleLiveEvent.addSource(this) {
        singleLiveEvent.value = it
    }
    return singleLiveEvent
}

fun <T, K, R> LiveData<T>.combineWith(
    liveData: LiveData<K>,
    block: (T?, K?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        result.value = block(this.value, liveData.value)
    }
    result.addSource(liveData) {
        result.value = block(this.value, liveData.value)
    }
    return result
}

fun Context.shareAsText(title: String, msg: String) {
    ShareCompat.IntentBuilder(this)
        .setText(msg)
        .setType("text/plain")
        /*
         * The title of the chooser that the system will show
         * to allow the user to select an app
         */
        .setChooserTitle(title)
        .startChooser();
}

fun Context.getReferShareMsg(
    referralLink: String,
    shareMsg: String?,
    fallbackMsg: String? = null
): String {
    shareMsg?.let {
        return if (it.contains(BaseConstants.USER_INVITE_LINK)) {
            it.replace(BaseConstants.USER_INVITE_LINK, referralLink)
        } else {
            it
        }
    } ?: return if (fallbackMsg.isNullOrBlank()) {
        getString(
            R.string.referral_share_message_v2,
            referralLink
        )
    } else {
        fallbackMsg
    }
}

fun Long.epochMillisToHHmmss(): String {

    val instant = Instant.ofEpochMilli(this)
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}

fun Int.secondsToMinsAndSecs(): String {
    val mins = this / 60
    val secs = this % 60
    return "%02d:%02d".format(mins, secs)
}

fun String.toSentenceCase(): String {
    if (this.isBlank()) {
        return this
    }
    val firstChar = this[0].uppercaseChar()
    val restOfString = this.substring(1).toLowerCase()

    return "$firstChar$restOfString"
}


inline fun <reified T : Any> Any.cast(): T {
    return this as T
}

/**
 * this function replaces ul and li tags to show more consistent output
 * since ul and li tags have some default margins
 */
fun String.replaceListTagFromHtml() = this
    .replace("<ul>", "")
    .replace("</ul>", "")
    .replace("<li>", "• ")
    .replace("</li>", "<br>")



val integerChars = '0'..'9'

fun String.isNumber(): Boolean {
    var dotOccurred = 0
    return this.all { it in integerChars || it == '.' && dotOccurred++ < 1 }
}

fun Int?.isNullOrZero(): Boolean {
    return this == null || this == 0
}

fun Float?.isNullOrZero(): Boolean {
    return this == null || this == 0.0f || this == 0f
}
