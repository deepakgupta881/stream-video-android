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

import org.openapitools.client.models.APNS




import com.squareup.moshi.Json

/**
 * 
 *
 * @param apns 
 * @param enabled 
 */


data class EventNotificationSettings (

    @Json(name = "apns")
    val apns: APNS,

    @Json(name = "enabled")
    val enabled: kotlin.Boolean

)




