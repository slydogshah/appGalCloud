import React, { useState, useEffect, useContext,Component } from 'react'
import ReactDOM from 'react-dom';
import { withRouter } from "react-router";
import { axios} from "../App"

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
import DonutLargeOutlinedIcon from '@material-ui/icons/DonutLargeOutlined';
import Snackbar from "../components/Snackbar/Snackbar.js";

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

function ResetPasswordView({state, props}) {
    const classes = useStyles();
    const profile = props.history.location.state.data.profile;
    const sourceOrg = props.history.location.state.data.sourceOrg;
    const resetEmail = profile.email;
    const producer = sourceOrg.producer;
    return (
        <>
        <br/><br/><br/>
        <div id="validation_error"/>
        <div>
          <GridContainer>
            <GridItem xs={12} sm={12} md={8}>
              <Card>
                <CardHeader color="primary">
                  <h4 className={classes.cardTitleWhite}>Reset Password</h4>
                </CardHeader>
                <CardBody>
                  <GridContainer>
                    <GridItem xs={12} sm={12} md={4}>
                      <CustomInput
                        labelText="New Password"
                        id="newPassword"
                        formControlProps={{
                          fullWidth: true
                        }}
                        inputProps={{
                           type:"password",
                           onChange:(event) => {
                               const target = event.target;
                               const value = target.value;
                               const name = target.name;
                               state.newPassword = value;
                           }
                       }}
                      />
                      <div id="newPasswordRequired"/>
                    </GridItem>
                  </GridContainer>
                  <GridContainer>
                  <GridItem xs={12} sm={12} md={4}>
                    <CustomInput
                      labelText="Confirm New Password"
                      id="confirmNewPassword"
                      formControlProps={{
                        fullWidth: true
                      }}
                      inputProps={{
                         type:"password",
                         onChange:(event) => {
                             const target = event.target;
                             const value = target.value;
                             const name = target.name;
                             state.confirmNewPassword = value;
                         }
                     }}
                    />
                    <div id="confirmNewPasswordRequired"/>
                  </GridItem>
                </GridContainer>
                </CardBody>
                <CardFooter>
                  <Button color="primary" onClick={(e) => {
                  ReactDOM.unmountComponentAtNode(document.getElementById('newPasswordRequired'));
                                                                    ReactDOM.unmountComponentAtNode(document.getElementById('confirmNewPasswordRequired'));
                                                                    const required = (
                                                                         <CAlert
                                                                         color="warning"
                                                                         >
                                                                            Required
                                                                        </CAlert>
                                                                     );
                                                                     let validationSuccess = true;



                                                                    if(state.newPassword == null || state.newPassword == "")
                                                                    {
                                                                      ReactDOM.render(required,document.getElementById('newPasswordRequired'));
                                                                      validationSuccess = false;
                                                                    }
                                                                    if(state.confirmNewPassword == null || state.confirmNewPassword == "")
                                                                      {
                                                                        ReactDOM.render(required,document.getElementById('confirmNewPasswordRequired'));
                                                                        validationSuccess = false;
                                                                      }

                                                                      if(!validationSuccess)
                                                                      {
                                                                          return;
                                                                      }

                          const orgId = store.getState().sourceOrg.orgId;
                          ReactDOM.unmountComponentAtNode(document.getElementById('validation_error'));
                          const payload = {
                             newPassword:state.newPassword,
                             confirmNewPassword:state.confirmNewPassword,
                             email:resetEmail
                          };
                          //show progress bar
                          var element = (
                                  <Snackbar
                                    place="tc"
                                    color="info"
                                    icon={DonutLargeOutlinedIcon}
                                    message="Loading...."
                                    open={true}
                                  />
                          );
                          ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                          ReactDOM.render(element,document.getElementById('progress'));
                          var apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/resetPassword/";
                          axios.post(apiUrl,payload).then((response) => {
                            ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                            var apiUrl;
                            var land;

                            if(producer)
                            {
                                apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/recovery/?orgId="+orgId;
                                land = "/home";
                            }
                            else
                            {
                                apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/dropoff/?orgId="+orgId;
                                land = "/dropOffHome";
                            }

                            //show progress bar
                            var element = (
                                    <Snackbar
                                      place="tc"
                                      color="info"
                                      icon={DonutLargeOutlinedIcon}
                                      message="Authentication In Progress...."
                                      open={true}
                                    />
                            );
                            ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                            ReactDOM.render(element,document.getElementById('progress'));
                            store.setState(state => ({
                                                                            ...state,
                                                                            auth: true,

                                                                          }));
                            axios.get(apiUrl).then((response) => {
                                ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                                const responseData = response.data;
                                props.history.push({
                                 pathname: land,
                                 state: { data: response.data }
                               });
                            }).catch(err => {
                                ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                                var element = (
                                            <Snackbar
                                              place="tc"
                                              color="danger"
                                              icon={DonutLargeOutlinedIcon}
                                              message="500: Unknown System Error...."
                                              open={true}
                                              close
                                              closeNotification={() => {
                                                ReactDOM.unmountComponentAtNode(document.getElementById('unknown_error'));
                                              }}
                                            />
                                    );
                                    ReactDOM.unmountComponentAtNode(document.getElementById('unknown_error'));
                                    ReactDOM.render(element,document.getElementById('unknown_error'));
                            });
                            }).catch(err => {
                             ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                             if(err.response != null && err.response.status == 400)
                             {
                                    const error = (
                                                         <CAlert
                                                         color="warning"
                                                         >
                                                            400: Your Confirm password does not match the New password
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
                                    ReactDOM.render(error,document.getElementById('unknown_error'));
                               }
                             else{
                               const error = (
                                                     <CAlert
                                                     color="warning"
                                                     >
                                                        500: Unknown System Error
                                                    </CAlert>
                                                 );
                               ReactDOM.render(error,document.getElementById('unknown_error'));
                             }
                            });
                    }}>Reset Password</Button>
                </CardFooter>
              </Card>
            </GridItem>
          </GridContainer>
        </div>
        </>
    );
}

class StaffResetPassword extends React.Component {
    constructor(props) {
            super(props);
            this.state = {data: null};
        }

        render() {
           return (
                <>
                    <div id="progress"/>
                    <div id="parent">
                      <ResetPasswordView state={this.state} props={this.props} />
                    </div>
                </>
            );
        }
}

export default withRouter(StaffResetPassword)