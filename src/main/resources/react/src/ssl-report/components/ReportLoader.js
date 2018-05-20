import React from 'react';
import PropTypes from 'prop-types';

export default class ReportLoader extends React.Component {

    constructor(props) {
        super(props);
    }

    componentWillMount() {
        this.props.fetchReport();
    }

    render() {
        return(<div className="row">
            <div className="col-xs-12">
                <div className="alert alert-info">
                    <img  src="pics/ajax-loader.gif" />
                    <strong> Loading...</strong>
                </div>
            </div>
        </div>);
    }
}

ReportLoader.propTypes = {
    fetchReport: PropTypes.func.isRequired
}