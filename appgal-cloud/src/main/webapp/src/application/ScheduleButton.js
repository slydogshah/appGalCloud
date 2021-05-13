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
import { AppContext,store} from "./AppContext"

import AddAlert from "@material-ui/icons/AddAlert";
import Snackbar from "../components/Snackbar/Snackbar.js";

class ScheduleButton extends React.Component
{
    element;
    constructor(props)
    {
        super(props);
        //console.log("State: "+JSON.stringify(this.props.value));
        this.schedulePickup = this.schedulePickup.bind(this);
        this.schedulePickupProcess = this.schedulePickupProcess.bind(this);
    }

    schedulePickup(event)
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
                                <Snackbar
                                                  place="tc"
                                                  color="info"
                                                  icon={AddAlert}
                                                  message="Welcome to MATERIAL DASHBOARD React - a beautiful freebie for every web developer."
                                                  open={true}
                                                  closeNotification={() => {
                                                    this.props.history.push({
                                                                pathname: "/home"
                                                            });
                                                  }}
                                                  close
                                                />
                            );
                            /*this.element = (
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
                                                                            <CButton color="success" onClick={this.schedulePickupProcess}>OK</CButton>
                                                                        </CModalFooter>
                                                                      </CModal>
                                                                 );*/
                            ReactDOM.unmountComponentAtNode(document.getElementById('schedulePickup'));
                            ReactDOM.render(this.element,document.getElementById('schedulePickup'));
              }).catch(err => {
               //TODO
               console.log(JSON.stringify(err));
              });
    }

    schedulePickupProcess(event)
    {
        this.props.history.push({
            pathname: "/home"
        });
    }

    render()
    {
        return(
            <>
                <div className="progress-group-prepend">
                    <span className="progress-group-text">
                        <CButton color="success" value={this.props.value} onClick={this.schedulePickup}>Schedule</CButton>
                    </span>
                </div>
            </>
        );
    }
}

export default withRouter(ScheduleButton)