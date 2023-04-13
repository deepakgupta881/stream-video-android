/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package org.openapitools.client.models

import org.openapitools.client.models.CallSettingsRequest
import org.openapitools.client.models.MemberRequest
import org.openapitools.client.models.UserRequest




import com.squareup.moshi.Json

/**
 * 
 *
 * @param createdBy 
 * @param createdById 
 * @param custom 
 * @param members 
 * @param settingsOverride 
 * @param startsAt 
 * @param team 
 */


data class CallRequest (

    @Json(name = "created_by")
    val createdBy: UserRequest? = null,

    @Json(name = "created_by_id")
    val createdById: kotlin.String? = null,

    @Json(name = "custom")
    val custom: kotlin.collections.Map<kotlin.String, kotlin.Any>? = null,

    @Json(name = "members")
    val members: kotlin.collections.List<MemberRequest>? = null,

    @Json(name = "settings_override")
    val settingsOverride: CallSettingsRequest? = null,

    @Json(name = "starts_at")
    val startsAt: java.time.OffsetDateTime? = null,

    @Json(name = "team")
    val team: kotlin.String? = null

)




