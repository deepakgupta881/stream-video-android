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

package io.getstream.video.android.core.mapper

/**
 * Reaction mapper that maps an emoji code (e.g., :like:) to a real emoji text (👍).
 */
public fun interface ReactionMapper {

    public fun map(emojiCode: String): String

    public companion object {

        fun defaultReactionMapper(): ReactionMapper {
            return ReactionMapper { emojiCode ->
                when (emojiCode) {
                    ":fireworks:", ":tada:" -> "\uD83C\uDF89"
                    ":hello:" -> "\uD83D\uDC4B"
                    ":raise-hand:" -> "✋"
                    ":like:" -> "\uD83D\uDC4D"
                    ":hate:" -> "\uD83D\uDC4E"
                    ":smile:" -> "\uD83D\uDE04"
                    ":heart:" -> "❤️"
                    else -> emojiCode
                }
            }
        }
    }
}
