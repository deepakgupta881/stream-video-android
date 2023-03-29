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

package io.getstream.video.android.core

import io.getstream.video.android.core.events.ConnectedEvent
import io.getstream.video.android.core.events.VideoEvent
import io.getstream.video.android.core.model.QueryCallsData
import io.getstream.video.android.core.model.User
import io.getstream.video.android.core.model.UserType
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ClientAndAuthTest : IntegrationTestBase() {
    /**
     * Test coverage for Client and Authentication
     *
     * TODO
     * - Test logging
     * - Build vars (and document the system)
     * - Truth & Mockk
     * - Consider DataStore over SharedPreferences
     *
     * - Ensure API calls run on DispatcherProvider.IO
     * - Connection id / connect setup when should it run
     * - Guest user creation, and anon user id setup
     *
     */
    @Test
    fun regularUser() = runTest {
        StreamVideoBuilder(
            context = context,
            apiKey = apiKey,
            geo = GEO.GlobalEdgeNetwork,
            testData.users["thierry"]!!,
            testData.tokens["thierry"]!!,
        ).build()
    }

    @Test
    fun anonymousUser() = runTest {
        StreamVideoBuilder(
            context = context,
            apiKey = apiKey,
            geo = GEO.GlobalEdgeNetwork,
            user = User(id="anon", type=UserType.Anonymous)
        ).build()
    }

    @Test
    fun guestUser() = runTest {
        // we get the token from Stream's server in this case
        // the ID is generated, client side...
        // verify that we get the token
        // API call is getGuestUser or something like that
        // TODO: Implement
        StreamVideoBuilder(
            context = context,
            apiKey = apiKey,
            geo = GEO.GlobalEdgeNetwork,
            user = User(id="guest", type=UserType.Guest)
        ).build()
    }

    @Test
    fun subscribeToAllEvents() = runTest {

        val client = StreamVideoBuilder(
            context = context,
            apiKey = apiKey,
            geo = GEO.GlobalEdgeNetwork,
            user = User(id="guest", type=UserType.Guest)
        ).build()
        val sub = client.subscribe { event: VideoEvent ->
            System.out.println(event)
        }
        sub.dispose()
        // TODO: subscribe to events
    }

    @Test
    fun subscribeToSpecificEvents() = runTest {

        val client = StreamVideoBuilder(
            context = context,
            apiKey = apiKey,
            geo = GEO.GlobalEdgeNetwork,
            user = User(id="guest", type=UserType.Guest)
        ).build()
        // Subscribe for new message events
        val sub = client.subscribeFor<ConnectedEvent> { newMessageEvent ->
            System.out.println(newMessageEvent)
        }
        sub.dispose()
    }

    @Test
    fun testInvalidAPIKey() = runTest {
        StreamVideoBuilder(
            context = context,
            apiKey = "notvalid",
            geo = GEO.GlobalEdgeNetwork,
            testData.users["thierry"]!!,
            testData.tokens["thierry"]!!,
        ).build()
    }

    @Test
    fun testEmptyAPIKey() = runTest {
        StreamVideoBuilder(
            context = context,
            apiKey = "",
            geo = GEO.GlobalEdgeNetwork,
            testData.users["thierry"]!!,
            testData.tokens["thierry"]!!,
        ).build()
    }

    @Test
    fun testInvalidToken() = runTest {
        StreamVideoBuilder(
            context = context,
            apiKey = apiKey,
            geo = GEO.GlobalEdgeNetwork,
            testData.users["thierry"]!!,
            "invalidtoken",
        ).build()
    }

    @Test
    fun testConnectionId() = runTest {
        // all requests should have a connection id
        // the connection id comes from the websocket
        // TODO:
        // - you shouldn't immediately connect to the WS
        // - maybe a manual connect step is best
        // - maybe it doesn't wait for WS
        // - there is no .connect on android, when should it connect?

        val client = StreamVideoBuilder(
            context = context,
            apiKey = apiKey,
            geo = GEO.GlobalEdgeNetwork,
            testData.users["thierry"]!!,
            testData.tokens["thierry"]!!,
        ).build()
        // client.connect()
        val filters = mutableMapOf("active" to true)
        client.joinCall("default", "123")

        val result = client.queryCalls(QueryCallsData(filters))
        assert(result.isSuccess)
    }
}
