/*
 * Copyright (c) 2014-2023 Stream.io Inc. All rights reserved.
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

package io.getstream.video.android.xml.widget.participant

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.Px
import io.getstream.video.android.xml.R
import io.getstream.video.android.xml.font.TextStyle
import io.getstream.video.android.xml.utils.extensions.getColorCompat
import io.getstream.video.android.xml.utils.extensions.getDimension
import io.getstream.video.android.xml.utils.extensions.getDrawableCompat
import io.getstream.video.android.xml.utils.extensions.getResourceId
import io.getstream.video.android.xml.utils.extensions.use
import io.getstream.video.android.xml.widget.participant.internal.CallParticipantsGridView
import io.getstream.video.android.xml.widget.participant.internal.CallParticipantsListView
import io.getstream.video.android.xml.widget.screenshare.ScreenShareView
import io.getstream.video.android.xml.widget.transformer.TransformStyle
import io.getstream.video.android.ui.common.R as RCommon

/**
 * Style for [CallParticipantsView].
 * Use this class together with [TransformStyle.callParticipantsStyleTransformer] to change [CallParticipantsView]
 * styles programmatically.
 *
 * @param gridCallParticipantStyle The id of the custom style for [CallParticipantView] to be applied for each call
 * participant in the [CallParticipantsGridView].
 * @param listCallParticipantStyle The id of the custom style for [CallParticipantView] to be applied for each call
 * participant in the [CallParticipantsListView].
 * @param localParticipantHeight The height of the [FloatingParticipantView] used fot the local user.
 * @param localParticipantWidth The width of the [FloatingParticipantView] used fot the local user.
 * @param localParticipantPadding The padding between the [FloatingParticipantView] used fot the local user and the
 * borders of [CallParticipantsView].
 * @param localParticipantRadius The corner radius of the [FloatingParticipantView] used fot the local user.
 * @param participantListHeight The height of the participants list when there is a screen share session active.
 * @param participantListPadding The padding applied to the participants list.
 * @param participantListItemMargin The margin between two adjacent [CallParticipantView]s inside
 * [CallParticipantsListView].
 * @param participantListItemWidth The width of a [CallParticipantView] inside [CallParticipantsListView].
 * @param screenShareMargin Size of the margin between [ScreenShareView] and [CallParticipantsListView].
 * @param presenterTextStyle Active screen share presenter text style.
 * @param presenterTextPadding Padding around the presenter text.
 * @param presenterTextMargin Margin between presenter text and screen share content.
 */
