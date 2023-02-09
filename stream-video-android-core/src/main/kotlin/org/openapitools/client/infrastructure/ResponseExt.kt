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

package org.openapitools.client.infrastructure

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import retrofit2.Response

@Throws(JsonDataException::class)
internal inline fun <reified T> Response<*>.getErrorResponse(serializerBuilder: Moshi.Builder = Serializer.moshiBuilder): T? {
    val serializer = serializerBuilder.build()
    val parser = serializer.adapter(T::class.java)
    val response = errorBody()?.string()
    if (response != null) {
        return parser.fromJson(response)
    }
    return null
}
