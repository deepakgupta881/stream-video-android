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

package io.getstream.video.android.compose.ui.components.avatar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.crossfade.CrossfadePlugin
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.ui.common.R

/**
 * An avatar that renders an image from the provided image URL. In case the image URL
 * was empty or there was an error loading the image, it falls back to the initials avatar.
 *
 * @param modifier Modifier for styling.
 * @param imageUrl The URL of the image to load.
 * @param initials The fallback text.
 * @param shape The shape of the avatar.
 * @param textStyle The text style of the [initials] text.
 * @param contentScale The scale option used for the content.
 * @param contentDescription Description of the image.
 * @param requestSize The actual request size.
 * @param previewPlaceholder A placeholder that will be displayed on the Compose preview (IDE).
 * @param loadingPlaceholder A placeholder that will be displayed while loading an image.
 * @param initialsAvatarOffset The initials offset to apply to the avatar.
 * @param onClick OnClick action, that can be nullable.
 */
@Composable
public fun Avatar(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    initials: String? = null,
    shape: Shape = VideoTheme.shapes.avatar,
    textStyle: TextStyle = VideoTheme.typography.title3Bold,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
    requestSize: IntSize = IntSize(DEFAULT_IMAGE_SIZE, DEFAULT_IMAGE_SIZE),
    @DrawableRes previewPlaceholder: Int =
        LocalAvatarPreviewProvider.getLocalAvatarPreviewPlaceholder(),
    @DrawableRes loadingPlaceholder: Int? =
        LocalAvatarPreviewProvider.getLocalAvatarLoadingPlaceholder(),
    initialsAvatarOffset: DpOffset = DpOffset(0.dp, 0.dp),
    onClick: (() -> Unit)? = null,
) {
    if (LocalInspectionMode.current && !imageUrl.isNullOrEmpty()) {
        Image(
            modifier = modifier
                .fillMaxSize()
                .clip(CircleShape)
                .testTag("avatar"),
            painter = painterResource(id = previewPlaceholder),
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
        return
    }

    if (imageUrl.isNullOrEmpty() && !initials.isNullOrBlank()) {
        InitialsAvatar(
            modifier = modifier,
            initials = initials,
            shape = shape,
            textStyle = textStyle,
            avatarOffset = initialsAvatarOffset,
        )
        return
    }

    val clickableModifier: Modifier = if (onClick != null) {
        modifier.clickable(
            onClick = onClick,
            indication = rememberRipple(bounded = false),
            interactionSource = remember { MutableInteractionSource() },
        )
    } else {
        modifier
    }

    CoilImage(
        modifier = clickableModifier.clip(shape),
        imageModel = { imageUrl },
        imageOptions = ImageOptions(
            contentDescription = contentDescription,
            contentScale = contentScale,
            requestSize = requestSize,
        ),
        previewPlaceholder = previewPlaceholder,
        component = rememberImageComponent {
            +CrossfadePlugin()
            loadingPlaceholder?.let {
                +PlaceholderPlugin.Loading(painterResource(id = it))
            }
        },
        failure = {
            InitialsAvatar(
                modifier = modifier,
                initials = initials.orEmpty(),
                shape = shape,
                textStyle = textStyle,
                avatarOffset = initialsAvatarOffset,
            )
        },
    )
}

@Preview
@Composable
private fun AvatarInitialPreview() {
    VideoTheme {
        Avatar(
            modifier = Modifier.size(72.dp),
            initials = "Thierry",
        )
    }
}

@Preview
@Composable
internal fun AvatarImagePreview() {
    VideoTheme {
        Avatar(
            modifier = Modifier.size(72.dp),
            initials = null,
            previewPlaceholder = R.drawable.stream_video_call_sample,
        )
    }
}

internal const val DEFAULT_IMAGE_SIZE = -1
