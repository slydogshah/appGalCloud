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

const DropOffOptionsView = ({dropOffOrgs,widget}) => {
    const classes = useStyles();

    const array = [];
    const orgArray = [];
    for (const [index, value] of dropOffOrgs.entries()) {
        console.log(JSON.stringify(value));
        const org = value.orgName +"- "+value.street+", "+value.zip;
        const orgId = value.orgId;
        const row = [org];
        const orgRow = [orgId];
        array.push(row);
        orgArray.push(orgRow);
    }

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
                                tasksIndexes={[0, 1]}
                                tasks={array}
                                actions={orgArray}
                              />
                            )
                          },
                          {
                            tabName: "Homeless Helpers",
                            tabIcon: Code,
                            tabContent: (
                              <Tasks
                                checkedIndexes={[0]}
                                tasksIndexes={[0,1]}
                                tasks={website}
                                actions={orgArray}
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
         const dropOffOrgId = event.target.value;
         const payload = {
            pickupNotificationId:this.props.location.state.data.pickupNotificationId,
            dropOffOrgId: dropOffOrgId,
            sourceOrg:store.getState().sourceOrg
         };

         //console.log(JSON.stringify(payload));

         const apiUrl = window.location.protocol +"//"+window.location.hostname+"/notification/schedulePickup/";
                  axios.post(apiUrl,payload).then((response) => {
                        console.log(JSON.stringify(response.data));
                        this.element = (
                                      <CModal
                                        size="sm"
                                        show={true}
                                        color="success"
                                        fade="true"
                                      >
                                        <CModalHeader>
                                          <CModalTitle>Pickup Confirmation</CModalTitle>
                                        </CModalHeader>
                                        <CModalBody>
                                             <CCallout color="info">
                                              <div className="progress-group-prepend">
                                                 <small className="text-muted">Your Pickup is scheduled</small>
                                              </div>
                                            </CCallout>
                                        </CModalBody>
                                        <CModalFooter>
                                            <CButton color="success" onClick={this.handlePickupProcess}>OK</CButton>
                                        </CModalFooter>
                                      </CModal>
                                 );
                                 ReactDOM.unmountComponentAtNode(document.getElementById('schedulePickup'));
                                 ReactDOM.render(this.element,document.getElementById('schedulePickup'));
          }).catch(err => {
           //TODO
           console.log(JSON.stringify(err));
          });
    }

    handlePickupProcess(event)
    {
        this.props.history.push({
            pathname: "/home"
        });
    }

    render()
    {
        const dropOffOrgs = this.props.location.state.data.dropOffOrgs;
        return(
            <>
                <br/>
                                      <br/>
                                      <br/>
                                      <br/>
                                <div id="schedulePickup"></div>
                <DropOffOptionsView dropOffOrgs={dropOffOrgs} widget={this}/>
            </>
        );
    }
}

export default withRouter(DropOffOptions)