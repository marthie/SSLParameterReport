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

import React from 'react';
import ReportForm from './ReportForm';
import ReportSheet from './ReportSheet';
import ReportLoader from './ReportLoader';
import {fetchSSLReport} from '../backend/RESTClient';
import {FORM_VIEW, LOADER_VIEW, SHEET_VIEW} from './ViewStates';


class ReportController extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        let currentView = null;

        const {reportState} = this.props;

        switch(reportState.activeState) {
            case FORM_VIEW:
                currentView = <ReportForm />;
                break;
            case LOADER_VIEW:
                currentView = <ReportLoader />;
                break;
            case SHEET_VIEW:
                currentView = <ReportSheet />;
                break;
            default: {
                currentView = (<p className="text-danger">`Error: ${reportState.activeState} unknown...`</p>);
            }
        }

        return currentView;
    }
};

import {connect} from 'react-redux';

function mapStateToProps(state) {
    return {
        reportState: state
    };
}

export default connect(mapStateToProps, null) (ReportController);