import {Component} from "react";
import ClubCard from "./ClubCard";
import { Row, Col, Container, Button } from "react-bootstrap";


export default class ClubContainer extends Component{
    /**
        Parent class, used to control all of the boxes that contain each ind. nightclub
     */
    constructor(props) {
    super(props);
    this.state = { 
        data: null,
        allData: null  
    };
}
    updateData=(apiResponse)=>{
        this.setState({data:apiResponse});
    }

   fetchData = () => {
        const { filterCity } = this.props;
        const url = filterCity
            ? `http://localhost:5001/clubs?city=${filterCity}`
            : 'http://localhost:5001/clubs';

       
        fetch('http://localhost:5001/clubs')
            .then(response => response.json())
            .then(allClubs => {
                this.setState({ allData: allClubs });
                if (this.props.onAllDataLoad) {
                    this.props.onAllDataLoad(allClubs); 
                }
            })
            .catch(error => console.log(error));

        fetch(url)
            .then(response => response.json())
            .then(clubs => this.setState({ data: clubs }))
            .catch(error => console.log(error));
    }

    componentDidMount(){
        this.fetchData();
    }


    componentDidUpdate(prevProps) {
        if (prevProps.filterCity !== this.props.filterCity) {
            this.fetchData();  
        }
    }
    render(){
        const{counts,onIncrease,onDecrease, clubs}=this.props;
        if(this.state.data==null || this.state.data==("")){
            return(
                <div><p>No data returned from server</p></div>
            )
        }else{
            return(
                <><Container fluid>
                    <Row>
                        {this.state.data.map((club) => (
                            <Col key={club.id} xs={12} sm={6} md={3}>
                                <ClubCard
                                    clubKey={club.id}
                                    name={club.name}
                                    count={club.count}       
                                    yellow={club.yellow}
                                    capacity={club.capacity}
                                    onIncrease={() => onIncrease(club.name)}
                                    onDecrease={() => onDecrease(club.name)}
                                    genre={club.genre}
                                    callback={this.props.callback}
                                    city={club.city}
                                />
                            </Col>
                        ))}
                    </Row>
                </Container>
                </>
            );
        }
    }
}