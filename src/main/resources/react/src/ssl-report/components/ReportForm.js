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

const initState = {
    host: '',
    port: ''
};

class ReportForm extends React.Component {

    constructor(props) {
        super(props);

        this.state = initState;
    }

    onChange(event) {
        const newState = {
            [event.target.name]: event.target.value
        };

        this.setState(newState);
    }

    onClick(event) {
        event.preventDefault();

        const {host, port} = this.state;
        const {submitReport} = this.props;

        submitReport({host, port});
    }

    form() {
        return (<div className="row">
            <div className="col-xs-12">
                <div className="panel panel-primary">
                    <div className="panel-heading">Input</div>
                    <div className="panel-body">
                        <form className="form-inline">
                            <div className="form-group">
                                <label htmlFor="host"
                                       style={{marginLeft: 5 + 'px', marginRight: 5 + 'px'}}>Host:</label>
                                <input type='text'
                                       placeholder='Host/IP'
                                       name='host'
                                       value={this.state.host}
                                       onChange={(e) => this.onChange(e)}
                                       className="form-control"
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="port"
                                       style={{marginLeft: 5 + 'px', marginRight: 5 + 'px'}}>Port:</label>
                                <input type='text'
                                       placeholder='Port'
                                       name='port'
                                       value={this.state.port}
                                       onChange={(e) => this.onChange(e)}
                                       className="form-control"
                                />
                            </div>
                            <button
                                onClick={(e) => this.onClick(e)}
                                className="btn btn-primary"
                                style={{marginLeft: 15 + 'px'}}>
                                Get SSL Report
                                <span className="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>);
    }


    render() {
        return this.form();
    }
}

import {connect} from 'react-redux';
import {submitReport} from '../actions/reportActions'

function mapDispatchToProps(dispatch) {
    return {
        submitReport: (formData) => dispatch(submitReport(formData))
    };
}

export default connect(null, mapDispatchToProps) (ReportForm);