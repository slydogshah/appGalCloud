import React, { useEffect, useState, createRef, lazy } from 'react'
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
  CRow
} from '@coreui/react'
import CIcon from '@coreui/icons-react'

const WaitOnData = ({state},{handleRegistration}) => {
          return(
            <div className="c-app c-default-layout flex-row align-items-center">
                        <CContainer>
                          <CRow className="justify-content-center">
                            <CCol md="8">
                              <CCardGroup>
                                <CCard className="p-4">
                                      <CCardBody>
                                        <CForm>
                                          <h1>Profile</h1>
                                          <div id="system_error"/>
                                          <CInputGroup className="mb-3">
                                            <CInputGroupPrepend>
                                              <CInputGroupText>
                                                <CIcon name="cil-user" />
                                              </CInputGroupText>
                                            </CInputGroupPrepend>
                                            <CInput type="text" placeholder="Username" autoComplete="username"
                                            name="email"/>
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
                                            name="password"/>
                                            <div id="passwordRequired"/>
                                            <div id="password_mismatch"/>
                                          </CInputGroup>
                                          <CInputGroup className="mb-5">
                                              <CInputGroupPrepend>
                                                                                  <CInputGroupText>
                                                                                    <CIcon name="cil-lock-locked" />
                                                                                  </CInputGroupText>
                                                                                </CInputGroupPrepend>
                                              <CInput type="text" placeholder="Mobile" autoComplete="mobile" name="mobile"/>
                                              <div id="mobileRequired"/>
                                              <div id="phoneInvalid"/>
                                          </CInputGroup>
                                          <CInputGroup className="mb-6">
                                              <CInputGroupPrepend>
                                                                                  <CInputGroupText>
                                                                                    <CIcon name="cil-lock-locked" />
                                                                                  </CInputGroupText>
                                                                                </CInputGroupPrepend>
                                              <CInput type="text" placeholder="Organization" autoComplete="organization" name="sourceOrgId"/>
                                              <div id="organizationRequired"/>
                                          </CInputGroup>
                                          <br/><br/>
                                          <CButton color="success" block onClick={handleRegistration}>Save</CButton>
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

class Profile extends React.Component {
  mixins = [OverlayMixin];
  constructor(props) {
    super(props);
    this.state = {data: null};
    this.handleChange = this.handleChange.bind(this);
    this.handleRegistration = this.handleRegistration.bind(this);

    this.renderMyData();
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
            axios.post(apiUrl,{httpsAgent: agent,"email":this.state.email,"password":this.state.password,"mobile":this.state.mobile,"sourceOrgId":this.state.sourceOrgId,"profileType":this.state.profileType}).
            then((response) => {
                      const loginUrl = window.location.protocol +"//"+window.location.hostname+"/registration/login/";
                      axios.post(loginUrl,{"email":this.state.email,"password":this.state.password}).
                      then((response) => {
                          console.log("**************************");
                          console.log(JSON.stringify(response.data));
                          /*this.props.history.push({
                              pathname: "/dropOffHome",
                              state: response.data
                          });*/
                          this.props.history.push({
                              pathname: "/home",
                              state: response.data
                          });
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

    renderMyData(){
        const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/profile/?email=jen@appgallabs.io";
        axios.get(apiUrl).then((response) => {
            this.setState({data: response.data});
            console.log("RESPONSE: "+JSON.stringify(response.data));
        });
    }

  render() {
    return (
            <div>
                <WaitOnData state={this.state} handleRegistration={this.handleRegistration}/>
            </div>
    );
  }
}

export default withRouter(Profile)