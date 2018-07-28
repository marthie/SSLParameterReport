/*

 The MIT License (MIT)

 Copyright (c) 2018 Marius Thiemann <marius dot thiemann at ploin dot de>

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.


 */

import {SUBMIT_FORM, FETCH_REPORT_RESPONSE, FETCH_REPORT_FAILURE, NEW_REPORT, CLEAR_FORM} from './ActionTypes'
import {fetchSSLReport} from '../backend/RESTClient';


export function submitForm(formData) {
    return {
        type: SUBMIT_FORM,
        formData
    };
}

export function fetchResponseOnSuccess(data) {
    return {
        type: FETCH_REPORT_RESPONSE,
        sslReports: data
    };
}

export function fetchResponseOnFailure(error) {
    return {
        type: FETCH_REPORT_FAILURE,
        fetchError: error
    };
}

export function fetchReport(formData) {
    return function(dispatch) {
        return fetchSSLReport(formData)
            .done((data)=> dispatch(fetchResponseOnSuccess(data))).fail((jqXHR, textStatus, errorThrown) => {
                let error = JSON.parse(jqXHR.responseText);
                dispatch(fetchResponseOnFailure(error));
            });
    };
}

export function newReport() {
    return {
        type: NEW_REPORT
    };
}

export function clearForm() {
    return {
      type: CLEAR_FORM
    };
}