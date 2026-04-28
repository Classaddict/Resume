import 'bootstrap/dist/css/bootstrap.min.css';
import { Component, useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import heroImg from './assets/hero.png'
import './index.css'
import Header from './Header'
import ClubContainer from './ClubContainer'
import ManipulationButtons from './ManipulationButtons';


export default class App extends Component {
  constructor(props){
    super(props);
    this.state = {
        filterCity: ""
    };
  };

  increase = (name) => {
    const data= new FormData();
    data.append("name",name);
    fetch('http://localhost:5001/increase', {method:'PUT', body:data})
    .then(response=>response.json())
    .then(()=>this.clubContainerRef.fetchData())
    .catch(error=> console.log(error))
  };

  decrease = (name) => {
      const data= new FormData();
      data.append("name",name);
      fetch('http://localhost:5001/decrease', {method:'PUT', body:data})
      .then(response=>response.json())
      .then(()=>this.clubContainerRef.fetchData())
      .catch(error=> console.log(error))
  };

  handleSelect = (club) => {
    this.setState({ selectedClub: club });
  };

  updateClub = (id, name, city, genre,capacity,yellow) => {
    const data = new FormData();
    data.append("name", name);
    data.append("city", city);
    data.append("genre", genre);
    data.append("capacity", capacity);
    data.append("yellow", yellow);

    fetch(`http://localhost:5001/clubs/${id}`, { method: 'PUT', body: data })
        .then(response => response.json())
        .then(() => this.clubContainerRef.fetchData()) 
        .catch(error => console.log(error));
  }
  addClub = (name, capacity, genre, city, yellow) => {
    const data = new FormData();
    data.append("name", name);
    data.append("capacity", capacity);
    data.append("genre", genre);
    data.append("city", city);
    data.append("yellow", yellow);  

    fetch('http://localhost:5001/clubs', { method: 'POST', body: data })
        .then(response => response.json())
        .then(() => this.clubContainerRef.fetchData())
        .catch(error => console.log(error));
}

  removeClub =(name)=>{
    fetch(`http://localhost:5001/clubs/${name}`, {method:'DELETE'})
    .then(response=>response.json())
    .then(()=>this.clubContainerRef.fetchData())
    .catch(error=>console.log(error))
  }
  
  filterByCity = (city) => {
    this.setState({ filterCity: city });
  }

  

  render(){
    const { filterCity } = this.state;
    return (
      <div>
        <Header />
        <ClubContainer 
          ref={ref => this.clubContainerRef = ref}
          onIncrease={this.increase}
          onDecrease={this.decrease}
          callback={this.updateClub}
          filterCity={filterCity}
        />
        <ManipulationButtons
          onAdd={this.addClub}
          remove={this.removeClub}
          onFilter={this.filterByCity}
          getClubs={() => this.clubContainerRef?.state.data || []}
        />
      </div>
      
  );
  }
}