public data class CallParticipantsStyle(
    public val gridCallParticipantStyle: Int,
    public val listCallParticipantStyle: Int,
    @Px public val localParticipantHeight: Float,
    @Px public val localParticipantWidth: Float,
    @Px public val localParticipantPadding: Float,
    @Px public val localParticipantRadius: Float,
    @Px public val participantListHeight: Int,
    @Px public val participantListPadding: Int,
    @Px public val participantListItemMargin: Int,
    @Px public val participantListItemWidth: Int,
    @Px public val screenShareMargin: Int,
    public val presenterTextStyle: TextStyle,
    @Px public val presenterTextPadding: Int,
    @Px public val presenterTextMargin: Int,
    public val preConnectionImage: Drawable
) {

    internal companion object {
        operator fun invoke(context: Context, attrs: AttributeSet?): CallParticipantsStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.CallParticipantsView,
                R.attr.streamCallParticipantsViewStyle,
                R.style.Stream_CallParticipants
            ).use {

                val gridCallParticipantStyle = it.getResourceId(
                    R.styleable.CallParticipantsView_streamCallParticipantsGridParticipantStyle,
                    context.getResourceId(R.style.StreamVideoTheme, R.attr.streamCallParticipantViewStyle)
                )

                val listCallParticipantStyle = it.getResourceId(
                    R.styleable.CallParticipantsView_streamCallParticipantsListParticipantStyle,
                    context.getResourceId(R.style.StreamVideoTheme, R.attr.streamCallParticipantViewStyle)
                )

                val localParticipantHeight = it.getDimension(
                    R.styleable.CallParticipantsView_streamCallParticipantsLocalParticipantHeight,
                    context.getDimension(RCommon.dimen.floatingVideoHeight).toFloat()
                )

                val localParticipantWidth = it.getDimension(
                    R.styleable.CallParticipantsView_streamCallParticipantsLocalParticipantWidth,
                    context.getDimension(RCommon.dimen.floatingVideoWidth).toFloat()
                )

                val localParticipantPadding = it.getDimension(
                    R.styleable.CallParticipantsView_streamCallParticipantsLocalParticipantPadding,
                    context.getDimension(RCommon.dimen.floatingVideoPadding).toFloat()
                )

                val localParticipantRadius = it.getDimension(
                    R.styleable.CallParticipantsView_streamCallParticipantsLocalParticipantRadius,
                    context.getDimension(RCommon.dimen.floatingVideoRadius).toFloat()
                )

                val participantListHeight = it.getDimension(
                    R.styleable.CallParticipantsView_streamCallParticipantsListHeight,
                    context.getDimension(RCommon.dimen.screenShareParticipantsListHeight).toFloat()
                ).toInt()

                val participantListPadding = it.getDimension(
                    R.styleable.CallParticipantsView_streamCallParticipantsListPadding,
                    context.getDimension(RCommon.dimen.screenShareParticipantsListPadding).toFloat()
                ).toInt()

                val participantListItemMargin = it.getDimension(
                    R.styleable.CallParticipantsView_streamCallParticipantsListItemMargin,
                    context.getDimension(RCommon.dimen.screenShareParticipantsListItemMargin).toFloat()
                ).toInt()

                val participantListItemWidth = it.getDimension(
                    R.styleable.CallParticipantsView_streamCallParticipantsListItemWidth,
                    context.getDimension(RCommon.dimen.screenShareParticipantItemSize).toFloat()
                ).toInt()

                val screenShareMargin = it.getDimension(
                    R.styleable.CallParticipantsView_streamCallParticipantsScreenShareListMargin,
                    context.getDimension(RCommon.dimen.screenShareParticipantsRadius).toFloat()
                ).toInt()

                val presenterTextStyle = TextStyle.Builder(it)
                    .size(
                        R.styleable.CallParticipantsView_streamCallParticipantsPresenterInfoTextSize,
                        context.getDimension(RCommon.dimen.title3TextSize)
                    )
                    .color(
                        R.styleable.CallParticipantsView_streamCallParticipantsPresenterInfoTextColor,
                        context.getColorCompat(RCommon.color.stream_text_high_emphasis)
                    )
                    .font(
                        R.styleable.CallParticipantsView_streamCallParticipantsPresenterInfoFontAsset,
                        R.styleable.CallParticipantsView_streamCallParticipantsPresenterInfoFont,
                    )
                    .style(
                        R.styleable.CallParticipantsView_streamCallParticipantsPresenterInfoTextStyle,
                        Typeface.BOLD
                    )
                    .build()

                val presenterTextMargin = it.getDimension(
                    R.styleable.CallParticipantsView_streamCallParticipantsPresenterTextMargin,
                    context.getDimension(RCommon.dimen.screenSharePresenterTitleMargin).toFloat()
                ).toInt()

                val presenterTextPadding = it.getDimension(
                    R.styleable.CallParticipantsView_streamCallParticipantsPresenterTextPadding,
                    context.getDimension(RCommon.dimen.screenSharePresenterPadding).toFloat()
                ).toInt()

                val preConnectionImage = it.getDrawable(
                    R.styleable.CallParticipantsView_streamCallParticipantsPreConnectionImage
                ) ?: context.getDrawableCompat(RCommon.drawable.ic_call)!!

                return CallParticipantsStyle(
                    gridCallParticipantStyle = gridCallParticipantStyle,
                    listCallParticipantStyle = listCallParticipantStyle,
                    localParticipantHeight = localParticipantHeight,
                    localParticipantWidth = localParticipantWidth,
                    localParticipantPadding = localParticipantPadding,
                    localParticipantRadius = localParticipantRadius,
                    participantListHeight = participantListHeight,
                    participantListPadding = participantListPadding,
                    participantListItemMargin = participantListItemMargin,
                    participantListItemWidth = participantListItemWidth,
                    screenShareMargin = screenShareMargin,
                    presenterTextStyle = presenterTextStyle,
                    presenterTextMargin = presenterTextMargin,
                    presenterTextPadding = presenterTextPadding,
                    preConnectionImage = preConnectionImage
                ).let(TransformStyle.callParticipantsStyleTransformer::transform)
            }
        }
    }
}
