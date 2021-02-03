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

    const apiUrl = 'http://localhost:8080/registration/login/';
    axios.post(apiUrl,{"email":this.state.username,"password":this.state.password}).then((response) => {
          this.props.history.push({
            pathname: "/home",
            state: response.data
          });
    }).catch(err => {

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
                    <CCard className="text-white bg-primary py-5 d-md-down-none" style={{ width: '44%' }}>
                      <CCardBody className="text-center">
                        <div>
                          <h2>Sign up</h2>
                          <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut
                            labore et dolore magna aliqua.</p>
                          <Link to="/registration">
                            <CButton color="primary" className="mt-3" active tabIndex={-1}>Register Now!</CButton>
                          </Link>
                        </div>
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