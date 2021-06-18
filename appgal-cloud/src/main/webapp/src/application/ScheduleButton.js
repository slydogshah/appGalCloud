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

import AddAlert from '@material-ui/icons/AddAlert';
import DonutLargeOutlinedIcon from '@material-ui/icons/DonutLargeOutlined';
import Snackbar from "../components/Snackbar/Snackbar.js";

class ScheduleButton extends React.Component
{
    element;
    constructor(props)
    {
        super(props);
        this.schedulePickup = this.schedulePickup.bind(this);
        this.schedulePickupProcess = this.schedulePickupProcess.bind(this);
    }

    schedulePickup(event)
    {
         const dropOffOrgId = event.target.value;

         const payload = {
            pickupNotificationId:this.props.location.state.data.pickupNotificationId,
            dropOffOrgId: dropOffOrgId,
            enableOfflineCommunitySupport:false,
            sourceOrg:store.getState().sourceOrg
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
         const apiUrl = window.location.protocol +"//"+window.location.hostname+"/notification/schedulePickup/";
         axios.post(apiUrl,payload).then((response) => {
            ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
            console.log(JSON.stringify(response.data));
            this.element = (
                <Snackbar
                                  place="tc"
                                  color="info"
                                  icon={AddAlert}
                                  message="Your Pickup is scheduled"
                                  open={true}
                                  closeNotification={() => {
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
                                  }}
                                  close
                                />
                );
                ReactDOM.unmountComponentAtNode(document.getElementById('schedulePickup'));
                ReactDOM.render(this.element,document.getElementById('schedulePickup'));
          }).catch(err => {
           ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
           //TODO
           console.log(JSON.stringify(err));
          });
    }

    schedulePickupProcess(event)
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
        return(
            <>
                <div id="progress"/>
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