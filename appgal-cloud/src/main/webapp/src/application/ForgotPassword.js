import React, { useState, useEffect, useContext,Component } from 'react'
import ReactDOM from 'react-dom';
import { withRouter } from "react-router";
import axios from 'axios'

import {
  CAlert
} from '@coreui/react'


// @material-ui/core components
import { makeStyles } from "@material-ui/core/styles";
import InputLabel from "@material-ui/core/InputLabel";
// core components
import GridItem from "../components/Grid/GridItem.js";
import GridContainer from "../components/Grid/GridContainer.js";
import CustomInput from "../components/CustomInput/CustomInput.js";
import Button from "../components/CustomButtons/Button.js";
import Card from "../components/Card/Card.js";
import CardHeader from "../components/Card/CardHeader.js";
import CardAvatar from "../components/Card/CardAvatar.js";
import CardBody from "../components/Card/CardBody.js";
import CardFooter from "../components/Card/CardFooter.js";

import { AppContext,store} from "./AppContext"

const styles = {
  cardCategoryWhite: {
    color: "rgba(255,255,255,.62)",
    margin: "0",
    fontSize: "14px",
    marginTop: "0",
    marginBottom: "0"
  },
  cardTitleWhite: {
    color: "#FFFFFF",
    marginTop: "0px",
    minHeight: "auto",
    fontWeight: "300",
    fontFamily: "'Roboto', 'Helvetica', 'Arial', sans-serif",
    marginBottom: "3px",
    textDecoration: "none"
  }
};

const useStyles = makeStyles(styles);

function ForgotPasswordView({state, props}) {
    const classes = useStyles();
    return (
        <>
        <br/><br/><br/>
        <div id="validation_error"/>
        <div>
          <GridContainer>
            <GridItem xs={12} sm={12} md={8}>
              <Card>
                <CardHeader color="primary">
                  <h4 className={classes.cardTitleWhite}>Forgot Password</h4>
                </CardHeader>
                <CardBody>
                  <GridContainer>
                    <GridItem xs={12} sm={12} md={4}>
                      <CustomInput
                        labelText="Email address"
                        id="email"
                        formControlProps={{
                          fullWidth: true
                        }}
                        inputProps={{
                             onChange:(event) => {
                                 const target = event.target;
                                 const value = target.value;
                                 const name = target.name;
                                 state.email = value;
                             }
                         }}
                      />
                    </GridItem>
                  </GridContainer>
                  <GridContainer>
                      <GridItem xs={12} sm={12} md={4}>
                        <CustomInput
                          labelText="Mobile Number"
                          id="mobileNumber"
                          formControlProps={{
                            fullWidth: true
                          }}
                          inputProps={{
                               onChange:(event) => {
                                   const target = event.target;
                                   const value = target.value;
                                   const name = target.name;
                                   state.mobileNumber = value;
                               }
                           }}
                        />
                      </GridItem>
                    </GridContainer>
                </CardBody>
                <CardFooter>
                  <Button color="primary" onClick={(e) => {
                        ReactDOM.unmountComponentAtNode(document.getElementById('validation_error'));
                        const payload = {
                           email:state.email,
                           mobileNumber:state.mobileNumber
                        };
                        var apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/sendResetCode/";
                        axios.post(apiUrl,payload).then((response) => {
                            const propagation = {
                              email:state.email
                            };
                            props.history.push({
                                pathname: "/confirmResetCode",
                                state: { data: propagation }
                            });
                        }).catch(err => {
                           if(err.response != null && err.response.status == 404)
                           {
                                  const error = (
                                                       <CAlert
                                                       color="warning"
                                                       >
                                                          Provided Email Not Found
                                                      </CAlert>
                                                   );
                                 ReactDOM.render(error,document.getElementById('validation_error'));
                           }
                           else if(err.response != null && err.response.status == 403)
                           {
                                 const error = (
                                                      <CAlert
                                                      color="warning"
                                                      >
                                                         403: Access Denied.
                                                     </CAlert>
                                                  );
                                ReactDOM.render(error,document.getElementById('validation_error'));
                           }
                           else if(err.response != null && err.response.status == 500)
                              {
                                    const error = (
                                                         <CAlert
                                                         color="warning"
                                                         >
                                                            500: Unknown System Error
                                                        </CAlert>
                                                     );
                                   ReactDOM.render(error,document.getElementById('validation_error'));
                              }
                        });
                    }}>Send Reset Code</Button>
                </CardFooter>
              </Card>
            </GridItem>
          </GridContainer>
        </div>
        </>
    );
}

class ForgotPassword extends React.Component {
    constructor(props) {
            super(props);
            this.state = {data: null};
        }

        render() {
           return (
                <>
                    <div id="parent">
                      <ForgotPasswordView state={this.state} props={this.props} />
                    </div>
                </>
            );
        }
}

export default withRouter(ForgotPassword)