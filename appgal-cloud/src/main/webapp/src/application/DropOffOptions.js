import React, { useEffect, useState, createRef, lazy } from 'react'
import ReactDOM from 'react-dom';
import { withRouter } from "react-router";
import axios from 'axios'
import {
  CCardGroup,
  CCardFooter,
  CCol,
  CLink,
  CRow,
  CWidgetProgress,
  CWidgetIcon,
  CWidgetProgressIcon,
  CWidgetSimple,
  CWidgetBrand,
  CHeaderNavLink,
  CProgress,
  CNav,
  CNavLink,
  CWidgetDropdown,
  CDropdown,
  CDropdownMenu,
  CDropdownToggle,
  CDropdownItem,
  CAlert,
  CModal,
  CModalHeader,
  CModalTitle,
  CModalBody,
  CCard,
  CCardHeader,
  CCardBody,
  CFormGroup,
  CLabel,
  CInput,
  CSelect,
  CModalFooter,
  CButton,
  CBadge,
  CButtonGroup,
  CCallout
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import WidgetsDropdown from './WidgetsDropdown'
import Modals from '../views/notifications/modals/Modals'
import ChartLineSimple from '../views/charts/ChartLineSimple'
import ChartBarSimple from '../views/charts/ChartBarSimple'
import { AppContext,store} from "./AppContext"
// @material-ui/core components
import { makeStyles } from "@material-ui/core/styles";
// core components
import GridItem from "../components/Grid/GridItem.js";
import GridContainer from "../components/Grid/GridContainer.js";
import Table from "../components/Table/Table.js";
import Card from "../components/Card/Card.js";
import CardHeader from "../components/Card/CardHeader.js";
import CardBody from "../components/Card/CardBody.js";
import CustomTabs from "../components/CustomTabs/CustomTabs.js";
import Tasks from "../components/Tasks/Tasks.js";
import { bugs, website, server } from "./variables/general.js";

import BugReport from "@material-ui/icons/BugReport";
import Code from "@material-ui/icons/Code";
import Cloud from "@material-ui/icons/Cloud";

import PropTypes from "prop-types";
import classnames from "classnames";
// @material-ui/core components
import Checkbox from "@material-ui/core/Checkbox";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";
import TableRow from "@material-ui/core/TableRow";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
// @material-ui/icons
import Edit from "@material-ui/icons/Edit";
import Close from "@material-ui/icons/Close";
import Check from "@material-ui/icons/Check";
import Button from "../components/CustomButtons/Button.js";

const styles = {
  cardCategoryWhite: {
    "&,& a,& a:hover,& a:focus": {
      color: "rgba(255,255,255,.62)",
      margin: "0",
      fontSize: "14px",
      marginTop: "0",
      marginBottom: "0"
    },
    "& a,& a:hover,& a:focus": {
      color: "#FFFFFF"
    }
  },
  cardTitleWhite: {
    color: "#FFFFFF",
    marginTop: "0px",
    minHeight: "auto",
    fontWeight: "300",
    fontFamily: "'Roboto', 'Helvetica', 'Arial', sans-serif",
    marginBottom: "3px",
    textDecoration: "none",
    "& small": {
      color: "#777",
      fontSize: "65%",
      fontWeight: "400",
      lineHeight: "1"
    }
  }
};

const useStyles = makeStyles(styles);

//const showNotification = place => {
function ShowNotification(place){
    console.log("*********SHOWNOTIFICATION********");

    const [tl, setTL] = React.useState(false);
    const [tc, setTC] = React.useState(false);
    const [tr, setTR] = React.useState(false);
    const [bl, setBL] = React.useState(false);
    const [bc, setBC] = React.useState(false);
    const [br, setBR] = React.useState(false);
    /*React.useEffect(() => {
        // Specify how to clean up after this effect:
        return function cleanup() {
          // to stop the warning of calling setState of unmounted component
          var id = window.setTimeout(null, 0);
          while (id--) {
            window.clearTimeout(id);
          }
        };
    });*/
    switch (place) {
      case "tl":
        if (!tl) {
          setTL(true);
          setTimeout(function() {
            setTL(false);
          }, 6000);
        }
        break;
      case "tc":
        if (!tc) {
          setTC(true);
          setTimeout(function() {
            setTC(false);
          }, 6000);
        }
        break;
      case "tr":
        if (!tr) {
          setTR(true);
          setTimeout(function() {
            setTR(false);
          }, 6000);
        }
        break;
      case "bl":
        if (!bl) {
          setBL(true);
          setTimeout(function() {
            setBL(false);
          }, 6000);
        }
        break;
      case "bc":
        if (!bc) {
          setBC(true);
          setTimeout(function() {
            setBC(false);
          }, 6000);
        }
        break;
      case "br":
        if (!br) {
          setBR(true);
          setTimeout(function() {
            setBR(false);
          }, 6000);
        }
        break;
      default:
        break;
    }
};

const DropOffOptionsView = ({props,dropOffOrgs,offlineCommunityHelpers,widget}) => {
    const classes = useStyles();
    const orgArray = [];
    const orgStatus = [];
    const orgIdArray = [];
    const helperArray = [];
    const orgTaskIndex = [];
    const helperTaskIndex = [];
    const helperArrayStatus = [];
    for (const [index, value] of dropOffOrgs.entries()) {
        const org = value.orgName +"- "+value.street+", "+value.zip;
        const row = [org];
        orgArray.push(row);
        orgTaskIndex.push(index);

        const orgId = value.orgId;
        orgIdArray.push(orgId);
    }
    for (const [index, value] of offlineCommunityHelpers.entries()) {
            const email = value.profile.email;
            const row = [email];
            helperArray.push(row);
            helperTaskIndex.push(index);
            helperArrayStatus.push(true);
    }

    if(offlineCommunityHelpers.length > 0)
    {
        return(
            <>
                <br/><br/><br/>
                <GridContainer>
                        <GridItem xs={12} sm={12} md={6}>
                          <CustomTabs
                            title="DropOff Options"
                            headerColor="primary"
                            tabs={[
                              {
                                tabName: "Organizations",
                                tabIcon: BugReport,
                                tabContent: (
                                  <Tasks
                                    checkedIndexes={[0]}
                                    tasksIndexes={orgTaskIndex}
                                    tasks={orgArray}
                                    actions={orgArray}
                                    status={true}
                                    orgIds={orgIdArray}
                                    pickupNotificationId={props.location.state.data.pickupNotificationId}
                                    history={props.history}
                                  />
                                )
                              },
                              {
                                tabName: "Homeless Helpers",
                                tabIcon: Code,
                                tabContent: (
                                  <Tasks
                                    checkedIndexes={[0]}
                                    tasksIndexes={helperTaskIndex}
                                    tasks={helperArray}
                                    actions={helperArray}
                                    status = {false}
                                    pickupNotificationId={props.location.state.data.pickupNotificationId}
                                    history={props.history}
                                  />
                                )
                              },
                            ]}
                          />
                        </GridItem>
                      </GridContainer>
            </>
        );
    }
    else
    {
        return(
                    <>
                        <br/><br/><br/>
                        <GridContainer>
                                <GridItem xs={12} sm={12} md={6}>
                                  <CustomTabs
                                    title="DropOff Options"
                                    headerColor="primary"
                                    tabs={[
                                      {
                                        tabName: "Organizations",
                                        tabIcon: BugReport,
                                        tabContent: (
                                          <Tasks
                                            checkedIndexes={[0]}
                                            tasksIndexes={orgTaskIndex}
                                            tasks={orgArray}
                                            actions={orgArray}
                                            status={true}
                                            orgIds={orgIdArray}
                                            pickupNotificationId={props.location.state.data.pickupNotificationId}
                                            history={props.history}
                                          />
                                        )
                                      },
                                    ]}
                                  />
                                </GridItem>
                              </GridContainer>
                    </>
                );
    }
}

class DropOffOptions extends React.Component
{
    constructor(props)
    {
        super(props);
        //console.log("State: "+JSON.stringify(this.props.location.state.data));
        this.handlePickupProcess = this.handlePickupProcess.bind(this);
        this.handlePickup = this.handlePickup.bind(this);
    }

    handlePickup(event)
    {
         console.log("*******PICKUP**********");
         ShowNotification("tc");
         /*const dropOffOrgId = event.target.value;
         const payload = {
            pickupNotificationId:this.props.location.state.data.pickupNotificationId,
            dropOffOrgId: dropOffOrgId,
            sourceOrg:store.getState().sourceOrg
         };

         //console.log(JSON.stringify(payload));

         const apiUrl = window.location.protocol +"//"+window.location.hostname+"/notification/schedulePickup/";
                  axios.post(apiUrl,payload).then((response) => {
                        console.log(JSON.stringify(response.data));
                        ShowNotification("tc");
          }).catch(err => {
           //TODO
           console.log(JSON.stringify(err));
          });*/
    }

    handlePickupProcess(event)
    {
        //TODO:
        const producer = true;
        const orgId = store.getState().sourceOrg.orgId;
        const apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/recovery/?orgId="+orgId;
        axios.get(apiUrl).then((response) => {
            if(producer)
            {
                   this.props.history.push({
                     pathname: "/home",
                     state: { data: response.data }
                   });
            }
            else
            {
                    this.props.history.push({
                      pathname: "/dropOffHome",
                      state: { data: response.data }
                    });
            }
        });
    }

    render()
    {
        const dropOffOrgs = this.props.location.state.data.dropOffOrgs;
        const offlineCommunityHelpers = this.props.location.state.data.offlineCommunityHelpers;
        return(
            <>
                <br/>
                                      <br/>
                                      <br/>
                                      <br/>
                                      <div id="unknown_error"/>
                                      <div id="progress"/>
                                <div id="schedulePickup"></div>
                <DropOffOptionsView props={this.props} dropOffOrgs={dropOffOrgs} offlineCommunityHelpers={offlineCommunityHelpers} widget={this}/>
            </>
        );
    }
}

export default withRouter(DropOffOptions)