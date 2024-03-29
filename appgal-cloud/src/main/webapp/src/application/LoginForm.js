import React, { useState, useEffect, useContext,Component } from 'react'
import ReactDOM from 'react-dom';
import { Link } from 'react-router-dom'
import { withRouter } from "react-router";
import Modal from 'react-modal';
import OverlayMixin from 'react-overlays';
import Select from 'react-select'
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
  CLink,
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
import Input from "@material-ui/core/Input";
import InputLabel from "@material-ui/core/InputLabel";
import DonutLargeOutlinedIcon from '@material-ui/icons/DonutLargeOutlined';
import Snackbar from "../components/Snackbar/Snackbar.js";
import { axios} from "../App"

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
           <>
           <GridContainer>
            <GridItem xs={12} sm={12} md={4}>
                <CustomInput
                 labelText="New Organization"
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
            </GridItem>
          </GridContainer>
          <GridContainer>
              <GridItem xs={12} sm={12} md={4}>
                  <CustomInput
                   labelText="Street"
                   id="street"
                   formControlProps={{
                     fullWidth: true
                   }}
                   inputProps={{
                       onChange:(event) => {
                           const target = event.target;
                           const value = target.value;
                           const name = target.name;
                           state.street = value;
                       }
                   }}
                 />
                 <div id="streetRequired"/>
              </GridItem>
            </GridContainer>
           <GridContainer>
           <GridItem xs={12} sm={12} md={4}>
               <CustomInput
                labelText="Zip"
                id="zip"
                formControlProps={{
                  fullWidth: true
                }}
                inputProps={{
                    onChange:(event) => {
                        const target = event.target;
                        const value = target.value;
                        const name = target.name;
                        state.zip = value;
                    }
                }}
              />
           </GridItem>
         </GridContainer>
         <GridContainer>
                    <GridItem xs={12} sm={12} md={4}>
                        <CSelect custom name="timeZone" onChange={(event)=>{
                                        const target = event.target;
                                                                                   const value = target.value;
                                                                                   const name = target.name;
                                                                                   state.timeZone = value;
                                    }}>
                                      <option value="0">--TimeZone--</option>
                                      <option value="US/Central">US/Central</option>
                                    </CSelect>
                    </GridItem>
                  </GridContainer>
         </>
      );
      ReactDOM.unmountComponentAtNode(document.getElementById('orgInput'));
      ReactDOM.render(element,document.getElementById('orgInput'));
}

