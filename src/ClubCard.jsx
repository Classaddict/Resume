import { Card, CardBody, CardText, Button, Container, Row, Col, CardFooter } from 'react-bootstrap';
import {Component} from "react";
import EditModal from './EditModal';
export default class ClubCard extends Component{
    /*
        Child of ClubContaier, used to describe the contents of each ind. nightclub box
    */
   constructor(props){
    super(props);
   }
    state = { showEditModal: false }

    toggleEditModal = () => {
        this.setState(prev => ({ showEditModal: !prev.showEditModal }));
    }

    getBackgroundColor(){
        /**
         * Function used to determine the background color of each club
         */
        const{count,yellow,capacity}=this.props;
        if(count>=capacity){
            return "red";
        }else if (count>=yellow){
            return "yellow";
        }else{
            return "green";
        }
    }

    getMessage(){
        const{count,yellow,capacity}=this.props;
        if(count>=capacity){
            return "No one allowed in!";
        }else if (count>=yellow){
            return "Warn the bouncers...";
        }else{
            return "Welcome!";
        }
    }

    render(){
        const{name,count,city,genre,capacity,onIncrease,onDecrease,clubKey,yellow}=this.props;

        return(
            <Container class="container-lg">
                <Row>
                        <Col>
                        <Card style={{background:this.getBackgroundColor()}}>
                            <CardBody>
                                <p className="text-center fw-bold fs-4 mb-0">{name}</p>
                                <p className="text-center fw-bold fs-7 mb-0">City: {city}</p>
                                <p className="text-center fw-bold fs-7 mb-3">Genre: {genre}</p>
                                <p className="text-center fw-bold fs-5 mb-2">{this.getMessage()}</p>
                                <p className="text-center fw-bold capacity">{count}</p>
                                <div className="d-flex justify-content-between mt-2 px-2">
                                    <Button variant="dark" size="lg" onClick={onIncrease} disabled={count>=capacity}>
                                        +
                                    </Button>{'    '}
                                    <EditModal
                                        show={this.state.showEditModal} 
                                        cancel={this.toggleEditModal}
                                        name={name}
                                        city={city}
                                        genre={genre}
                                        capacity={capacity}
                                        callback={this.props.callback}
                                        clubKey={clubKey}
                                        yellow={yellow}
                                    />
                                    <Button variant="secondary" size="lg" onClick={this.toggleEditModal}>Edit</Button>
                                    <Button variant="secondary" size="lg" onClick={onDecrease} disabled={count<=0}>
                                        -
                                    </Button>{'    '}
                                </div>
                            </CardBody>
                        </Card>
                    </Col>
                </Row>
            </Container>
        );
    }
}