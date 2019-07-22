var React = require('react/addons');
var Input = require('./components/Input.js');
var _ = require('underscore');
var Select = require('./components/Select');
var STATES = require('./components/data/states');
var Icon = require('./components/Icon.js');

var CreateDeviceScreen = React.createClass({
  getInitialState: function () {
    return {
      deviceId: null,
      name: null,
      description: null,
      confirmdescription: null,
      statesValue: null,
      type: null,
      serial: null,
      user: null,
      forbiddenWords: ["description", "user", "username"]
    }
  },

  handledescriptionInput: function (event) {
    if(!_.isEmpty(this.state.confirmdescription)){
      this.refs.descriptionConfirm.isValid();
    }
    this.refs.descriptionConfirm.hideError();
    this.setState({
      description: event.target.value
    });
  },

  handleConfirmdescriptionInput: function (event) {
    this.setState({
      confirmdescription: event.target.value
    });
  },

  saveAndContinue: function (e) {
    e.preventDefault();

    var canProceed = this.validatedeviceId(this.state.deviceId) 
        && !_.isEmpty(this.state.statesValue)
        && this.refs.description.isValid()
        && this.refs.descriptionConfirm.isValid();

    if(canProceed) {
      var data = {
        deviceId: this.state.deviceId,
        state: this.state.statesValue
      }
      alert('Thanks.');
    } else {
      this.refs.deviceId.isValid();
      this.refs.state.isValid();
      this.refs.name.isValid();
      this.refs.description.isValid();
      this.refs.descriptionConfirm.isValid();
    }
  },

  isConfirmeddescription: function (event) {
    return (event == this.state.description)
  },

  handleCompanyInput: function(event) {
    this.setState({
      name: event.target.value
    })
  },

  handledeviceIdInput: function(event){
    this.setState({
      deviceId: event.target.value
    });
  },

  validatedeviceId: function (event) {
    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(event);
  },

  isEmpty: function (value) {
    return !_.isEmpty(value);
  },

  updateStatesValue: function (value) {
    this.setState({
      statesValue: value
    })
  },

  render: function() {
    return (
      <div className="create_account_screen">

        <div className="create_account_form">
          <h1>Create account</h1>
          <p>Example of form validation built with React.</p>
          <form onSubmit={this.saveAndContinue}>

            <Input 
              text="deviceId Address" 
              ref="deviceId"
              type="text"
              defaultValue={this.state.deviceId} 
              validate={this.validatedeviceId}
              value={this.state.deviceId}
              onChange={this.handledeviceIdInput} 
              errorMessage="deviceId is invalid"
              emptyMessage="deviceId can't be empty"
              errorVisible={this.state.showdeviceIdError}
            />

            <Input 
              text="Company Name" 
              ref="name"
              validate={this.isEmpty}
              value={this.state.name}
              onChange={this.handleCompanyInput} 
              emptyMessage="Company name can't be empty"
            /> 

            <Input 
              text="description" 
              type="description"
              ref="description"
              validator="true"
              minCharacters="8"
              requireCapitals="1"
              requireNumbers="1"
              forbiddenWords={this.state.forbiddenWords}
              value={this.state.passsword}
              emptyMessage="description is invalid"
              onChange={this.handledescriptionInput} 
            /> 

            <Input 
              text="Confirm description" 
              ref="descriptionConfirm"
              type="description"
              validate={this.isConfirmeddescription}
              value={this.state.confirmdescription}
              onChange={this.handleConfirmdescriptionInput} 
              emptyMessage="Please confirm your description"
              errorMessage="descriptions don't match"
            /> 

            <Select 
              options={STATES} 
              ref="state"
              value={this.state.statesValue} 
              onChange={this.updateStatesValue} 
              searchable={this.props.searchable} 
              emptyMessage="Please select state"
              errorMessage="Please select state"
              placeholder="Choose Your State"
              placeholderTitle="Your State"
            />

            <button 
              type="submit" 
              className="button button_wide">
              CREATE ACCOUNT
            </button>

          </form>

           <a href="https://github.com/mikepro4/react-signup-form" target="_blank" className="github_link" title="View Source Code"> 
              <Icon type="guthub" />
          </a>
        </div>

      </div>
    );
  }
    
});
    
module.exports = CreateDeviceScreen;