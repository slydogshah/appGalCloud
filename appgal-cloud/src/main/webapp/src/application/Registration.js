import React, { useState } from 'react'
import ReactDOM from 'react-dom';
import axios from 'axios'
import {
  CButton,
  CCard,
  CCardBody,
  CCardFooter,
  CCol,
  CContainer,
  CForm,
  CInput,
  CInputGroup,
  CInputGroupPrepend,
  CInputGroupText,
  CAlert,
  CRow
} from '@coreui/react'
import CIcon from '@coreui/icons-react'

class Register extends React.Component {
  constructor(props) {
      super(props);
      //console.log("Constructor: "+JSON.stringify(this.props));
      this.state = {profileType:'ORG',email:'',password:'',mobile:'',sourceOrgId:'',confirmPassword:''};
      this.handleChange = this.handleChange.bind(this);
      this.handleRegistration = this.handleRegistration.bind(this);
    }

    handleChange(event) {
      const target = event.target;
      const value = target.value;
      const name = target.name;
      this.setState({
            [name]: value
      });
    }

    handleRegistration(event)
    {
      ReactDOM.unmountComponentAtNode(document.getElementById('emailRequired'));
      ReactDOM.unmountComponentAtNode(document.getElementById('passwordRequired'));
      ReactDOM.unmountComponentAtNode(document.getElementById('mobileRequired'));
      ReactDOM.unmountComponentAtNode(document.getElementById('organizationRequired'));
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
          const apiUrl = 'http://localhost:8080/registration/profile/';
              axios.post(apiUrl,{"email":this.state.email,"password":this.state.password,"mobile":this.state.mobile,"sourceOrgId":this.state.sourceOrgId,"profileType":this.state.profileType}).then((response) => {
                    //setTimeout(() => {  this.props.history.push("/dashboard"); }, 10000)
                    this.props.history.push({
                      pathname: "/main",
                      state: response.data
                    });
              }).catch(err => {
                    if(err.response != null && err.response.status == 401)
                    {
                         this.setState({
                           "errorMessage": "Login Failed. Please check your Username and/or Password"
                         });
                    }
                    else
                    {
                         this.setState({
                             "errorMessage": "Unknown Error. Please check your Network Connection"
                         });
                    }
                    const element = (
                          <CAlert
                          color="dark"
                          closeButton
                          >
                             {this.state.errorMessage}
                         </CAlert>
                      );
                    ReactDOM.unmountComponentAtNode(document.getElementById('errorAlert'));
                    ReactDOM.render(element,document.getElementById('errorAlert'));
              });
      }
  }

  render(){
      return (
        <div className="c-app c-default-layout flex-row align-items-center">
          <CContainer>
            <CRow className="justify-content-center">
              <CCol md="9" lg="7" xl="6">
                <CCard className="mx-4">
                  <CCardBody className="p-4">
                    <CForm>
                      <h1>Register</h1>
                      <p className="text-muted">Create your account</p>
                      <CInputGroup className="mb-3">
                        <CInputGroupPrepend>
                          <CInputGroupText>@</CInputGroupText>
                        </CInputGroupPrepend>
                        <CInput type="text" placeholder="Email" autoComplete="email" name="email" onChange={this.handleChange}/>
                        <div id="emailRequired"/>
                      </CInputGroup>
                      <CInputGroup className="mb-3">
                        <CInputGroupPrepend>
                          <CInputGroupText>
                            <CIcon name="cil-lock-locked" />
                          </CInputGroupText>
                        </CInputGroupPrepend>
                        <CInput type="password" placeholder="Password" autoComplete="new-password" name="password" onChange={this.handleChange}/>
                        <div id="passwordRequired"/>
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
                      </CInputGroup>
                      <CInputGroup className="mb-6">
                        <CInputGroupText>
                            <CIcon name="cil-lock-locked" />
                        </CInputGroupText>
                        <CInput type="text" placeholder="Organization" autoComplete="organization" name="sourceOrgId" onChange={this.handleChange}/>
                        <div id="organizationRequired"/>
                      </CInputGroup>
                      <CButton color="success" block onClick={this.handleRegistration}>Create Account</CButton>
                      <div id="errorAlert" />
                    </CForm>
                  </CCardBody>
                </CCard>
              </CCol>
            </CRow>
          </CContainer>
        </div>
      );
  }
}

export default Register
