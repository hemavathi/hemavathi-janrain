/*
 *  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2013, Janrain, Inc.
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *  * Neither the name of the Janrain, Inc. nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 */

package com.janrain.android.capture;

import org.json.JSONObject;

/**
 * http://developers.janrain.com/documentation/capture/restful_api/
 */
public class CaptureApiError {
    public static final int EMAIL_ADDRESS_IN_USE = 380;
    private String engageToken;
    private String conflictingIdentityProvider;

    /**
     * The Capture error code. See http://developers.janrain.com/documentation/capture/restful_api/
     */
    public final int code;

    /**
     * The error string which is 1:1 associate with the code
     */
    public final String error;

    /**
     * A description of this instance of the error, which can vary for a single given code.
     */
    public final String error_description;

    /**
     * The raw JSON response
     */
    public final JSONObject raw_response;

    /**
     * Indicates an API response that could not be parsed. Has no meaningful fields values.
     */
    public static final CaptureApiError INVALID_API_RESPONSE = new CaptureApiError();

    private CaptureApiError() {
        error = "INVALID_API_RESPONSE";
        code = -1;
        error_description = null;
        raw_response = null;
    }

    /**
     * Construct an error object from a JSON response
     * @param response the JSON response
     * @param engageToken the Engage auth_info token in the request precipitating this error, if any
     * @param conflictingProvider for merge errors, the identity provider in the request precipitating this
     *                            error.
     */
    /*package*/ CaptureApiError(JSONObject response, String engageToken, String conflictingProvider) {
        code = response.optInt("code");
        error = response.optString("error");
        error_description = response.optString("error_description");
        raw_response = response;
        this.engageToken = engageToken;
        this.conflictingIdentityProvider = conflictingProvider;
    }

    public boolean isInvalidApiResponse() {
        return this == INVALID_API_RESPONSE;
    }

    public String toString() {
        return "<CaptureApiError code: " + code + " error: " + error + " description: " + error_description
                + ">";
    }

    public boolean isInvalidPassword() {
        if (isInvalidApiResponse()) return false;
        if (error.equals("bad username/password combo")) return true; // legacy username/password endpoint
        return false;
    }

    /**
     * True if this object is an error representing a merge-flow is required to continue signing in.
     * @return whether a merge-account-flow is required.
     */
    public boolean isMergeFlowError() {
        return code == EMAIL_ADDRESS_IN_USE;
    }

    public String getMergeToken() {
        return engageToken;
    }

    public String getExistingAccountIdentityProvider() {
        return raw_response.optString("existing_provider");
    }

    public String getConflictingIdentityProvider() {
        return conflictingIdentityProvider;
    }
}
