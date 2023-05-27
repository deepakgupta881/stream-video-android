package org.openapitools.client.apis


import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path






import org.openapitools.client.models.ListRecordingsResponse
import org.openapitools.client.models.StartRecordingResponse
import org.openapitools.client.models.StopRecordingResponse

interface RecordingApi {
    /**
     * List recordings
     * Lists recordings  Required permissions: - ListRecordings 
     * Responses:
     *  - 200: Successful response
     *  - 400: Bad request
     *  - 429: Too many requests
     *
     * @param type 
     * @param id 
     * @param session 
     * @return [ListRecordingsResponse]
     */
    @GET("/video/call/{type}/{id}/{session}/recordings")
    suspend fun listRecordings(
        @Path("type") type: String, 
        @Path("id") id: String, 
        @Path("session") session: String
    ): ListRecordingsResponse

    /**
     * Start recording
     * Starts recording  Sends events: - call.recording_started  Required permissions: - StopRecording 
     * Responses:
     *  - 201: Successful response
     *  - 400: Bad request
     *  - 429: Too many requests
     *
     * @param type 
     * @param id 
     * @return [StartRecordingResponse]
     */
    @POST("/video/call/{type}/{id}/start_recording")
    suspend fun startRecording(
        @Path("type") type: String, 
        @Path("id") id: String
    ): StartRecordingResponse

    /**
     * Stop recording
     * Stops recording  Sends events: - call.recording_stopped  Required permissions: - StopRecording 
     * Responses:
     *  - 201: Successful response
     *  - 400: Bad request
     *  - 429: Too many requests
     *
     * @param type 
     * @param id 
     * @return [StopRecordingResponse]
     */
    @POST("/video/call/{type}/{id}/stop_recording")
    suspend fun stopRecording(
        @Path("type") type: String, 
        @Path("id") id: String
    ): StopRecordingResponse

}
