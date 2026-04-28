import {Component} from "react";
import {Button, Dropdown, DropdownItem, DropdownMenu, DropdownToggle, InputGroup, Modal,ModalBody, ModalFooter, ModalHeader } from 'react-bootstrap';
import { Input, InputGroupText } from "reactstrap";

export default class DeleteModal extends Component{
    /**
     * Class that diaplys the modal allowing users to select a club they want to remove
     */
    constructor(props){
        super(props)
    }
    state = { dropOpen: false, selectedClub: null }
    toggle=()=>{
        this.props.cancel();
    }

    dropToggle = () => this.setState(prev => ({ dropOpen: !prev.dropOpen }))

    remove =()=>{
        if(this.state.selectedClub){
            this.props.remove(this.state.selectedClub.name)
        }
        this.toggle();
    }

    render(){
        return(
            <Modal show={this.props.show} onHide={this.toggle}>
                <ModalBody>
                    <Dropdown show={this.state.dropOpen} onToggle={this.dropToggle}>
                         <Dropdown.Toggle variant="secondary">
                            {this.state.selectedClub ? this.state.selectedClub.name : "Select a Club"}
                        </Dropdown.Toggle>
                        <Dropdown.Menu>
                            {this.props.clubs.map(club => (
                                <Dropdown.Item 
                                    key={club.id}
                                    onClick={() => this.setState({ selectedClub: club })}
                                >
                                    {club.name}
                                </Dropdown.Item>
                            ))}
                        </Dropdown.Menu>
                    </Dropdown>
                </ModalBody>
                <ModalFooter>
                    <Button variant="secondary" size="lg" onClick={this.remove}>Delete</Button>
                    <Button variant="secondary" size="lg" onClick={this.toggle}>Cancel</Button>
                </ModalFooter>
            </Modal>
        );
    }
}