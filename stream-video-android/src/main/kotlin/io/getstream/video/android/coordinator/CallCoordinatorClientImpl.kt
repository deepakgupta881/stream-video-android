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

package io.getstream.video.android.coordinator

import io.getstream.video.android.api.ClientRPCService
import io.getstream.video.android.errors.VideoError
import io.getstream.video.android.model.CallUser
import io.getstream.video.android.model.StreamCallCid
import io.getstream.video.android.model.User
import io.getstream.video.android.model.toCallUser
import io.getstream.video.android.utils.Failure
import io.getstream.video.android.utils.Result
import io.getstream.video.android.utils.Success
import stream.video.coordinator.call_v1.Call
import stream.video.coordinator.client_v1_rpc.CreateCallRequest
import stream.video.coordinator.client_v1_rpc.CreateCallResponse
import stream.video.coordinator.client_v1_rpc.GetCallEdgeServerRequest
import stream.video.coordinator.client_v1_rpc.GetCallEdgeServerResponse
import stream.video.coordinator.client_v1_rpc.GetOrCreateCallRequest
import stream.video.coordinator.client_v1_rpc.GetOrCreateCallResponse
import stream.video.coordinator.client_v1_rpc.JoinCallRequest
import stream.video.coordinator.client_v1_rpc.JoinCallResponse
import stream.video.coordinator.client_v1_rpc.MemberInput
import stream.video.coordinator.client_v1_rpc.QueryMembersRequest
import stream.video.coordinator.client_v1_rpc.SendCustomEventRequest
import stream.video.coordinator.client_v1_rpc.SendEventRequest
import stream.video.coordinator.client_v1_rpc.UpsertCallMembersRequest

/**
 * An accessor that allows us to communicate with the API around video calls.
 */
internal class CallCoordinatorClientImpl(
    private val callCoordinatorService: ClientRPCService,
) : CallCoordinatorClient {

    /**
     * Attempts to create a new [Call].
     *
     * @param createCallRequest The information used to create a call.
     * @return [Result] wrapper around the response from the server, or an error if something went
     * wrong.
     */
    override suspend fun createCall(createCallRequest: CreateCallRequest): Result<CreateCallResponse> =
        try {
            val response = callCoordinatorService.createCall(createCallRequest = createCallRequest)

            Success(response)
        } catch (error: Throwable) {
            Failure(VideoError(error.message, error))
        }

    override suspend fun getOrCreateCall(getOrCreateCallRequest: GetOrCreateCallRequest): Result<GetOrCreateCallResponse> =
        try {
            val response =
                callCoordinatorService.getOrCreateCall(getOrCreateCallRequest = getOrCreateCallRequest)

            Success(response)
        } catch (error: Throwable) {
            Failure(VideoError(error.message, error))
        }

    /**
     * Attempts to join a [Call]. If successful, gives us more information about the
     * user and the call itself.
     *
     * @param request The details of the call, like the ID and its type.
     * @return [Result] wrapper around the response from the server, or an error if something went
     * wrong.
     */
    override suspend fun joinCall(request: JoinCallRequest): Result<JoinCallResponse> =
        try {
            val response = callCoordinatorService.joinCall(joinCallRequest = request)

            Success(response)
        } catch (error: Throwable) {
            Failure(VideoError(error.message, error))
        }

    /**
     * Finds the correct server to connect to for given user and [request]. In case there are no
     * servers, returns an error to the user.
     *
     * @param request The data used to find the best server.
     * @return [Result] wrapper around the response from the server, or an error if something went
     * wrong.
     */
    override suspend fun selectEdgeServer(request: GetCallEdgeServerRequest): Result<GetCallEdgeServerResponse> =
        try {
            val response =
                callCoordinatorService.getCallEdgeServer(getCallEdgeServerRequest = request)

            Success(response)
        } catch (error: Throwable) {
            Failure(VideoError(error.message, error))
        }

    /**
     * Sends a user-based event to the API to notify if we've changed something in the state of the
     * call.
     *
     * @param sendEventRequest The request holding information about the event type and the call.
     * @return a [Result] wrapper if the call succeeded or not.
     */
    override suspend fun sendUserEvent(sendEventRequest: SendEventRequest): Result<Boolean> =
        try {
            callCoordinatorService.sendEvent(sendEventRequest = sendEventRequest)

            Success(true)
        } catch (error: Throwable) {
            Failure(VideoError(error.message, error))
        }

    /**
     * Sends a custom event with encoded JSON data.
     *
     * @param sendCustomEventRequest The request holding the CID and the data.
     */
    override suspend fun sendCustomEvent(sendCustomEventRequest: SendCustomEventRequest): Result<Boolean> =
        try {
            callCoordinatorService.sendCustomEvent(sendCustomEventRequest = sendCustomEventRequest)

            Success(true)
        } catch (error: Throwable) {
            Failure(VideoError(error.message, error))
        }

    /**
     * Sends invite to people for an existing call.
     *
     * @param users The users to invite.
     * @param cid The call ID.
     * @return [Result] if the operation is successful or not.
     */
    override suspend fun inviteUsers(users: List<User>, cid: StreamCallCid): Result<Unit> =
        try {
            callCoordinatorService.upsertCallMembers(
                UpsertCallMembersRequest(
                    call_cid = cid,
                    members = users.map { user ->
                        MemberInput(
                            user_id = user.id,
                            role = user.role
                        )
                    }
                )
            )

            Success(Unit)
        } catch (error: Throwable) {
            Failure(VideoError(error.message, error))
        }

    override suspend fun queryUsers(request: QueryMembersRequest): Result<List<CallUser>> =
        try {
            val users =
                callCoordinatorService.queryMembers(request)
                    .members
                    ?.users
                    ?.values
                    ?.toList()
                    ?: emptyList()

            Success(users.map { it.toCallUser() })
        } catch (error: Throwable) {
            Failure(VideoError(error.message, error))
        }
}
