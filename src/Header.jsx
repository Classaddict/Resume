import {Component} from "react";

export default class header extends Component{
    render(){
        return(
            <>
                <h1 style={{fontFamily: "Verdana", fontSize: "36px", textAlign: "center"}} >
                    Nightclub Capacity Tracker 
                </h1>
                <h2 style={{fontFamily: "Impact", fontSize:"24px",textAlign: "center"}}>
                    Click on the club you wish to select:
                </h2>
            </>
        );
    }
}