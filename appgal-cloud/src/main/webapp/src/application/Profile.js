import React from "react";
import ReactDOM from 'react-dom';
import { withRouter } from "react-router";
import axios from 'axios'
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

import {
  CAlert,
  CRow,
  CCol
} from '@coreui/react'

import { AppContext,store} from "./AppContext"
import DonutLargeOutlinedIcon from '@material-ui/icons/DonutLargeOutlined';
import Snackbar from "../components/Snackbar/Snackbar.js";
import CustomTabs from "../components/CustomTabs/CustomTabs.js";
import Tasks from "../components/Tasks/Tasks.js";
import AddStaff from "../components/Tasks/AddStaff.js";
import RemoveStaff from "../components/Tasks/RemoveStaff.js";
import { bugs, website, server } from "./variables/general.js";
import BugReport from "@material-ui/icons/BugReport";
import Code from "@material-ui/icons/Code";

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

function ProfileView({state, props}) {
    //console.log(JSON.stringify(props.history.location.state.data.pending.length));


    const classes = useStyles();
    const profile = props.history.location.state.data;
    const data = state.data;
    const newPassword = null;
    const confirmNewPassword = null;
    state = {data: data, newPassword: newPassword, confirmNewPassword: confirmNewPassword};
    return (
        <>
        <br/><br/><br/>
        <div id="validation_error"/>
        <div>
          <CRow>
          <CCol>
          <GridContainer>
            <GridItem xs={12} sm={12} md={8}>
              <Card>
                <CardHeader color="primary">
                  <h4 className={classes.cardTitleWhite}>Update Profile</h4>
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
                            value:profile.email,
                            disabled: true
                        }}
                      />
                    </GridItem>
                  </GridContainer>
                  <GridContainer>
                    <GridItem xs={12} sm={12} md={6}>
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
                                //console.log("VALUE: "+value);
                                const data = state.data;
                                const newPassword = value;
                                const confirmNewPassword = state.confirmNewPassword;
                                state = {data: data, newPassword: newPassword, confirmNewPassword: confirmNewPassword};
                            }
                        }}
                      />
                    </GridItem>
                    <GridItem xs={12} sm={12} md={6}>
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
                                //console.log("VALUE: "+value);
                                const data = state.data;
                                const newPassword = state.newPassword;
                                const confirmNewPassword = value;
                                state = {data: data, newPassword: newPassword, confirmNewPassword: confirmNewPassword};
                            }
                        }}
                      />
                    </GridItem>
                  </GridContainer>
                </CardBody>
                <CardFooter>
                  <Button color="primary" onClick={(e) => {
                        //console.log("PASS:" + state.newPassword);
                        //console.log("PASS2:" + state.confirmNewPassword);
                        const email = profile.email;
                        const payload = {
                                                     newPassword:state.newPassword,
                                                     confirmNewPassword:state.confirmNewPassword,
                                                     email:profile.email
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
                        const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/resetPassword/";
                        axios.post(apiUrl,payload).then((response) => {
                            //console.log("MY_DATA: "+JSON.stringify(response.data));
                            const producer = store.getState().sourceOrg.producer;
                            const orgId = store.getState().sourceOrg.orgId;
                            const apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/recovery/?orgId="+orgId;
                            //console.log(apiUrl);
                            axios.get(apiUrl).then((response) => {
                                ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
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
                                if(err.response != null && err.response.status == 500)
                                   {
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
                                   }
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
                                       }
                            });
                    }}>Update</Button>
                    <GridItem xs={12} sm={12} md={6}>
                                                           <Button color="primary" onClick={(e) => {
                                                                const orgId = store.getState().sourceOrg.orgId;
                                                                const producer = store.getState().sourceOrg.producer;
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
                                                           }}>Cancel</Button>
                       </GridItem>
                </CardFooter>
              </Card>
            </GridItem>
          </GridContainer>
          </CCol>
          </CRow>
          <div id="addStaff">
            <AddStaffView state={state} props={props} profile={profile}/>
          </div>
          </div>
        </>
    );
}

function AddStaffView({state,props,profile}) {
   const orgProfiles = profile.orgProfiles;
   const orgArray = [];
   const orgTaskIndex = [];

   for (const [index, value] of orgProfiles.entries()) {
       const email = value.email;
       const row = [email];
       orgArray.push(row);
       orgTaskIndex.push(index);
   }

   const widget = (<>
         <CRow>
                            <CCol>
                                <GridContainer>
                                                        <GridItem xs={12} sm={12} md={6}>
                                                          <CustomTabs
                                                            headerColor="primary"
                                                            tabs={[
                                                              {
                                                                tabName: "Staff",
                                                                tabIcon: BugReport,
                                                                tabContent: (
                                                                  <RemoveStaff
                                                                    checkedIndexes={[0]}
                                                                    tasksIndexes={orgTaskIndex}
                                                                    tasks={orgArray}
                                                                    status={true}
                                                                    history={props.history}
                                                                    buttonTitle="Remove"
                                                                    state={state}
                                                                  />
                                                                )
                                                              },
                                                              {
                                                                tabName: "Add a Staff Member",
                                                                tabIcon: BugReport,
                                                                tabContent: (
                                                                  <AddStaff
                                                                    checkedIndexes={[0]}
                                                                    tasksIndexes={orgTaskIndex}
                                                                    tasks={orgArray}
                                                                    status={true}
                                                                    history={props.history}
                                                                    buttonTitle="Add"
                                                                    state={state}
                                                                  />
                                                                )
                                                              },
                                                            ]}
                                                          />
                                                        </GridItem>
                                                      </GridContainer>

                            </CCol>
                          </CRow>
             </>
           );
           return widget;
}

class Profile extends React.Component {
    constructor(props) {
            super(props);
            this.state = {data: null};
    }

    render() {
       return (
            <>
                <div id="unknown_error"/>
                <div id="progress"/>
                <div id="parent">
                  <ProfileView state={this.state} props={this.props} />
                </div>
            </>
        );
    }
}

export default withRouter(Profile)