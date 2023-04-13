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

import org.openapitools.client.models.CallResponse
import org.openapitools.client.models.MemberResponse




import com.squareup.moshi.Json

/**
 * This event is sent when one or more members are added to a call
 *
 * @param call 
 * @param callCid 
 * @param createdAt 
 * @param members the members added to this call
 * @param type The type of event: \"call.member_added\" in this case
 */


data class CallMemberAddedEvent (

    @Json(name = "call")
    val call: CallResponse,

    @Json(name = "call_cid")
    val callCid: kotlin.String,

    @Json(name = "created_at")
    val createdAt: java.time.OffsetDateTime,

    /* the members added to this call */
    @Json(name = "members")
    val members: kotlin.collections.List<MemberResponse>,

    /* The type of event: \"call.member_added\" in this case */
    @Json(name = "type")
    val type: kotlin.String

): VideoEvent(), WSCallEvent{ 

    override fun getCallCID(): String {
        return callCid
    }

    override fun getEventType(): String {
        return type
    }
}



