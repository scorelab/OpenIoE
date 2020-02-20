import React, { Component } from 'react';
import Menu from './Menu';

import logo from './logo.png';
class App extends Component {
  render() {
    let links = [
      { label: 'Home', link: '#home', active: true },
      { label: 'About', link: '#about' },
      { label: 'Portfolio', link: '#portfolio' },
      { label: 'Contact Us', link: '#contact-us' },
    ];

    return (
      <div className="container center">
        <Menu links={links} logo={logo} />
      </div>
    );
  }
}

export default App;
