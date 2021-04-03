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
  CRow
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import { AppContext,store} from "./AppContext"


class LoginForm extends React.Component {
  mixins = [OverlayMixin];
  constructor(props) {
    super(props);
    this.state = {username:'',profileType:'ORG',email:'',password:'',mobile:'',sourceOrgId:'', producer:'',isModalOpen:false,
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
        ReactDOM.unmountComponentAtNode(document.getElementById('mobileRequired'));
        ReactDOM.unmountComponentAtNode(document.getElementById('organizationRequired'));
        ReactDOM.unmountComponentAtNode(document.getElementById('emailInvalid'));
        ReactDOM.unmountComponentAtNode(document.getElementById('phoneInvalid'));
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
        if(this.state.mobile == null || this.state.mobile == "")
        {
          ReactDOM.render(required,document.getElementById('mobileRequired'));
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
                                        "mobile":this.state.mobile,
                                        "password":this.state.password,
                                        "orgId":this.state.sourceOrgId,
                                        "orgName":this.state.sourceOrgId,
                                        "orgType":this.state.orgType,
                                        "profileType":this.state.profileType,
                                        "orgContactEmail":this.state.email,
                                        "profileType":this.state.profileType,
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
                          if(violations.includes("phone_invalid"))
                          {
                              const phoneInvalid = (
                                                                             <CAlert
                                                                             color="warning"
                                                                             >
                                                                                Phone is not valid
                                                                            </CAlert>
                                                                         );
                              ReactDOM.render(phoneInvalid,document.getElementById('phoneInvalid'));
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
      <div className="c-app c-default-layout flex-row align-items-center">
            <CContainer>
              <CRow className="justify-content-center">
                <CCol md="8">
                  <CCardGroup>
                    <CCard className="p-4">
                      <CCardBody>
                        <CForm>
                          <h1>Login</h1>
                          <p className="text-muted">Sign In to your account</p>
                          <div id="system_error"/>
                          <CInputGroup className="mb-3">
                            <CInputGroupPrepend>
                              <CInputGroupText>
                                <CIcon name="cil-user" />
                              </CInputGroupText>
                            </CInputGroupPrepend>
                            <CInput type="text" placeholder="Username" autoComplete="username"
                            name="username" onChange={this.handleChange}/>
                            <div id="profile_not_found"/>
                          </CInputGroup>
                          <CInputGroup className="mb-4">
                            <CInputGroupPrepend>
                              <CInputGroupText>
                                <CIcon name="cil-lock-locked" />
                              </CInputGroupText>
                            </CInputGroupPrepend>
                            <CInput type="password" placeholder="Password" autoComplete="current-password"
                            name="password" onChange={this.handleChange}/>
                            <div id="password_mismatch"/>
                          </CInputGroup>
                          <CRow>
                            <CCol xs="6">
                                <CButton color="primary" className="px-4" onClick={this.handleLogin}>Login</CButton>
                                <div id="loginErrorAlert"></div>
                            </CCol>
                            <CCol xs="6" className="text-right">
                              <CButton color="link" className="px-0">Forgot password?</CButton>
                            </CCol>
                          </CRow>
                        </CForm>
                      </CCardBody>
                    </CCard>
                    <CCard className="p-4">
                          <CCardBody>
                            <CForm>
                              <h1>Register</h1>
                              <p className="text-muted">Create your account</p>
                              <div id="system_error"/>
                              <CInputGroup className="mb-3">
                                <CInputGroupPrepend>
                                  <CInputGroupText>
                                    <CIcon name="cil-user" />
                                  </CInputGroupText>
                                </CInputGroupPrepend>
                                <CInput type="text" placeholder="Username" autoComplete="username"
                                name="email" onChange={this.handleChange}/>
                                <div id="emailRequired"/>
                                <div id="emailInvalid"/>
                              </CInputGroup>
                              <CInputGroup className="mb-4">
                                <CInputGroupPrepend>
                                  <CInputGroupText>
                                    <CIcon name="cil-lock-locked" />
                                  </CInputGroupText>
                                </CInputGroupPrepend>
                                <CInput type="password" placeholder="Password" autoComplete="current-password"
                                name="password" onChange={this.handleChange}/>
                                <div id="passwordRequired"/>
                                <div id="password_mismatch"/>
                              </CInputGroup>
                              <CInputGroup className="mb-5">
                                  <CInputGroupPrepend>
                                      <CInputGroupText>
                                        <CIcon name="cil-lock-locked" />
                                      </CInputGroupText>
                                  </CInputGroupPrepend>
                                  <CInput type="text" placeholder="Mobile" autoComplete="mobile" name="mobile" onChange={this.handleChange}/>
                                  <div id="mobileRequired"/>
                                  <div id="phoneInvalid"/>
                              </CInputGroup>
                              <CInputGroup className="mb-5">
                                <CInputGroupPrepend>
                                    <CInputGroupText>
                                      <CIcon name="cil-lock-locked" />
                                    </CInputGroupText>
                                </CInputGroupPrepend>
                                <CSelect custom name="producer" onChange={this.handleChange}>
                                 <option value="0">--Select--</option>
                                 <option value={true}>Pickup</option>
                                 <option value={false}>DropOff</option>
                               </CSelect>
                              </CInputGroup>
                              <CInputGroup className="mb-6">
                                  <CInputGroupPrepend>
                                      <CInputGroupText>
                                        <CIcon name="cil-lock-locked" />
                                      </CInputGroupText>
                                  </CInputGroupPrepend>
                                  <div>
                                          {this.state.activeElementType === "dropdown"
                                            ? this.dropDownComp()
                                            : this.inputFieldComp()}
                                   </div>
                                  <div id="organizationRequired"/>
                              </CInputGroup>
                              <br/><br/>
                              <CButton color="success" block onClick={this.handleRegistration}>Create Account</CButton>
                              <div id="errorAlert" />
                            </CForm>
                          </CCardBody>
                        </CCard>
                  </CCardGroup>
                </CCol>
              </CRow>
            </CContainer>
          </div>
    );
  }
}

export default withRouter(LoginForm)