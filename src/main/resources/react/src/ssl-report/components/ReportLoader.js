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
        return(<div>
            <img  src="pics/ajax-loader.gif" />
        </div>);
    }
}

ReportLoader.propTypes = {
    fetchReport: PropTypes.func.isRequired
}