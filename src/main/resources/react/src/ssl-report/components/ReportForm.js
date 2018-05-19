import React from 'react';
import PropTypes from 'prop-types';

export default class ReportForm extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            host: 'heise.de',
            port: '443'
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
        this.props.submitData(host, port);

        this.setState({
            host: '',
            port: ''
        });
    }

    form() {
        return (<div className="row">
            <div className="col-xs-12">
                <div className="panel panel-primary">
                    <div className="panel-heading">Input</div>
                    <div className="panel-body">
                        <form className="form-inline">
                            <div className="form-group">
                                <label htmlFor="host">Host:</label>
                                <input type='text'
                                       placeholder='Host/IP'
                                       name='host'
                                       value={this.state.host}
                                       onChange={(e) => this.onChange(e)}
                                       className="form-control"
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="port">Port:</label>
                                <input type='text'
                                       placeholder='Port'
                                       name='port'
                                       value={this.state.port}
                                       onChange={(e) => this.onChange(e)}
                                />
                            </div>
                            <button
                                onClick={(e) => this.onClick(e)}
                                className="btn btn-primary">
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

ReportForm.propTypes = {
    submitData: PropTypes.func.isRequired
};