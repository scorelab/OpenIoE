import React, { Component } from 'react';
import { Link } from 'react-router-dom';

import IconButton from '@material-ui/core/IconButton';
import InfoIcon from '@material-ui/icons/Info';

class SignUpForm extends Component {
  constructor() {
    super();

    this.state = {
      email: '',
      password: '',
      name: '',
      hasAgreed: false,
      disabledButtonColor: '#9fcfff',
      disabledTextColor: '#808080'
    };

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(e) {
    let target = e.target;
    let value = target.type === 'checkbox' ? target.checked : target.value;
    let name = target.name;

    this.setState({
      [name]: value
    });
  }

  handleSubmit(e) {
    e.preventDefault();

    console.log('The form was submitted with the following data:');
    console.log(this.state);
  }

  passwordCheckHandler = () => {
    alert(" Rules for a valid Password : \n 1- Minimum password length is 6. \n 2- Password must contain atleast an uppercase letter \n 3- Password must contain atleast an lowercase letter. \n 4- Password must contain atleast a digit. \n 5- Password must contain atleast a special character. (@,$,#,%,&)")
  }

  render() {
    let content = null;
    if (this.state.email.length && this.state.password.length && this.state.name.length && this.state.hasAgreed &&
      new RegExp("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{6,}$").test(this.state.password)) {
      content = <button className="FormField__Button mr-20"
        onClick={() => { console.log(this.state.isValid) }}
      >Sign Up</button>
    }
    else {
      content = <button className="FormField__Button mr-20"
        style={
          {
            backgroundColor: this.state.disabledButtonColor,
            color: this.state.disabledTextColor
          }
        }
        disabled
      >Sign Up</button>
    }

    return (
      <div className="container" >
        <div className="FormCenter">
          <form onSubmit={this.handleSubmit} className="FormFields">
            <div className="FormField">
              <label className="FormField__Label" htmlFor="name">Full Name</label>
              <input type="text" id="name" className="FormField__Input" placeholder="Enter your full name" name="name" value={this.state.name} onChange={this.handleChange} />
            </div>

            <div className="FormField">
              <div style={{ flexDirection: 'row', display: 'flex' }}>
                <label className="FormField__Label" htmlFor="password">Password</label>
                <IconButton aria-label="info" onClick={this.passwordCheckHandler}>
                  <InfoIcon />
                </IconButton>
              </div>
              <input type="password" id="password" className="FormField__Input" placeholder="Enter your password" name="password" value={this.state.password} onChange={this.handleChange} />
            </div>

            <div className="FormField">
              <label className="FormField__Label" htmlFor="email">E-Mail Address</label>
              <input type="email" id="email" className="FormField__Input" placeholder="Enter your email" name="email" value={this.state.email} onChange={this.handleChange} />
            </div>

            <div className="FormField">
              <label className="FormField__CheckboxLabel">
                <input className="FormField__Checkbox" type="checkbox" name="hasAgreed" value={this.state.hasAgreed} onChange={this.handleChange} /> I agree all statements in <a href="" className="FormField__TermsLink">terms of service</a>
              </label>
            </div>

            <div className="FormField">
              {content}
              <Link to="/sign-in" className="FormField__Link">I'm already member</Link>
            </div>
          </form>
        </div>
      </div>
    );
  }
}
export default SignUpForm;