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
import ReportController from './ReportController';

export default class Layout extends React.Component {

    scrollHandler(event) {
        var scrollTop = 0;
        document.body.scrollTop ? scrollTop = document.body.scrollTop : scrollTop = 0;
        document.documentElement.scrollTop ? scrollTop = document.documentElement.scrollTop : scrollTop = 0;

        console.log("ScrollTop: " + scrollTop);
    }

    componentDidMount() {
        window.addEventListener('scroll', this.scrollHandler);
    }

    componentWillUnmount() {
        window.removeEventListener('scroll', this.scrollHandler);
    }

    render() {
        const layout = (<React.Fragemnt>
            <div className="container">
                <div className="row">
                    <div className="page-header">
                        <h1>SSL Report</h1>
                    </div>
                </div>
                <ReportController/>
            </div>
        </React.Fragemnt>);

        return layout;
    }
}