import {Component} from "react";
import {Button, InputGroup, Modal,ModalBody, ModalFooter, ModalHeader } from 'react-bootstrap';
import { Input, InputGroupText } from "reactstrap";

export default class AddModal extends Component{
    /**
     * Modal used to add a new club into the container 
     */

    constructor(props){
        super(props)
        this.state={
            name:props.name,
            city:props.city,
            genre:props.genre,
            capacity: props.capacity,
            yellow: props.yellow
        }
    }

    toggle=()=>{
        this.setState({ 
            name: "",
            city: "",
            genre: "",
            capacity: "",
            yellow: ""
        });
        this.props.cancel();
    }
    
    add = () => {
        if (!this.state.name || !this.state.genre || !this.state.capacity || !this.state.city) {
            alert("All fields are required");
            return;
        }
      
        const yellow = this.state.yellow 
            ? this.state.yellow 
            : Math.round(parseInt(this.state.capacity) * 0.8);

        this.props.onAdd(
            this.state.name,
            this.state.capacity,
            this.state.genre,
            this.state.city,
            yellow  
        );
        this.toggle();
    }
    render(){
        return(
            <Modal show={this.props.show} onHide={this.toggle}>
                <ModalBody>
                    <InputGroup>
                        <InputGroupText>Name:</InputGroupText>
                        <Input value={this.state.name}
                            onChange={(e)=>this.setState({name:e.target.value})}/>

                    </InputGroup>
                    <InputGroup>
                        <InputGroupText>City:</InputGroupText>
                        <Input value={this.state.city}
                            onChange={(e)=>this.setState({city:e.target.value})}/>
                    </InputGroup>
                    <InputGroup>
                        <InputGroupText>Genre:</InputGroupText>
                        <Input value={this.state.genre}
                            onChange={(e)=>this.setState({genre:e.target.value})}/>
                    </InputGroup>
                    <InputGroup>
                        <InputGroupText>Capacity:</InputGroupText>
                        <Input value={this.state.capacity}
                            onChange={(e)=>this.setState({capacity:e.target.value})}/>
                    </InputGroup>
                     <InputGroup> 
                    <InputGroupText>Yellow Threshold (optional):</InputGroupText>
                    <Input 
                        value={this.state.yellow}
                        onChange={(e) => this.setState({ yellow: e.target.value })}/>
                </InputGroup>
                </ModalBody>
                <ModalFooter>
                    <Button variant="secondary" size="lg" onClick={this.add}>Add</Button>
                    <Button variant="secondary" size="lg" onClick={this.toggle}>Cancel</Button>
                </ModalFooter>
            </Modal>
        );
    }
}