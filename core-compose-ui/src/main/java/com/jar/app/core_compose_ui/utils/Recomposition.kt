import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import com.jar.app.core_ui.BuildConfig

class Ref(var value: Int)

@Composable
inline fun LogCompositions(tag: String, msg: String) {
    if (BuildConfig.DEBUG) {
        val ref = remember { Ref(0) }
        SideEffect { ref.value++}
    }
}