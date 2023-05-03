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

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package org.openapitools.client.models

import org.openapitools.client.models.CallResponse
import org.openapitools.client.models.Credentials
import org.openapitools.client.models.MemberResponse
import org.openapitools.client.models.UserResponse




import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import org.openapitools.client.infrastructure.Serializer

/**
 *
 *
 * @param blockedUsers
 * @param call
 * @param credentials
 * @param duration Duration of the request in human-readable format
 * @param members
 * @param membership
 */


data class GetCallEdgeServerResponse (

    @Json(name = "blocked_users")
    val blockedUsers: kotlin.collections.List<UserResponse>,

    @Json(name = "call")
    val call: CallResponse,

    @Json(name = "credentials")
    val credentials: Credentials,

    /* Duration of the request in human-readable format */
    @Json(name = "duration")
    val duration: kotlin.String,

    @Json(name = "members")
    val members: kotlin.collections.List<MemberResponse>,

    @Json(name = "membership")
    val membership: MemberResponse? = null

)
