import React, { useState, useEffect, useContext,Component } from 'react'
import ReactDOM from 'react-dom';
import { Link } from 'react-router-dom'
import { withRouter } from "react-router";
import Modal from 'react-modal';
import OverlayMixin from 'react-overlays';
import axios from 'axios'
import https from 'http';
import {
  CButton,
  CCard,
  CCardBody,
  CCardGroup,
  CCol,
  CContainer,
  CForm,
  CInput,
  CInputGroup,
  CInputGroupPrepend,
  CInputGroupText,
  CModal,
  CModalBody,
  CModalFooter,
  CModalHeader,
  CModalTitle,
  CPopover,
  CAlert,
  CProgress,
  CSelect,
  CLabel,
  CRow
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import { AppContext,store} from "./AppContext"
import { makeStyles } from "@material-ui/core/styles";
import GridItem from "../components/Grid/GridItem.js";
import GridContainer from "../components/Grid/GridContainer.js";
import CustomInput from "../components/CustomInput/CustomInput.js";
import Button from "../components/CustomButtons/Button.js";
import Card from "../components/Card/Card.js";
import CardHeader from "../components/Card/CardHeader.js";
import CardAvatar from "../components/Card/CardAvatar.js";
import CardBody from "../components/Card/CardBody.js";
import CardFooter from "../components/Card/CardFooter.js";

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

function inputFieldComp(state) {
      const element = (
        <CustomInput
                 labelText="Register New Organization"
                 id="sourceOrgId"
                 formControlProps={{
                   fullWidth: true
                 }}
                 inputProps={{
                     onChange:(event) => {
                         const target = event.target;
                         const value = target.value;
                         const name = target.name;
                         state.sourceOrgId = value;
                     }
                 }}
               />
      );
      ReactDOM.unmountComponentAtNode(document.getElementById('orgInput'));
      ReactDOM.render(element,document.getElementById('orgInput'));
}

function dropDownComp(state) {
      return (
        <div id="orgInput">
            <CSelect custom name="sourceOrgId" onChange={(event)=>{
                if (event.target.value === "custom") {
                    inputFieldComp(state);
                }
                else
                {
                   const target = event.target;
                   const value = target.value;
                   const name = target.name;
                   state.sourceOrgId = value;
                }
            }}>
              <option value="0">--Select Organization--</option>
              <option value="Irenes">Irenes</option>
              <option value="TraderJoe's">Trader Joe's</option>
              <option value="ChiliParlor">Chili Parlor</option>
              <option value="custom">Register New Organization</option>
            </CSelect>
        </div>
      );
    }

function RenderLogin({state,props})
{
    const classes = useStyles();
    return (
          <>
          <br/><br/><br/><br/><br/>
          <div id="system_error"/>
          <div>
             <GridContainer>
               <GridItem xs={12} sm={12} md={8}>
                 <Card>
                   <CardHeader color="primary">
                     <h4 className={classes.cardTitleWhite}>Login</h4>
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
                       <div id="profile_not_found"/>
                       </GridItem>
                     </GridContainer>
                     <GridContainer>
                       <GridItem xs={12} sm={12} md={6}>
                          <CustomInput
                           labelText="Password"
                           id="password"
                           formControlProps={{
                             fullWidth: true
                           }}
                           inputProps={{
                                onChange:(event) => {
                                    const target = event.target;
                                    const value = target.value;
                                    const name = target.name;
                                    state.password = value;
                                }
                            }}
                         />
                         <div id="password_mismatch"/>
                       </GridItem>
                     </GridContainer>
                   </CardBody>
                   <CardFooter>
                       <GridContainer>
                          <GridItem xs={12} sm={12} md={6}>
                             <Button color="primary" onClick={(e) => {
                                ReactDOM.unmountComponentAtNode(document.getElementById('system_error'));
                                ReactDOM.unmountComponentAtNode(document.getElementById('profile_not_found'));
                                ReactDOM.unmountComponentAtNode(document.getElementById('password_mismatch'));

                                const login = state.email;
                                const payload = {
                                    "email":state.email,
                                    "password":state.password
                                };
                                const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/login/";
                                axios.post(apiUrl,payload).then((response) => {
                                      store.setState(state => ({
                                        ...state,
                                        auth: true,
                                        email:login,
                                        sourceOrg: response.data.sourceOrg
                                      }));


                                      LaunchHome(props,response.data.sourceOrg.producer);

                                }).catch(err => {
                                       console.log("ERROR(LOGIN): "+JSON.stringify(err));
                                       if(err.response != null && err.response.status == 401)
                                       {
                                            if(err.response.data.message == "profile_not_found")
                                            {
                                                const profile_not_found = (
                                                                      <CAlert
                                                                      color="warning"
                                                                      >
                                                                         The email is not registered.
                                                                     </CAlert>
                                                                  );
                                                ReactDOM.render(profile_not_found,document.getElementById('profile_not_found'));
                                            }
                                            else if(err.response.data.message == "password_mismatch")
                                            {
                                                const password_mismatch = (
                                                                                          <CAlert
                                                                                          color="warning"
                                                                                          >
                                                                                             Password does not match.
                                                                                         </CAlert>
                                                                                      );
                                                                    ReactDOM.render(password_mismatch,document.getElementById('password_mismatch'));
                                            }
                                       }
                                       else
                                       {
                                           const system_error = (
                                                                                                         <CAlert
                                                                                                         color="warning"
                                                                                                         >
                                                                                                            Unknown Error. Please check your Network Connection
                                                                                                        </CAlert>
                                                                                                     );
                                                                                   ReactDOM.render(system_error,document.getElementById('system_error'));
                                       }
                                });
                             }}>Login</Button>
                          </GridItem>
                          <GridItem xs={12} sm={12} md={6}>
                           <Button color="primary" onClick={(e) => {
                                const element = (
                                    <>
                                              <br/><br/><br/><br/><br/>
                                              <div id="errorAlert" />
                                              <div>
                                                 <GridContainer>
                                                   <GridItem xs={12} sm={12} md={8}>
                                                     <Card>
                                                       <CardHeader color="primary">
                                                         <h4 className={classes.cardTitleWhite}>Register</h4>
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
                                                           <div id="emailRequired"/>
                                                           <div id="emailInvalid"/>
                                                           </GridItem>
                                                         </GridContainer>
                                                         <GridContainer>
                                                           <GridItem xs={12} sm={12} md={6}>
                                                              <CustomInput
                                                               labelText="Password"
                                                               id="password"
                                                               formControlProps={{
                                                                 fullWidth: true
                                                               }}
                                                               inputProps={{
                                                                                               onChange:(event) => {
                                                                                                   const target = event.target;
                                                                                                   const value = target.value;
                                                                                                   const name = target.name;
                                                                                                   state.password = value;

                                                                                               }
                                                                                           }}
                                                             />
                                                             <div id="passwordRequired"/>
                                                             <div id="password_mismatch"/>
                                                           </GridItem>
                                                         </GridContainer>
                                                         <GridContainer>
                                                            <GridItem xs={12} sm={12} md={6}>
                                                               <br/>
                                                               <CSelect custom name="producer" onChange={(event)=>{
                                                                    const target = event.target;
                                                                    const value = target.value;
                                                                    const name = target.name;
                                                                    state.producer = value;

                                                               }}>
                                                                <option value="0">-- Select Organization Type--</option>
                                                                <option value={true}>Pickup</option>
                                                                <option value={false}>DropOff</option>
                                                              </CSelect>
                                                            </GridItem>
                                                         </GridContainer>
                                                         <GridContainer>
                                                             <GridItem xs={12} sm={12} md={6}>
                                                                <div>
                                                                      <br/>
                                                                      {state.activeElementType === "dropdown"
                                                                        ? dropDownComp(state)
                                                                        : inputFieldComp(state)}
                                                               </div>
                                                             </GridItem>
                                                             <div id="organizationRequired"/>
                                                        </GridContainer>
                                                       </CardBody>
                                                       <CardFooter>
                                                           <GridContainer>
                                                              <GridItem xs={12} sm={12} md={6}>
                                                               <Button color="primary" onClick={(e) => {
                                                                    ReactDOM.unmountComponentAtNode(document.getElementById('emailRequired'));
                                                                    ReactDOM.unmountComponentAtNode(document.getElementById('passwordRequired'));
                                                                    ReactDOM.unmountComponentAtNode(document.getElementById('organizationRequired'));
                                                                    ReactDOM.unmountComponentAtNode(document.getElementById('emailInvalid'));
                                                                    ReactDOM.unmountComponentAtNode(document.getElementById('errorAlert'));
                                                                    const required = (
                                                                                 <CAlert
                                                                                 color="warning"
                                                                                 >
                                                                                    Required
                                                                                </CAlert>
                                                                             );
                                                                let validationSuccess = true;
                                                                if(state.email == null || state.email == "")
                                                                {
                                                                  ReactDOM.render(required,document.getElementById('emailRequired'));
                                                                  validationSuccess = false;
                                                                }
                                                                if(state.password == null || state.password == "")
                                                                {
                                                                    ReactDOM.render(required,document.getElementById('passwordRequired'));
                                                                    validationSuccess = false;
                                                                }
                                                                if(state.sourceOrgId == null || state.sourceOrgId == "")
                                                                {
                                                                  ReactDOM.render(required,document.getElementById('organizationRequired'));
                                                                  validationSuccess = false;
                                                                }

                                                                if(!validationSuccess)
                                                                {
                                                                    return;
                                                                }

                                                                const agent = new https.Agent({
                                                                              rejectUnauthorized: false
                                                                            });

                                                                const login = state.email;
                                                                const payload = {httpsAgent: agent,
                                                                "email":state.email,
                                                                "mobile":"123",
                                                                "password":state.password,
                                                                "orgId":state.sourceOrgId,
                                                                "orgName":state.sourceOrgId,
                                                                "orgType":state.orgType,
                                                                "profileType":state.profileType,
                                                                "orgContactEmail":state.email,
                                                                "street":state.street,
                                                                "zip":state.zip,
                                                                "producer":state.producer};

                                                                const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/org/";
                                                                axios.post(apiUrl,payload).then((response) => {
                                                                    console.log(JSON.stringify(response.data));
                                                                    store.setState(state => ({
                                                                             ...state,
                                                                             auth: true,
                                                                             email:login,
                                                                             sourceOrg: response.data
                                                                           }));

                                                                   LaunchHome(props,response.data.producer);
                                                                }).catch(err => {
                                                                    if(err.response != null && err.response.status == 409)
                                                                    {
                                                                         const element = (
                                                                            <CAlert
                                                                            color="dark"
                                                                            closeButton
                                                                            >
                                                                             "This email is already registered"
                                                                           </CAlert>
                                                                         );
                                                                         ReactDOM.render(element,document.getElementById('errorAlert'));
                                                                    }
                                                                    else if(err.response != null && err.response.status == 400)
                                                                    {
                                                                         console.log("ERROR(REG): "+JSON.stringify(err.response.data));

                                                                        const violations = err.response.data.violations;
                                                                        if(violations.includes("email_invalid"))
                                                                        {
                                                                        const emailInvalid = (
                                                                                               <CAlert
                                                                                               color="warning"
                                                                                               >
                                                                                                  Email is not valid
                                                                                              </CAlert>
                                                                                           );
                                                                        ReactDOM.render(emailInvalid,document.getElementById('emailInvalid'));
                                                                        }

                                                                    }
                                                                    else
                                                                    {
                                                                         const element = (
                                                                            <CAlert
                                                                            color="dark"
                                                                            closeButton
                                                                            >
                                                                               "Unknown Error. Please check your Network Connection
                                                                           </CAlert>
                                                                        );
                                                                        ReactDOM.render(element,document.getElementById('errorAlert'));
                                                                    }
                                                              });
                                                               }}>Register</Button>
                                                            </GridItem>
                                                            </GridContainer>
                                                       </CardFooter>
                                                     </Card>
                                                   </GridItem>
                                                 </GridContainer>
                                           </div>

                                              </>
                                );
                                ReactDOM.unmountComponentAtNode(document.getElementById('parent'));
                                ReactDOM.render(element,document.getElementById('parent'));
                           }}>Register</Button>
                        </GridItem>
                        </GridContainer>
                   </CardFooter>
                 </Card>
               </GridItem>
             </GridContainer>
       </div>
       </>
    );
}

function LaunchHome(props,producer)
{
    const orgId = store.getState().sourceOrg.orgId;
    var apiUrl;
    if(producer)
    {
        apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/recovery/?orgId="+orgId;
    }
    else
    {
        apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/dropoff/?orgId="+orgId;
    }
    axios.get(apiUrl).then((response) => {
        //console.log(JSON.stringify(props));
        if(producer)
        {
               props.history.push({
                 pathname: "/home",
                 state: { data: response.data }
               });
        }
        else
        {
                props.history.push({
                  pathname: "/dropOffHome",
                  state: { data: response.data }
                });
        }
    });
}

class LoginForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {username:'',profileType:'ORG',email:'',password:'',mobile:'',sourceOrgId:'', producer:'',isModalOpen:false,
    street:'801 West Fifth Street',zip:'78703',
    activeElementType: "dropdown"};
  }

  render() {
    return (
      <>
        <br/><br/><br/><br/><br/>
        <div id="parent">
            <RenderLogin state={this.state} props={this.props}/>
        </div>
      </>
    );
  }
}

export default withRouter(LoginForm)