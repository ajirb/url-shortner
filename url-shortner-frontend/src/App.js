import axios from 'axios';
import React, { Component } from 'react';
import './App.css';

class App extends Component {
  constructor (props){
    super(props);

    this.onChangeUrl = this.onChangeUrl.bind(this);
  }

  onChangeUrl (e) {
    const req = {url:e.target.value};
    axios.post('http://localhost:8081/shortener',req)
    .then(res=>{
      console.log(res.data);
      document.getElementById('shortened_url').innerHTML = 
      `<h3>Shortened URL : <a href=${res.data} target=”_blank”>${res.data}</a></h3>`;

      const req = {
        url:res.data
      }
      axios.post('http://localhost:8081/QR',req,{ responseType: 'arraybuffer' })
      .then(res => {
        let blob = new Blob(
          [res.data], 
          { type: res.headers['content-type'] }
        )
        const img = document.createElement('img')
        img.src = window.URL.createObjectURL(blob);
        let img_div = document.getElementById('qr_image');
        img_div.innerHTML='<h3>QR image : </h3>';
        img_div.append(img);
      }) 
    })
    .catch(err => {
      document.getElementById('shortened_url').innerHTML = '<h3>Unable to Shorten URL</h3>';
      document.getElementById('qr_image').innerText = '';
      console.log(err)});
  }

  render(){
    return (
      <div>
        <div className="wrapper">
          <script src="https://kit.fontawesome.com/b99e675b6e.js"></script>
          <div className="search_box">
            <div className="search_btn"><i className="fas fa-search"></i></div>
            <input type="text" className="input_search" placeholder="Enter URL..." onChange={this.onChangeUrl}/>
          </div>
        </div>
        <div id="shortened_url"></div>
        <div id="qr_image"></div>
      </div>
    );
  }
  
}

export default App;
