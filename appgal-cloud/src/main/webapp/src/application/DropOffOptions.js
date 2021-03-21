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

const DropOffOptionsView = ({dropOffOrgs}) => {
    console.log("ARRAY: "+JSON.stringify(dropOffOrgs));
    const array = []
    for (const [index, value] of dropOffOrgs.entries()) {
        array.push(
             <div className="progress-group mb-4">
                 <div className="progress-group-prepend">
                   <span className="progress-group-text">
                     {value.orgName}
                   </span>
                 </div>
                 <div className="progress-group-prepend">
                   <span className="progress-group-text">
                     {value.orgContactEmail}
                   </span>
                 </div>
                 <div className="progress-group-bars">
                   <CProgress className="progress-xs" color="info" value="34" />
                   <CProgress className="progress-xs" color="danger" value="78" />
                 </div>
           </div>
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
                                          <DropOffOptionsView dropOffOrgs={dropOffOrgs}/>
                                        </CCol>
                                        <CCol xs="12" md="6" xl="6">
                                            <hr className="mt-0" />
                                            <CButton color="success" onClick={this.handlePickupProcess}>Schedule</CButton>
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