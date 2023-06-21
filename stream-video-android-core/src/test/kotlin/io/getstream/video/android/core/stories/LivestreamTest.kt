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

package io.getstream.video.android.core.stories

import com.google.common.truth.Truth.assertThat
import io.getstream.video.android.core.base.IntegrationTestBase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.temporal.ChronoUnit

@RunWith(RobolectricTestRunner::class)
class LivestreamTest : IntegrationTestBase() {
    /**
     * This test covers the most commonly used endpoints for a livestream
     * Some of these are already covered by the audio room test
     *
     * Create the client without providing the user
     *
     * Go live on a call that's in backstage mode
     *
     * Join a call with token based permissions (either publish or viewer mode)
     *
     * Join a call in viewing only mode
     *
     * Start and stop recording
     *
     * Verify that the following stats are available. (participant count, time running)
     *
     * Check that you can manually change the quality of the call you are receiving
     *
     * Mute the audio of the livestream you are viewing
     *
     * Also see: https://www.notion.so/stream-wiki/Livestream-Tutorial-Android-Brainstorm-1568ee5cb23b4d23b5de69defaa1cc76
     */

    @Test
    fun `list recorded calls`() = runTest {
        val result = call.listRecordings()
        assertSuccess(result)
    }

    @Test
    @Ignore
    fun `start and stop broadcasting to HLS`() = runTest {
        val call = client.call("livestream", randomUUID())
        val createResult = call.create()
        assertSuccess(createResult)

        val result = call.startBroadcasting()
        assertSuccess(result)

        assertThat(call.state.egress.value?.hls).isNotNull()

        val result2 = call.stopBroadcasting()
        assertSuccess(result2)
    }

    @Test
    fun `calls should support RTMP in`() = runTest {
        val call = client.call("default", "NnXAIvBKE4Hy")
        val response = call.create()
        assertSuccess(response)

        val url = call.state.ingress.value?.rtmp?.address
        val token = clientImpl.dataStore.userToken.value
        val apiKey = clientImpl.dataStore.apiKey.value
        val streamKey = "$apiKey/$token"
        // TODO: not implemented on the server
        // Create a publishing token
        // TODO: Wrap the CallIngressResponse? to expose the streamKey?
        // TODO: alternatively call.state.ingress could be a mapped state
    }

    @Test
    @Ignore // backend support isn't ready, call.create doesn't return counts
    fun `call should expose participant count`() = runTest {
        val call = client.call("livestream", randomUUID())
        val result = call.create()
        assertSuccess(result)
        // counts
        val count = call.state.participantCounts.value
        assertThat(count?.anonymous).isEqualTo(0)
        assertThat(count?.total).isEqualTo(0)

        call.join()

        val newCount = call.state.participantCounts.value
        assertThat(newCount?.anonymous).isEqualTo(1)
        assertThat(newCount?.total).isEqualTo(1)
    }

    @Test
    @Ignore
    fun `call should return time running`() = runTest {
        val call = client.call("livestream", randomUUID())
        assertSuccess(call.create())
        val goLiveResponse = call.goLive()
        assertSuccess(goLiveResponse)

        // call running time
//        goLiveResponse.onSuccess {
//            assertThat(it.call.session).isNotNull()
//        }

        val start = call.state.session.value?.startedAt ?: OffsetDateTime.now()
        assertThat(start).isNotNull()

        val test = flow {
            emit(1)
            emit(2)
        }

        val timeSince = flow<Long?> {
            while (true) {
                delay(1000)
                val now = OffsetDateTime.now()
                if (start == null) {
                    emit(null)
                } else {
                    val difference = ChronoUnit.SECONDS.between(start?.toInstant(), now.toInstant())
                    emit(difference)
                }
                if (call.state.session.value?.endedAt != null) {
                    break
                }
            }
        }

        println("a")
        timeSince.collect {
            println("its $it")
        }

        println("b")

        Thread.sleep(20000)
    }
}
