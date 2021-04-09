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

const DropOffOptionsView = ({dropOffOrgs,widget}) => {
    const array = []
    for (const [index, value] of dropOffOrgs.entries()) {
        array.push(
           <CRow>
                 <CCol>
                   <CCard>
                     <CCardBody>
                       <CRow>
                             <CCol xs="12" md="6" xl="6">
                               <CRow>
                                 <CCol sm="6">
                                   <CCallout color="info">
                                     <strong className="h4">Organization</strong>
                                     <br />
                                     <div className="progress-group-prepend">
                                        <small className="text-muted">{value.orgName}</small>
                                     </div>
                                     <div className="progress-group-prepend">
                                        <span className="progress-group-text">
                                            <CButton color="success" value={value.orgId} onClick={widget.handlePickup}>Schedule</CButton>
                                        </span>
                                     </div>
                                   </CCallout>
                                 </CCol>
                               </CRow>
                             </CCol>
                       </CRow>
                     </CCardBody>
                   </CCard>
                </CCol>
         </CRow>
         )
    }
    return(
        <div>
            {array}
        </div>
    )
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
            pathname: "/dashboard/home"
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
                <CRow>
                    <CCol>
                        <CRow>
                                <CCol>
                                  <CCard>
                                    <CCardHeader>
                                      DropOff Options
                                    </CCardHeader>
                                    <CCardBody>
                                      <CRow>
                                        <CCol xs="12" md="6" xl="6">
                                          <hr className="mt-0" />
                                          <DropOffOptionsView dropOffOrgs={dropOffOrgs} widget={this}/>
                                        </CCol>
                                      </CRow>
                                    </CCardBody>
                                  </CCard>
                                </CCol>
                              </CRow>
                    </CCol>
                </CRow>
            </>
        );
    }
}

export default withRouter(DropOffOptions)