function dropDownComp(state,orgs) {
      let map = new Map()
       for (const [index, value] of orgs.entries()) {
            map.set(value.orgId,value.orgName);
       }
       const options = [];
       for (let [key, value] of map) {
         options.push(<option value={key}>{value}</option>);
       }
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
              <option value="0">--Add Organization Information--</option>
              <option value="custom">--Register New Organization--</option>
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
          <div id="access_denied"/>
          <div>
             <GridContainer>
               <GridItem xs={12} sm={12} md={8}>
                 <Card>
                   <CardHeader color="primary">
                     <h4 className={classes.cardTitleWhite}>Login</h4>
                   </CardHeader>
                   <CardBody>
                     <GridContainer>
                       <GridItem xs={12} sm={12} md={6}>
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
                                type:"password",
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
                                ReactDOM.unmountComponentAtNode(document.getElementById('access_denied'));
                                ReactDOM.unmountComponentAtNode(document.getElementById('profile_not_found'));
                                ReactDOM.unmountComponentAtNode(document.getElementById('password_mismatch'));

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

                                const login = state.email;
                                const payload = {
                                    "email":state.email,
                                    "password":state.password
                                };
                                const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/login/";
                                axios.post(apiUrl,payload).then((response) => {

                                      const resetPasswordActive = response.data.profile.resetPasswordActive;

                                      ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                                      store.setState(state => ({
                                        ...state,
                                        auth: resetPasswordActive,
                                        email:login,
                                        bearer:response.data.bearerToken,
                                        sourceOrg: response.data.sourceOrg
                                      }));
                                      LaunchHome(props,response.data);
                                }).catch(err => {
                                       ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
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
                                       else if(err.response != null && err.response.status == 403)
                                      {
                                           const access_denied = (
                                                                <CAlert
                                                                color="warning"
                                                                >
                                                                   FoodRunners use the IOS or Android App to receive Food Pickup Requests<br/>
                                                                   The Dashboard is used by participating Organizations

                                                               </CAlert>
                                                            );
                                          ReactDOM.render(access_denied,document.getElementById('access_denied'));
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
                                const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/timezones/";
                                axios.get(apiUrl).then((response) => {
                                ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                                const timeZones = response.data.timezones;
                                //alert(JSON.stringify(timeZones));
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
                                                   <GridItem xs={12} sm={12} md={6}>
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
                                                       type:"password",
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
                                                      <div id="orgTypeRequired"/>
                                                    </GridItem>
                                                 </GridContainer>
                                                 <GridContainer>
                                                     <GridItem xs={12} sm={12} md={4}>
                                                                                                                                                                         <CustomInput
                                                                                                                                                                          labelText="New Organization"
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
                                                                                                                                                                        <div id="organizationRequired"/>
                                                                                                                                                                     </GridItem>

                                               </GridContainer>
                                               <GridContainer>

                                                                                                               <GridItem xs={12} sm={12} md={4}>
                                                                                                                                                                                      <CustomInput
                                                                                                                                                                                       labelText="Street"
                                                                                                                                                                                       id="street"
                                                                                                                                                                                       formControlProps={{
                                                                                                                                                                                         fullWidth: true
                                                                                                                                                                                       }}
                                                                                                                                                                                       inputProps={{
                                                                                                                                                                                           onChange:(event) => {
                                                                                                                                                                                               const target = event.target;
                                                                                                                                                                                               const value = target.value;
                                                                                                                                                                                               const name = target.name;
                                                                                                                                                                                               state.street = value;
                                                                                                                                                                                           }
                                                                                                                                                                                       }}
                                                                                                                                                                                     />
                                                                                                                                                                                     <div id="streetRequired"/>
                                                                                                                                                                                  </GridItem>

                                                                                              </GridContainer>
                                               <GridContainer>
                                                                                                    <GridItem xs={12} sm={12} md={4}>
                                                                                                                   <CustomInput
                                                                                                                    labelText="Zip"
                                                                                                                    id="zip"
                                                                                                                    formControlProps={{
                                                                                                                      fullWidth: true
                                                                                                                    }}
                                                                                                                    inputProps={{
                                                                                                                        onChange:(event) => {
                                                                                                                            const target = event.target;
                                                                                                                            const value = target.value;
                                                                                                                            const name = target.name;
                                                                                                                            state.zip = value;
                                                                                                                        }
                                                                                                                    }}
                                                                                                                  />
                                                                                                                  <div id="zipRequired"/>
                                                                                                               </GridItem>
                                                                                              </GridContainer>

                                               </CardBody>
                                               <CardFooter>
                                                   <GridContainer>
                                                      <GridItem xs={12} sm={12} md={6}>
                                                       <Button color="primary" onClick={(e) => {
                                                            ReactDOM.unmountComponentAtNode(document.getElementById('emailRequired'));
                                                            ReactDOM.unmountComponentAtNode(document.getElementById('passwordRequired'));
                                                            ReactDOM.unmountComponentAtNode(document.getElementById('orgTypeRequired'));
                                                            ReactDOM.unmountComponentAtNode(document.getElementById('organizationRequired'));
                                                            ReactDOM.unmountComponentAtNode(document.getElementById('emailInvalid'));
                                                            ReactDOM.unmountComponentAtNode(document.getElementById('streetRequired'));
                                                            ReactDOM.unmountComponentAtNode(document.getElementById('zipRequired'));
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
                                                        if(state.producer == null || state.producer == "")
                                                                                                                {
                                                                                                                  ReactDOM.render(required,document.getElementById('orgTypeRequired'));
                                                                                                                  validationSuccess = false;
                                                                                                                }
                                                        if(state.sourceOrgId == null || state.sourceOrgId == "")
                                                        {
                                                          ReactDOM.render(required,document.getElementById('organizationRequired'));
                                                          validationSuccess = false;
                                                        }

                                                        if(state.street == null || state.street == "")
                                                        {
                                                          ReactDOM.render(required,document.getElementById('streetRequired'));
                                                          validationSuccess = false;
                                                        }

                                                        if(state.zip == null || state.zip == "")
                                                                                                                {
                                                                                                                  ReactDOM.render(required,document.getElementById('zipRequired'));
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
                                                        "orgContactEmail":state.email,
                                                        "profileType":state.profileType,
                                                        "street":state.street,
                                                        "zip":state.zip,
                                                        "producer":state.producer};

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


                                                        const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/org/";
                                                        axios.post(apiUrl,payload).then((response) => {
                                                            ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                                                            store.setState(state => ({
                                                                     ...state,
                                                                     auth: true,
                                                                     email:login,
                                                                     bearer:response.data.bearerToken,
                                                                     sourceOrg: response.data
                                                                   }));

                                                           LaunchHomeAfterRegister(state,props,state.email);
                                                        }).catch(err => {
                                                            ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
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
                                                                else if(violations.includes("org_inconsistent")){
                                                                      const validationError = (
                                                                                               <CAlert
                                                                                               color="warning"
                                                                                               >
                                                                                                  This Organization is already registered as a different type
                                                                                              </CAlert>
                                                                                           );
                                                                        ReactDOM.render(validationError,document.getElementById('validation_error'));
                                                                }

                                                            }
                                                            else
                                                            {
                                                                 const element = (
                                                                    <CAlert
                                                                    color="dark"
                                                                    closeButton
                                                                    >
                                                                       Unknown Error. Please check your Network Connection
                                                                   </CAlert>
                                                                );
                                                                ReactDOM.render(element,document.getElementById('errorAlert'));
                                                            }
                                                      });
                                                       }}>Register</Button>
                                                    </GridItem>

                                                    <GridItem xs={12} sm={12} md={6}>
                                                       <Button color="primary" onClick={(e) => {
                                                              const element = (
                                                                   <RenderLogin state={state} props={props}/>
                                                              );
                                                              ReactDOM.unmountComponentAtNode(document.getElementById('parent'));
                                                              ReactDOM.render(element,document.getElementById('parent'));
                                                       }}>Cancel</Button>
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
                                }).catch(err => {
                                        ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                                        const system_error = (
                                                                                         <CAlert
                                                                                         color="warning"
                                                                                         >
                                                                                            Unknown Error. Please check your Network Connection
                                                                                        </CAlert>
                                                                                    );
                                                                                   ReactDOM.render(system_error,document.getElementById('system_error'));
                                });
                           }}>Register</Button>
                        </GridItem>
                        <GridItem xs={12} sm={12} md={6}>
                            <CLink className={classes.dropdownItem} onClick={(e) => {
                                    props.history.push({
                                        pathname: "/forgotPassword",
                                    });
                            }
                            }>Forgot Password</CLink>
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

function LaunchHomeAfterRegister(state,props){
const payload = {
                                    "email":state.email,
                                    "password":state.password
                                };
                                const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/login/";
                                axios.post(apiUrl,payload).then((response) => {

                                      const resetPasswordActive = response.data.profile.resetPasswordActive;

                                      ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                                      store.setState(state => ({
                                        ...state,
                                        auth: resetPasswordActive,
                                        email:state.email,
                                        bearer:response.data.bearerToken,
                                        sourceOrg: response.data.sourceOrg
                                      }));
                                      LaunchHome(props,response.data);
                                }).catch(err => {
                                       ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
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
                                       else if(err.response != null && err.response.status == 403)
                                      {
                                           const access_denied = (
                                                                <CAlert
                                                                color="warning"
                                                                >
                                                                   403: Access Denied.
                                                               </CAlert>
                                                            );
                                          ReactDOM.render(access_denied,document.getElementById('access_denied'));
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
}

function LaunchHome(props,orgLogin)
{
    const orgId = store.getState().sourceOrg.orgId;
    const profile = orgLogin.profile;
    const sourceOrg = orgLogin.sourceOrg;
    const producer = sourceOrg.producer;
    const resetPasswordActive = profile.resetPasswordActive;

    if(resetPasswordActive){
       store.setState(state => ({
                                                       ...state,
                                                       auth: false,

                                                     }));
       props.history.push({
        pathname: "/staffResetPassword",
        state: { data: orgLogin }
      });
      return;
    }

    var apiUrl;
    if(producer)
    {
        apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/recovery/?orgId="+orgId;
    }
    else
    {
        apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/dropoff/?orgId="+orgId;
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

    axios.get(apiUrl).then((response) => {
        ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
        store.setState(state => ({
                                                ...state,
                                                auth: true,

                                              }));
        const responseData = response.data;
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
}

class LoginForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {username:'',profileType:'ORG',email:'',password:'',mobile:'',sourceOrgId:'', producer:'',isModalOpen:false,
    street:'',zip:'',
    activeElementType: "dropdown"};
  }

  render() {
    return (
      <>
        <br/><br/><br/><br/><br/>
        <div id="validation_error"/>
        <div id="unknown_error"/>
        <div id="progress"/>


        <div class="page-content bg-white">
                <div
                  class="banner-three bg-primary"
                >
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
                              It is an Uber-like Network for Volunteer FoodRunners<br/>
                            </p>
                            <p
                                    data-wow-delay="1.5s"
                                    data-wow-duration="3s"
                                    class="wow fadeInUp m-b30"
                            >
                              FoodRunners are volunteers with an IOS or Android App
                            </p>
                            <p
                                    data-wow-delay="1.5s"
                                    data-wow-duration="3s"
                                    class="wow fadeInUp m-b30"
                            >
                              FoodRunners pick up surplus food from restaurants, cafeterias, parties, etc
                            </p>
                            <p
                                    data-wow-delay="1.5s"
                                    data-wow-duration="3s"
                                    class="wow fadeInUp m-b30"
                            >
                              They deliver it to a participating Organization such as a Church, a Food Pantry, etc.
                            </p>
                            <p
                                    data-wow-delay="1.5s"
                                    data-wow-duration="3s"
                                    class="wow fadeInUp m-b30"
                            >
                              The Network is designed to assist those that are Hungry.
                            </p>
                            <div id="parent">
                                        <RenderLogin state={this.state} props={this.props}/>
                                    </div>
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
             </div>
      </>
    );
  }
}

export default withRouter(LoginForm)