package com.jar.app.core_ui.dynamic_cards.base

import android.view.View
import androidx.annotation.LayoutRes
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.viewbinding.ViewBinding
import com.airbnb.epoxy.EpoxyModel
import com.jar.app.core_ui.R
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.concurrent.ConcurrentHashMap

/**
 * A pattern for using epoxy models with Kotlin with no annotations or code generation.
 */
abstract class ViewBindingKotlinModel<T : ViewBinding>(
    @LayoutRes private val layoutRes: Int
) : EpoxyModel<View>() {

    var translationY: SpringAnimation? = null

    var view: View? = null

    // Using reflection to get the static binding method.
    // Lazy so it's computed only once by instance, when the 1st ViewHolder is actually created.
    private val bindingMethod by lazy { getBindMethodFrom(this::class.java) }

    abstract fun T.bind()

    @Suppress("UNCHECKED_CAST")
    override fun bind(view: View) {
        var binding = view.getTag(R.id.epoxy_viewbinding_tag) as? T
        if (binding == null) {
            binding = bindingMethod.invoke(null, view) as T
            view.setTag(R.id.epoxy_viewbinding_tag, binding)
        }
        binding.bind()

        this.view = view


        /**
         * A [SpringAnimation] for this RecyclerView item. This animation is used to bring the item back
         * after the over-scroll effect.
         */
        translationY = SpringAnimation(view, SpringAnimation.TRANSLATION_Y)
            .setSpring(
                SpringForce()
                    .setFinalPosition(0f)
                    .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                    .setStiffness(SpringForce.STIFFNESS_LOW)
            )
    }

    override fun getDefaultLayout() = layoutRes
}

// Static cache of a method pointer for each type of item used.
private val sBindingMethodByClass = ConcurrentHashMap<Class<*>, Method>()

@Suppress("UNCHECKED_CAST")
@Synchronized
private fun getBindMethodFrom(javaClass: Class<*>): Method =
    sBindingMethodByClass.getOrPut(javaClass) {
        val actualTypeOfThis = getSuperclassParameterizedType(javaClass)
        val viewBindingClass = actualTypeOfThis.actualTypeArguments[0] as Class<ViewBinding>
        viewBindingClass.getDeclaredMethod("bind", View::class.java)
            ?: error("The binder class ${javaClass.canonicalName} should have a method bind(View)")
    }

private fun getSuperclassParameterizedType(klass: Class<*>): ParameterizedType {
    val genericSuperclass = klass.genericSuperclass
    return (genericSuperclass as? ParameterizedType)
        ?: getSuperclassParameterizedType(genericSuperclass as Class<*>)
}