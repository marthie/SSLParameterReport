import React from 'react';
import PropTypes from 'prop-types';
import $ from 'jquery';

const SHOW_STATE = {
    show: true
}

const HIDE_STATE = {
    show: false
}

export default class ReportJump extends React.Component {

    constructor(props) {
        super(props);

        this.state = HIDE_STATE;
        this.scrollHandler = this.scrollHandler.bind(this);
    }

    componentDidMount() {
        window.addEventListener('scroll', this.scrollHandler);
    }

    componentWillUnmount() {
        window.removeEventListener('scroll', this.scrollHandler);
    }

    scrollHandler(event) {
        var scrollIndex = 0;

        document.body.scrollTop ? scrollIndex = document.body.scrollTop : scrollIndex = 0;
        document.documentElement.scrollTop ? scrollIndex = document.documentElement.scrollTop : scrollIndex = 0;

        if(scrollIndex >= 500) {
            this.setState(SHOW_STATE);
        }

        if(scrollIndex < 500) {
            this.setState(HIDE_STATE);
        }
    }



    render() {
        const jumpTarget = "jumpPosition";

        const reportJump = (<React.Fragment>
            <div id={jumpTarget}></div>
            <JumpButton jumpTarget={jumpTarget} isVisible={this.state.show} />
        </React.Fragment>);

        return reportJump;
    }
}

class JumpButton extends React.Component {

    constructor(props) {
        super(props);

        this.containerRef = React.createRef();
    }

    getStyle() {
        return {
            position: 'fixed',
            zIndex: 1000,
            top: 50 + 'px',
            right: 50 + 'px'
        };
    }

    componentDidMount() {
        const {isVisible} = this.props;

        if(isVisible) {
            $(this.containerRef.current).fadeIn();
        }
    }

    UNSAFE_componentWillUpdate(nextProps, nextState) {
        const {isVisible} = nextProps;

        if(isVisible) {
            $(this.containerRef.current).fadeIn();
        }

        if(!isVisible) {
            $(this.containerRef.current).fadeOut();
        }
    }

    render() {
        const {jumpTarget} = this.props;

        const jumpButton = (<div style={this.getStyle()} ref={this.containerRef}>
            <button className="btn btn-default"
                    onClick={()=> window.location.href='#' + jumpTarget }>Back to top!</button>
        </div>);

        return jumpButton;
    }
}

JumpButton.propTypes = {
    jumpTarget: PropTypes.string.isRequired,
    isVisible: PropTypes.bool.isRequired
};