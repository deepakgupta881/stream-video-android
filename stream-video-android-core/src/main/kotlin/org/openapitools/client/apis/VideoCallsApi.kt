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

package org.openapitools.client.apis

import org.openapitools.client.models.EndCallResponse
import org.openapitools.client.models.GetCallEdgeServerRequest
import org.openapitools.client.models.GetCallEdgeServerResponse
import org.openapitools.client.models.GetOrCreateCallRequest
import org.openapitools.client.models.GetOrCreateCallResponse
import org.openapitools.client.models.JoinCallResponse
import org.openapitools.client.models.UpdateCallRequest
import org.openapitools.client.models.UpdateCallResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

internal interface VideoCallsApi {
    /**
     * End call
     *
     * Responses:
     *  - 201: Successful response
     *  - 400: Bad request
     *  - 429: Too many requests
     *
     * @param type
     * @param id
     * @return [EndCallResponse]
     */
    @POST("call/{type}/{id}/mark_ended")
    suspend fun endCall(
        @Path("type") type: String,
        @Path("id") id: String
    ): Response<EndCallResponse>

    /**
     * Get Call Edge Server
     *
     * Responses:
     *  - 201: Successful response
     *  - 400: Bad request
     *  - 429: Too many requests
     *
     * @param type
     * @param id
     * @param getCallEdgeServerRequest
     * @return [GetCallEdgeServerResponse]
     */
    @POST("get_call_edge_server/{type}/{id}")
    suspend fun getCallEdgeServer(
        @Path("type") type: String,
        @Path("id") id: String,
        @Body getCallEdgeServerRequest: GetCallEdgeServerRequest
    ): Response<GetCallEdgeServerResponse>

    /**
     * Get or create a call
     * Gets or creates a new call
     * Responses:
     *  - 201: Successful response
     *  - 400: Bad request
     *  - 429: Too many requests
     *
     * @param type
     * @param id
     * @param getOrCreateCallRequest
     * @return [GetOrCreateCallResponse]
     */
    @POST("call/{type}/{id}")
    suspend fun getOrCreateCall(
        @Path("type") type: String,
        @Path("id") id: String,
        @Body getOrCreateCallRequest: GetOrCreateCallRequest
    ): Response<GetOrCreateCallResponse>

    /**
     * Join call
     * Request to join a call
     * Responses:
     *  - 201: Successful response
     *  - 400: Bad request
     *  - 429: Too many requests
     *
     * @param type
     * @param id
     * @param getOrCreateCallRequest
     * @return [JoinCallResponse]
     */
    @POST("join_call/{type}/{id}")
    suspend fun joinCall(
        @Path("type") type: String,
        @Path("id") id: String,
        @Body getOrCreateCallRequest: GetOrCreateCallRequest
    ): Response<JoinCallResponse>

    /**
     * Update Call
     *
     * Responses:
     *  - 200: Call
     *  - 400: Bad request
     *  - 429: Too many requests
     *
     * @param type
     * @param id
     * @param updateCallRequest
     * @return [UpdateCallResponse]
     */
    @PATCH("call/{type}/{id}")
    suspend fun updateCall(
        @Path("type") type: String,
        @Path("id") id: String,
        @Body updateCallRequest: UpdateCallRequest
    ): Response<UpdateCallResponse>
}
