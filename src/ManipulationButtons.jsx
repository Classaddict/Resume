import {Component} from "react";
import { Card, CardBody, CardText, Button, Container, Row, Col, CardFooter, Dropdown,DropdownMenu,DropdownToggle,DropdownItem} from 'react-bootstrap';
import AddModal from "./AddModal";
import DeleteModal from "./DeleteModal";
export default class ManipulationButtons extends Component{
    /**
     * Class for the buttons to add/remove/edit the clubs
     */
    constructor(props) {
    super(props);
    this.state = { 
        data: null,
        allData: null  
    };
}
    state = { showAddModal: false, showRemoveModal: false, selectedCity: "All" }

    toggleAddModal=()=>{
        this.setState(prev=>({showAddModal:!prev.showAddModal}))
    }

    toggleRemoveModal=()=>{
        this.setState(prev=>({showRemoveModal:!prev.showRemoveModal}))
    }

    addClub = (name, capacity, genre, city, yellow) => {
        this.props.onAdd(name, capacity, genre, city, yellow);  
    }

    updateData = (apiResponse) => {
        this.setState({ 
            data: apiResponse,
            allData: apiResponse  
        });
    }


    handleFilter = (city) => {
        this.setState({ selectedCity: city === "" ? "All" : city });
        this.props.onFilter(city);
    }
    render(){
        const clubs=this.props.getClubs() ||[];
        const cities = [...new Set((clubs || []).map(club => club.city).filter(Boolean))];
        return(
            <Container fluid
            style={{
                position: 'fixed',
                bottom: '20px',
                left: '50%',
                transform: 'translateX(-50%)',
                display: 'flex',
                justifyContent: 'center',
                gap: '10px',
                width: 'auto'
            }}>
                <Button variant="secondary" size="lg" onClick={this.toggleAddModal}>Add</Button>
                <Button variant="secondary" size="lg" onClick={this.toggleRemoveModal}>Delete</Button>
                <AddModal
                    show={this.state.showAddModal}
                    cancel={this.toggleAddModal}
                    onAdd={this.addClub}
                />
                <DeleteModal
                    show={this.state.showRemoveModal}
                    cancel={this.toggleRemoveModal}
                    clubs={clubs}
                    remove={this.props.remove}
                />
                <Dropdown>
                    <Dropdown.Toggle variant="secondary">Filter by City
                        {this.state.selectedCity}
                    </Dropdown.Toggle>
                    <Dropdown.Menu>
                        <Dropdown.Item onClick={() => this.props.onFilter("")}>All</Dropdown.Item>
                        {cities.map(city => (
                            <Dropdown.Item key={city} onClick={() => this.props.onFilter(city)}>
                                {city}
                            </Dropdown.Item>
                        ))}
                    </Dropdown.Menu>
                </Dropdown>
            </Container>
        );
    }

}