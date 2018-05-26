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
import PropTypes from 'prop-types';

const VISIBLE_STATE = {
    isVisible: true,
    iconClassNames: "glyphicon glyphicon-chevron-up",
    bodyStyle: {display: 'block'}
};

const HIDE_STATE = {
    isVisible: false,
    iconClassNames: "glyphicon glyphicon-chevron-down",
    bodyStyle: {display: 'none'}
};

export default class ReportPanel extends React.Component {

    constructor(props) {
        super(props);

        this.state = HIDE_STATE;
    }

    slideUpOrDown(event) {
        const {isVisible} = this.state;

        if(isVisible) {
            this.setState(HIDE_STATE);
        } else {
            this.setState(VISIBLE_STATE);
        }
    }

    reportPanel() {
        const {panelTitle, children} = this.props;

        const panel = (<div className="row">
            <div className="col-xs-12">
            <div className="panel panel-default" style={{marginTop: 10 + 'px'}}>
                <div className="panel-heading">
                    <h3 className="panel-title">{panelTitle}</h3>
                    <span className="pull-right"
                          style={{marginTop: -15 + 'px'}}
                          onClick={(e)=>this.slideUpOrDown(e)} >
                        <i className={this.state.iconClassNames}></i>
                    </span>
                </div>
                <div className="panel-body" style={this.state.bodyStyle}>
                    {children}
                </div>
            </div>
            </div>
        </div>);

        return panel;
    }

    render() {
        return this.reportPanel();
    }
}

ReportPanel.propTypes = {
    panelTitle: PropTypes.string.isRequired
}