import React from 'react';
import PropTypes from 'prop-types';

export default class ReportForm extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            host: '',
            port: ''
        }
    }

    onChange(event) {
        const newState = {
            ...this.state,
            [event.target.name]: event.target.value
        };

        this.setState(newState);
    }

    onClick(event) {
        event.preventDefault();

        const {host, port} = this.state;
        this.props.fetchReport(host, port);

        this.setState({
            host: '',
            port: ''
        });
    }

    activeForm() {
        return (<div>
            <div>
                <span>Host:</span>
                <input type='text'
                       placeholder='Host/IP'
                       name='host'
                       value={this.state.host}
                       onChange={(e)=> this.onChange(e)}
                />
            </div>
            <div>
                <span>Port:</span>
                <input type='text'
                       placeholder='Port'
                       name='port'
                       value={this.state.port}
                       onChange={(e)=>this.onChange(e)}
                />
            </div>
            <button onClick={(e)=>this.onClick(e)} >Get SSL Report!</button>
        </div>);
    }

    inactiveForm() {
        return null;
    }


    render() {
        if(this.props.isActiveForm) {
            return this.activeForm();
        } else {
            return this.inactiveForm();
        }
    }
}

ReportForm.propTypes = {
    isActiveForm: PropTypes.bool.isRequired,
    fetchReport: PropTypes.func.isRequired
};