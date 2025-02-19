/*
 * Copyright (c) 2014-2022 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.video.android.xml.utils.extensions

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StyleableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionOrThrow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Retrieves the Drawable for the attribute.
 *
 * Unlike [TypedArray.getDrawable], gracefully handles vector drawables with tints on API 21.
 *
 * @param context The context to inflate against.
 * @param id The index of attribute to retrieve.
 * @return An object that can be used to draw this resource.
 */
@JvmSynthetic
internal fun TypedArray.getDrawableCompat(context: Context, @StyleableRes id: Int): Drawable? {
    val resource = getResourceId(id, 0)
    if (resource != 0) {
        return AppCompatResources.getDrawable(context, resource)
    }
    return null
}

@OptIn(ExperimentalContracts::class)
@JvmSynthetic
internal inline fun TypedArray.use(block: (TypedArray) -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block(this)
    recycle()
}

@JvmSynthetic
internal inline fun <reified T : Enum<T>> TypedArray.getEnum(index: Int, default: T): T {
    return getInt(index, -1).let {
        if (it >= 0) enumValues<T>()[it] else default
    }
}

@ColorInt
@JvmSynthetic
internal fun TypedArray.getColorOrNull(@StyleableRes index: Int): Int? =
    runCatching { getColorOrThrow(index) }.getOrNull()

@Px
@JvmSynthetic
internal fun TypedArray.getDimensionOrNull(@StyleableRes index: Int): Float? =
    runCatching { getDimensionOrThrow(index) }.getOrNull()
