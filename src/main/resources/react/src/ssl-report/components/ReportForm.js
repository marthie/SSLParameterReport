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

const initLocalState = {
    host: '',
    port: ''
};

class ReportForm extends React.Component {

    constructor(props) {
        super(props);

        const {reportState} = props;

        if(reportState.requestData) {
            this.state = reportState.requestData;
        } else {
            this.state = initLocalState;
        }
    }

    onChange(event) {
        const newState = {
            [event.target.name]: event.target.value
        };

        this.setState(newState);
    }

    submit(event) {
        event.preventDefault();

        const {host, port} = this.state;
        const {submitForm} = this.props;

        submitForm({host, port});
    }

    clear(event) {
        event.preventDefault();

        const {clearForm} = this.props;
        clearForm();
        this.setState(initLocalState);
    }

    form() {
        return (<div className="row">
            <div className="col-xs-12">
                <div className="panel panel-primary">
                    <div className="panel-heading">Generate SSL/TLS Report for...</div>
                    <div className="panel-body">
                        <form>
                            <div className="form-group">
                                <label htmlFor="host">Host</label>
                                <input type='text'
                                       placeholder='Host/IP'
                                       name='host'
                                       value={this.state.host}
                                       onChange={(e) => this.onChange(e)}
                                       className="form-control"
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="port">Port</label>
                                <input type='text'
                                       placeholder='Port'
                                       name='port'
                                       value={this.state.port}
                                       onChange={(e) => this.onChange(e)}
                                       className="form-control"
                                />
                            </div>
                            <div className="well center-block">
                            <button
                                onClick={(e) => this.submit(e)}
                                className="btn btn-primary btn-block">
                                Get SSL Report
                                <span className="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
                            </button>
                            <button className="btn btn-default btn-block"
                                    onClick={(e)=> this.clear(e)}>Clear</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>);
    }

    alert() {
        const {reportState} = this.props;

        return (<div className="row">
            <div className="col-xs-12">
                <div className="alert alert-danger" role="alert">
                    <strong>Error: {reportState.fetchError.type} (type)</strong>
                    <p>Message = {reportState.fetchError.message} </p>
                </div>
            </div>
        </div>);
    }


    render() {
        const {reportState} = this.props;

        return (<React.Fragment>
            {this.form()}
            {reportState.fetchError? this.alert() : null}
        </React.Fragment>);
    }
}

import {connect} from 'react-redux';
import {submitForm, clearForm} from '../actions/reportActions'

function mapStateToProps(state) {
    return {
        reportState: state
    };
}

function mapDispatchToProps(dispatch) {
    return {
        submitForm: (formData) => dispatch(submitForm(formData)),
        clearForm: () => dispatch(clearForm())
    };
}

export default connect(mapStateToProps, mapDispatchToProps) (ReportForm);