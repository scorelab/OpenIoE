import React, { Component, PropTypes } from 'react';
import { findDOMNode } from 'react-dom';

export default class AddToList extends Component {
  render() {
    return (
      <div>
        <input type='text' ref='input' />
        <button onClick={ e => this.handleClick(e) }>
          Add
        </button>
        <button onClick={ e => this.handleReset(e) }>
          Reset
        </button>
      </div>
    );
  }

  handleClick(e) {
    const inputNode = findDOMNode(this.refs.input);
    const text = inputNode.value.trim();
    this.props.onAddClick(text);
    inputNode.value = '';
  }

  handleReset(e) {
    const inputNode = findDOMNode(this.refs.input);
    const text = "";
    this.props.onResetClick(text);
    inputNode.value = '';
  }
}

AddTodo.propTypes = {
  onAddClick: PropTypes.func.isRequired
};
