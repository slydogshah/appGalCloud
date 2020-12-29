import React from 'react'
import { Link } from 'react-router-dom'
import { withRouter } from "react-router";
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
  CRow
} from '@coreui/react'
import CIcon from '@coreui/icons-react'

class LoginForm extends React.Component {
  constructor(props) {
    super(props);
    //console.log("Constructor: "+JSON.stringify(this.props));
    this.state = {username:'',password:''};
    this.handleChange = this.handleChange.bind(this);
    this.handleLogin = this.handleLogin.bind(this);
  }

  handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    this.setState({
          [name]: [value]
    });
  }

  handleLogin(event) {
    this.props.history.push("/dashboard");
    alert('A name was submitted: ' + this.state.username+":"+this.state.password);
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
                          <CInputGroup className="mb-3">
                            <CInputGroupPrepend>
                              <CInputGroupText>
                                <CIcon name="cil-user" />
                              </CInputGroupText>
                            </CInputGroupPrepend>
                            <CInput type="text" placeholder="Username" autoComplete="username"
                            name="username" onChange={this.handleChange}/>
                          </CInputGroup>
                          <CInputGroup className="mb-4">
                            <CInputGroupPrepend>
                              <CInputGroupText>
                                <CIcon name="cil-lock-locked" />
                              </CInputGroupText>
                            </CInputGroupPrepend>
                            <CInput type="password" placeholder="Password" autoComplete="current-password"
                            name="password" onChange={this.handleChange}/>
                          </CInputGroup>
                          <CRow>
                            <CCol xs="6">
                              <CButton color="primary" className="px-4" onClick={this.handleLogin}>Login</CButton>
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
                          <Link to="/register">
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