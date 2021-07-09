import React, { useEffect, useState, createRef, lazy } from 'react'
import ReactDOM from 'react-dom';
import { withRouter } from "react-router";
import axios from 'axios'
import PropTypes from "prop-types";
import classnames from "classnames";
// @material-ui/core components
import { makeStyles } from "@material-ui/core/styles";
import Checkbox from "@material-ui/core/Checkbox";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";
import Table from "@material-ui/core/Table";
import TableRow from "@material-ui/core/TableRow";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
// @material-ui/icons
import Edit from "@material-ui/icons/Edit";
import Close from "@material-ui/icons/Close";
import Check from "@material-ui/icons/Check";
// core components
import styles from "../../assets/jss/material-dashboard-react/components/tasksStyle.js";

import GridItem from "../Grid/GridItem.js";
import GridContainer from "../Grid/GridContainer.js";
import Button from "../CustomButtons/Button.js";

import { AppContext,store} from "../../application/AppContext"
import AddAlert from "@material-ui/icons/AddAlert";
import Snackbar from "../Snackbar/Snackbar.js";
import DonutLargeOutlinedIcon from '@material-ui/icons/DonutLargeOutlined';
import CustomTabs from "../CustomTabs/CustomTabs.js";
import Tasks from "../Tasks/Tasks.js";
import RemoveStaff from "../Tasks/RemoveStaff.js";
import BugReport from "@material-ui/icons/BugReport";

import ScheduleButton from '../../application/ScheduleButton'

import {
  CAlert,
  CRow,
  CCol,
  CCard,
} from '@coreui/react'
import CustomInput from "../CustomInput/CustomInput.js";

import Card from "../Card/Card.js";
import CardHeader from "../Card/CardHeader.js";
import CardAvatar from "../Card/CardAvatar.js";
import CardBody from "../Card/CardBody.js";
import CardFooter from "../Card/CardFooter.js";

const useStyles = makeStyles(styles);

function schedulePickup(props,history,pickupNotificationId,dropOffOrgId,enableOfflineCommunitySupport)
{

     const payload = {
        pickupNotificationId:pickupNotificationId,
        enableOfflineCommunitySupport:enableOfflineCommunitySupport,
     };
     if(dropOffOrgId != null)
     {
        payload.dropOffOrgId = dropOffOrgId;
     }

//show progress bar
                                                                var element = (
                                                                        <Snackbar
                                                                          place="tc"
                                                                          color="info"
                                                                          icon={DonutLargeOutlinedIcon}
                                                                          message="Starting a Pickup Request...."
                                                                          open={true}
                                                                        />
                                                                );
                                                                ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                                                                ReactDOM.render(element,document.getElementById('progress'));
     const apiUrl = window.location.protocol +"//"+window.location.hostname+"/notification/schedulePickup/";
     axios.post(apiUrl,payload).then((response) => {
        ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
        //console.log(JSON.stringify(response.data));
        const element = (
            <Snackbar
                      place="tc"
                      color="info"
                      icon={AddAlert}
                      message="Your Pickup is scheduled"
                      open={true}
                      closeNotification={() => {
                            const orgId = store.getState().sourceOrg.orgId;
                            const apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/recovery/?orgId="+orgId;
                            axios.get(apiUrl).then((response) => {
                                history.push({
                                 pathname: "/home",
                                 state: { data: response.data }
                               });
                            });
                      }}
                      close
            />
        );
        ReactDOM.unmountComponentAtNode(document.getElementById('schedulePickup'));
        ReactDOM.render(element,document.getElementById('schedulePickup'));
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

export default function AddStaff(props) {
    const classes = useStyles();
              const [checked, setChecked] = React.useState([...props.checkedIndexes]);
              const handleToggle = value => {
                const currentIndex = checked.indexOf(value);
                const newChecked = [...checked];
                if (currentIndex === -1) {
                  newChecked.push(value);
                } else {
                  newChecked.splice(currentIndex, 1);
                }
                setChecked(newChecked);
              };
              const { tasksIndexes, tasks, rtlActive, actions, status,orgIds,pickupNotificationId,history,buttonTitle,state } = props;
              const tableCellClasses = classnames(classes.tableCell, {
                [classes.tableCellRTL]: rtlActive
              });

              return (
                    <>
                    <Table className={classes.table}>
                      <TableBody>
                          <TableRow>
                              <CRow>
                              <CCol>
                                <Card>
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
                                </CardBody>
                                <CardFooter>
                                   <GridContainer>
                                      <GridItem xs={12} sm={12} md={6}>
                                       <Button color="primary" onClick={(e) => {
                                             const orgId = store.getState().sourceOrg.orgId;
                                             const payload = {
                                                 "email":state.email,
                                                 "password":state.password,
                                                 "orgId":orgId
                                             };
                                             const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/staff";
                                             axios.post(apiUrl,payload).then((response) => {
                                                    const profile = response.data;
                                                    //alert(JSON.stringify(profile))

                                                    var element = (
                                                         <AddStaffView state={state} props={props} profile={profile}/>
                                                     );
                                                     ReactDOM.unmountComponentAtNode(document.getElementById('addStaff'));
                                                     ReactDOM.render(element,document.getElementById('addStaff'));
                                             });
                                       }}>Register</Button>
                                    </GridItem>
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
                                                       history.push({
                                                         pathname: "/home",
                                                         state: { data: response.data }
                                                       });
                                                }
                                                else
                                                {
                                                        history.push({
                                                          pathname: "/dropOffHome",
                                                          state: { data: response.data }
                                                        });
                                                }
                                            });
                                       }}>Cancel</Button>
                                    </GridItem>
                                    </GridContainer>
                                  </CardFooter>
                                  </Card>
                              </CCol>
                              </CRow>
                          </TableRow>
                      </TableBody>
                    </Table>
                    </>
              );

}

function AddStaffView({state, props, profile}) {
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


AddStaff.propTypes = {
  tasksIndexes: PropTypes.arrayOf(PropTypes.number),
  tasks: PropTypes.arrayOf(PropTypes.node),
  rtlActive: PropTypes.bool,
  checkedIndexes: PropTypes.array
};
