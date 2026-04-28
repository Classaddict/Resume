import {Component} from "react";
import {Button, InputGroup, Modal,ModalBody, ModalFooter, ModalHeader } from 'react-bootstrap';
import { Input, InputGroupText } from "reactstrap";


export default class EditModal extends Component{
    /**
     * Modal for editing teh attributes of a club
     * 
     */

    constructor(props){
        super(props);
        this.state = {
            name: props.name,
            city: props.city,
            genre: props.genre,
            capacity: props.capacity,
            yellow: props.yellow
        };
    }
    toggle=()=>{
        this.props.cancel();
    }

    updateName=(e)=>{
        this.setState({name : e.target.value});
    }
    updateCity=(e)=>{
        this.setState({city : e.target.value});
    }
    updateGenre=(e)=>{
        this.setState({genre : e.target.value});
    }
    updateCapacity=(e)=>{
        this.setState({capacity : e.target.value});
    }

    updateYellow=(e)=>{
        this.setState({yellow : e.target.value});
    }

    saveState = () => {
        this.props.callback(this.props.clubKey, this.state.name, this.state.city, this.state.genre, this.state.capacity,this.state.yellow);
        this.props.cancel(); 
    }

    render(){
        const {name,city,genre,capacity,clubKey,yellow}= this.state;
        return(
            <Modal show={this.props.show} onHide={this.toggle}>
                <ModalBody>
                    <InputGroup>
                        <InputGroupText>Name:</InputGroupText>
                        <Input value={name} onChange={this.updateName}/>

                    </InputGroup>
                    <InputGroup>
                        <InputGroupText>City:</InputGroupText>
                        <Input value={city} onChange={this.updateCity}/>
                    </InputGroup>
                    <InputGroup>
                        <InputGroupText>Genre:</InputGroupText>
                        <Input value={genre} onChange={this.updateGenre}/>
                    </InputGroup>
                    <InputGroup>
                        <InputGroupText>Yellow Threshold:</InputGroupText>
                        <Input value={yellow} onChange={this.updateYellow}/>
                    </InputGroup>
                    <InputGroup>
                        <InputGroupText>Capacity:</InputGroupText>
                        <Input value={capacity} onChange={this.updateCapacity}/>
                    </InputGroup>
                </ModalBody>
                <ModalFooter>
                    <Button variant="secondary" onClick={this.toggle}>Cancel</Button>
                    <Button variant="secondary" onClick={this.saveState}>Save </Button>
                </ModalFooter>
            </Modal>
        );
    }
}