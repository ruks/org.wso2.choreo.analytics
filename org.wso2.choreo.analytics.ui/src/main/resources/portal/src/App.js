import React, { Component } from 'react';
import PrimarySearchAppBar from './appBar'
import Content from './Content';

class App extends Component {
  render() {
    const user = localStorage.getItem("user");
    const token = document.cookie.includes("capp1=")
    if (user == null || !token) {
      console.log("no user found. Redirect to login page.")
      window.location = '/choreo-analytics/login.jsp';
    } else {
      console.log('Loged in as: ' + user);
    }
    console.log(user);
    return (
      <div className="App">
        <PrimarySearchAppBar user={user} />
        <Content />
      </div>
    );
  }
}

export default App;