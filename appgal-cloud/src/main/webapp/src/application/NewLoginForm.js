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

function inputFieldComp() {
      const element = (
        <CustomInput
                 labelText="Register New Organization"
                 id="sourceOrgId"
                 formControlProps={{
                   fullWidth: true
                 }}
               />
      );
      ReactDOM.unmountComponentAtNode(document.getElementById('orgInput'));
      ReactDOM.render(element,document.getElementById('orgInput'));
}

function dropDownComp() {
      return (
        <div id="orgInput">
            <CSelect custom name="sourceOrgId" onChange={(e)=>{
                inputFieldComp();
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
                       />
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
                         />
                       </GridItem>
                     </GridContainer>
                   </CardBody>
                   <CardFooter>
                       <GridContainer>
                          <GridItem xs={12} sm={12} md={6}>
                             <Button color="primary" onClick={(e) => {

                             }}>Login</Button>
                          </GridItem>
                          <GridItem xs={12} sm={12} md={6}>
                           <Button color="primary" onClick={(e) => {
                                const element = (
                                    <>
                                              <br/><br/><br/><br/><br/>
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
                                                           />
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
                                                             />
                                                           </GridItem>
                                                         </GridContainer>
                                                         <GridContainer>
                                                            <GridItem xs={12} sm={12} md={6}>
                                                               <br/>
                                                               <CSelect custom name="producer" onChange="">
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
                                                                        ? dropDownComp()
                                                                        : inputFieldComp()}
                                                               </div>
                                                             </GridItem>
                                                        </GridContainer>
                                                       </CardBody>
                                                       <CardFooter>
                                                           <GridContainer>
                                                              <GridItem xs={12} sm={12} md={6}>
                                                               <Button color="primary" onClick={(e) => {

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

class LoginForm extends React.Component {
  mixins = [OverlayMixin];
  constructor(props) {
    super(props);
    this.state = {username:'',profileType:'ORG',email:'',password:'',mobile:'',sourceOrgId:'', producer:'',isModalOpen:false,
    street:'801 West Fifth Street',zip:'78703',
    activeElementType: "dropdown"};
    this.handleChange = this.handleChange.bind(this);
    this.handleLogin = this.handleLogin.bind(this);
    this.handleRegistration = this.handleRegistration.bind(this);
  }

  inputFieldComp() {
      return <CInput type="text" placeholder="Organization" autoComplete="sourceOrgId" name="sourceOrgId" onChange={this.handleChange}/>;
    }

  dropDownComp() {
      return (
        <CSelect custom name="sourceOrgId" onChange={this.handleChange}>
          <option value="0">--Select--</option>
          <option value="Irenes">Irenes</option>
          <option value="TraderJoe's">Trader Joe's</option>
          <option value="ChiliParlor">Chili Parlor</option>
          <option value="custom">Register New Organization</option>
        </CSelect>
      );
    }

  handleChange(event) {
    if (event.target.value === "custom") {
          this.setState({ activeElementType: "input" });
    }
    else
    {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        this.setState({
                  [name]: value
        });
    }
  }

  handleLogin(event) {
    ReactDOM.unmountComponentAtNode(document.getElementById('system_error'));
    ReactDOM.unmountComponentAtNode(document.getElementById('profile_not_found'));
    ReactDOM.unmountComponentAtNode(document.getElementById('password_mismatch'));

    const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/login/";
    axios.post(apiUrl,{"email":this.state.username,"password":this.state.password}).then((response) => {
          console.log(response.data);

          store.setState(state => ({
            ...state,
            auth: true,
            email:this.state.username,
            sourceOrg: response.data.sourceOrg
          }));

          if(response.data.sourceOrg.producer)
             {
                 this.props.history.push({
                   pathname: "/home"
                 });
            }
            else
            {
                  this.props.history.push({
                    pathname: "/dropOffHome"
                  });
            }

    }).catch(err => {
           //console.log("ERROR(LOGIN): "+JSON.stringify(err));
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

    event.preventDefault();
  }

  handleRegistration(event)
      {
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
        if(this.state.email == null || this.state.email == "")
        {
          ReactDOM.render(required,document.getElementById('emailRequired'));
          validationSuccess = false;
        }
        if(this.state.password == null || this.state.password == "")
        {
            ReactDOM.render(required,document.getElementById('passwordRequired'));
            validationSuccess = false;
        }
        if(this.state.sourceOrgId == null || this.state.sourceOrgId == "")
        {
          ReactDOM.render(required,document.getElementById('organizationRequired'));
          validationSuccess = false;
        }

        if(validationSuccess)
        {
            const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/org/";
            // At request level
            const agent = new https.Agent({
              rejectUnauthorized: false
            });

            const payload = {httpsAgent: agent,
                                        "email":this.state.email,
                                        "mobile":"123",
                                        "password":this.state.password,
                                        "orgId":this.state.sourceOrgId,
                                        "orgName":this.state.sourceOrgId,
                                        "orgType":this.state.orgType,
                                        "profileType":this.state.profileType,
                                        "orgContactEmail":this.state.email,
                                        "street":this.state.street,
                                        "zip":this.state.zip,
                                        "producer":this.state.producer};
            console.log("PAYLOAD: "+JSON.stringify(payload));

            axios.post(apiUrl,payload).
            then((response) => {
                      console.log("RESPONSE: "+JSON.stringify(response));
                      const loginUrl = window.location.protocol +"//"+window.location.hostname+"/registration/login/";
                      axios.post(loginUrl,{"email":this.state.email,"password":this.state.password}).
                      then((response) => {
                        console.log("RESPONSE: "+JSON.stringify(response));
                        store.setState(state => ({
                                 ...state,
                                 auth: true,
                                 email:this.state.email,
                                 sourceOrg: response.data.sourceOrg
                               }));

                       if(response.data.sourceOrg.producer)
                       {
                           this.props.history.push({
                             pathname: "/home"
                           });
                      }
                      else
                      {
                            this.props.history.push({
                              pathname: "/dropOffHome"
                            });
                      }
               });
            }).catch(err => {
                      if(err.response != null && err.response.status == 401)
                      {
                           this.setState({
                             "errorMessage": "Login Failed. Please check your Username and/or Password"
                           });
                           const element = (
                                                                         <CAlert
                                                                         color="dark"
                                                                         closeButton
                                                                         >
                                                                            {this.state.errorMessage}
                                                                        </CAlert>
                                                                     );

                                               ReactDOM.render(element,document.getElementById('errorAlert'));
                      }
                      else if(err.response != null && err.response.status == 409)
                      {
                           this.setState({
                             "errorMessage": "This email is already registered"
                           });
                           const element = (
                                                                                                  <CAlert
                                                                                                  color="dark"
                                                                                                  closeButton
                                                                                                  >
                                                                                                     {this.state.errorMessage}
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
                           this.setState({
                               "errorMessage": "Unknown Error. Please check your Network Connection"
                           });
                           const element = (
                                                                                                  <CAlert
                                                                                                  color="dark"
                                                                                                  closeButton
                                                                                                  >
                                                                                                     {this.state.errorMessage}
                                                                                                 </CAlert>
                                                                                              );

                                                                        ReactDOM.render(element,document.getElementById('errorAlert'));
                      }
                });

        }
    }

  render() {
    return (
      <>
        <div id="parent">
                    <RenderLogin state={this.state} props={this.props}/>
        </div>
        <div class="page-content bg-white">
          <div class="container">
                      <div class="banner-inner">
                        <div class="row align-items-center">
                          <div class="col-md-6">
                            <div class="banner-content text-white">
                              <h6
                                data-wow-delay="0.5s"
                                data-wow-duration="3s"
                                class="wow fadeInUp sub-title text-primary"
                              >
                                Jen Summary
                              </h6>
                              <h1
                                data-wow-delay="1s"
                                data-wow-duration="3s"
                                class="wow fadeInUp m-b20"
                              >
                                #Jen Network
                              </h1>
                              <p
                                data-wow-delay="1.5s"
                                data-wow-duration="3s"
                                class="wow fadeInUp m-b30"
                              >
                                The #Jen: Network, short for Food Recovery Optimization
                                Network, is an Uber-like Network for FoodRunners, who can
                                volunteer to pick up food from restaurants, tech
                                cafeterias, parties, etc that they are going to discard
                                because it is extra. They can then deliver it to
                                participating organizations such as churches, food
                                pantries, etc so the people who are hungry can get a
                                deserving hearty meal.
                              </p>
                            </div>
                          </div>
                          <div class="col-md-6">
                            <div
                              class="dz-media wow fadeIn"
                              data-wow-delay="1s"
                              data-wow-duration="3s"
                            >
                              <img
                                src="images/main-slider/slider3/pic1.png"
                                class="move-1"
                                alt=""
                              />
                            </div>
                          </div>
                        </div>
                  </div>
             </div>
          </div>
      </>
    );
  }
}

export default withRouter(LoginForm)