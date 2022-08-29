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

package io.getstream.video.android.webrtc.signal

import io.getstream.video.android.utils.Result
import stream.video.sfu.IceCandidateRequest
import stream.video.sfu.IceCandidateResponse
import stream.video.sfu.JoinRequest
import stream.video.sfu.JoinResponse
import stream.video.sfu.SendAnswerRequest
import stream.video.sfu.SendAnswerResponse
import stream.video.sfu.SetPublisherRequest
import stream.video.sfu.SetPublisherResponse

public interface SignalClient {

    public suspend fun sendAnswer(request: SendAnswerRequest): Result<SendAnswerResponse>

    public suspend fun sendIceCandidate(request: IceCandidateRequest): Result<IceCandidateResponse>

    public suspend fun join(request: JoinRequest): Result<JoinResponse>

    public suspend fun setPublisher(request: SetPublisherRequest): Result<SetPublisherResponse>
}
