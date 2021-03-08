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

const fields = [
          { key: 'dropOffNotification', label:'Organization', _style: { width: '40%'} },
          { key: 'state', label:'Status', _style: { width: '20%'} }
]

const getBadge = (status)=>{
         switch (status) {
           case 'Active': return 'success'
           case 'Inactive': return 'secondary'
           case 'Pending': return 'warning'
           case 'Banned': return 'danger'
           default: return 'primary'
         }
     }

const ClosedTransactionView = ({closed}) => {
    const txs = []
    for (const [index, value] of closed.entries()) {
        txs.push(
             <div className="progress-group mb-4">
                    <div className="progress-group-prepend">
                      <span className="progress-group-text">
                        {value.pickupNotification.sourceOrg.orgName}
                      </span>
                    </div>
                    <div className="progress-group-prepend">
                      <span className="progress-group-text">
                        {value.foodRunner.profile.email}
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
            {txs}
        </div>
    )
}

class DropOffHistory extends React.Component
{
    constructor(props)
    {
        super(props);
        console.log("DropOffHistory: "+JSON.stringify(this.props.location.state));
    }

    render()
    {
        return(
            <>
                <CRow>
                                <CCol>
                                    <CRow>
                                            <CCol>
                                              <CCard>
                                                <CCardHeader>
                                                  DropOff History
                                                </CCardHeader>
                                                <CCardBody>
                                                  <CRow>
                                                    <CCol xs="12" md="6" xl="6">

                                                      <CRow>
                                                        <CCol sm="6">
                                                          <CCallout color="info">
                                                            <small className="text-muted">Closed</small>
                                                            <br />
                                                            <strong className="h4">{this.props.location.state.length}</strong>
                                                          </CCallout>
                                                        </CCol>
                                                      </CRow>

                                                      <hr className="mt-0" />
                                                      <ClosedTransactionView closed={this.props.location.state}/>
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

export default withRouter(DropOffHistory)