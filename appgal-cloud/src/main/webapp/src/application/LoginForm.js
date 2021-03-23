import React, { useState } from 'react'
import ReactDOM from 'react-dom';
import { Link } from 'react-router-dom'
import { withRouter } from "react-router";
import Modal from 'react-modal';
import OverlayMixin from 'react-overlays';
import axios from 'axios'
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
  CRow
} from '@coreui/react'
import CIcon from '@coreui/icons-react'

class LoginForm extends React.Component {
  mixins = [OverlayMixin];
  constructor(props) {
    super(props);
    //console.log("Constructor: "+JSON.stringify(this.props));

    this.state = {username:'',password:'',isModalOpen:false};
    this.handleChange = this.handleChange.bind(this);
    this.handleLogin = this.handleLogin.bind(this);
  }

  handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    this.setState({
          [name]: value
    });
  }

  handleLogin(event) {
    console.log(JSON.stringify(this.state));

    ReactDOM.unmountComponentAtNode(document.getElementById('system_error'));
    ReactDOM.unmountComponentAtNode(document.getElementById('profile_not_found'));
    ReactDOM.unmountComponentAtNode(document.getElementById('password_mismatch'));

    const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/login/";
    axios.post(apiUrl,{"email":this.state.username,"password":this.state.password}).then((response) => {
          this.props.history.push({
            pathname: "/home"
          });
          /*this.props.history.push({
                      pathname: "/dropOffHome",
                      state: response.data
                    });*/
    }).catch(err => {
           console.log(JSON.stringify(err));
           if(err.response != null && err.response.status == 401)
           {
                console.log(JSON.stringify(err.response.data));
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
                              <CInputGroup className="mb-4">
                                  <CInputGroupPrepend>
                                    <CInputGroupText>
                                      <CIcon name="cil-lock-locked" />
                                    </CInputGroupText>
                                  </CInputGroupPrepend>
                                  <CInput type="password" placeholder="Confirm password" autoComplete="new-password" name="confirmPassword" onChange={this.handleChange}/>
                                  <div id="confirmPasswordRequired"/>
                                </CInputGroup>
                                <CInputGroup className="mb-5">
                                  <CInputGroupText>
                                      <CIcon name="cil-lock-locked" />
                                  </CInputGroupText>
                                  <CInput type="text" placeholder="Mobile" autoComplete="mobile" name="mobile" onChange={this.handleChange}/>
                                  <div id="mobileRequired"/>
                                  <div id="phoneInvalid"/>
                                </CInputGroup>
                                <CInputGroup className="mb-6">
                                  <CInputGroupText>
                                      <CIcon name="cil-lock-locked" />
                                  </CInputGroupText>
                                  <CInput type="text" placeholder="Organization" autoComplete="organization" name="sourceOrgId" onChange={this.handleChange}/>
                                  <div id="organizationRequired"/>
                              </CInputGroup>
                              <CRow>
                                <CCol xs="6">
                                    <Link to="/registration">
                                        <CButton color="primary" className="mt-3" active tabIndex={-1}>Register Now!</CButton>
                                    </Link>
                                </CCol>
                                <CCol xs="6" className="text-right">
                                </CCol>
                              </CRow>
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