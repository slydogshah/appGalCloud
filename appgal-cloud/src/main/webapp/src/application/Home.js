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

const PendingTransactionView = ({pending}) => {
    const txs = []
    for (const [index, value] of pending.entries()) {
        txs.push(
             <div className="progress-group mb-4">
                    <div className="progress-group-prepend">
                      <span className="progress-group-text">
                        {value.transactionState}
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

const InProgressTransactionView = ({inProgress}) => {
    const txs = []
    for (const [index, value] of inProgress.entries()) {
        txs.push(
             <div className="progress-group mb-4">
                    <div className="progress-group-prepend">
                      <span className="progress-group-text">
                        {value.transactionState}
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

/**/

const WaitOnData = ({state}) => {
    if (state.data === null) {
      return <p>Loading...</p>;
    }
    return (
      <>
      <div id="schedulePickup"></div>
            <CRow>
                <CCol>
                    <CCardGroup className="mb-4">
                           <CWidgetDropdown
                                     color="gradient-primary"
                                     header={state.data.inProgress.length}
                                     text="Pickups In-Progress"
                                     footerSlot={
                                       <ChartLineSimple
                                         pointed
                                         className="c-chart-wrapper mt-3 mx-3"
                                         style={{height: '70px'}}
                                         dataPoints={[65, 59, 84, 84, 51, 55, 40]}
                                         pointHoverBackgroundColor="primary"
                                         label="Members"
                                         labels="months"
                                       />
                                     }
                                   >
                                 <CDropdown>
                                   <CDropdownToggle color="transparent">
                                     <CIcon name="cil-settings"/>
                                   </CDropdownToggle>
                                   <CDropdownMenu className="pt-0" placement="bottom-end">
                                     <CDropdownItem>Schedule</CDropdownItem>
                                     <CDropdownItem>History</CDropdownItem>
                                   </CDropdownMenu>
                                 </CDropdown>
                               </CWidgetDropdown>
                    </CCardGroup>
                </CCol>
            </CRow>
            <CRow>
                  <CCol>
                      <CRow>
                              <CCol>
                                <CCard>
                                  <CCardHeader>
                                    Pickups - Pending
                                  </CCardHeader>
                                  <CCardBody>
                                    <CRow>
                                          <CCol xs="12" md="6" xl="6">
                                            <CRow>
                                              <CCol sm="6">
                                                <CCallout color="info">
                                                  <small className="text-muted">Pending</small>
                                                  <br />
                                                  <strong className="h4">{state.data.pending.length}</strong>
                                                </CCallout>
                                              </CCol>
                                            </CRow>
                                            <hr className="mt-0" />
                                            <PendingTransactionView pending={state.data.pending}/>
                                            <hr className="mt-0" />
                                          </CCol>
                                      </CRow>
                                  </CCardBody>
                                </CCard>
                              </CCol>
                      </CRow>
                  </CCol>
                  <CCol>
                      <CRow>
                              <CCol>
                                <CCard>
                                  <CCardHeader>
                                    Pickups - In-Progress
                                  </CCardHeader>
                                  <CCardBody>
                                    <CRow>
                                          <CCol xs="12" md="6" xl="6">
                                            <CRow>
                                              <CCol sm="6">
                                                <CCallout color="info">
                                                  <small className="text-muted">In-Progress</small>
                                                  <br />
                                                  <strong className="h4">{state.data.inProgress.length}</strong>
                                                </CCallout>
                                              </CCol>
                                            </CRow>
                                            <hr className="mt-0" />
                                            <InProgressTransactionView inProgress={state.data.inProgress}/>
                                            <hr className="mt-0" />
                                          </CCol>
                                      </CRow>
                                  </CCardBody>
                                </CCard>
                              </CCol>
                      </CRow>
                  </CCol>
            </CRow>
      </>
    )
}

class Home extends React.Component {
  element;
  constructor(props) {
      super(props);
      this.handlePickup = this.handlePickup.bind(this);
      this.handlePickupProcess = this.handlePickupProcess.bind(this);
      this.handlePickupHistory = this.handlePickupHistory.bind(this);
      this.state = {data: null};

      this.renderMyData();
  }

  renderMyData(){
    const apiUrl = 'http://localhost:8080/tx/recovery/?email=cindu@zoe.com';
    axios.get(apiUrl).then((response) => {
        this.setState({data: response.data});
        //console.log("RESPONSE: "+JSON.stringify(response.data));
    });
  }

  handlePickup(event)
  {
     this.element = (
          <CModal
            size="sm"
            show={true}
            color="success"
            fade="true"
          >
            <CModalHeader>
              <CModalTitle>Schedule A Pickup</CModalTitle>
            </CModalHeader>
            <CModalBody>
                 <CCard>
                     <CCardBody>
                       <CRow>
                         <CCol>
                           <CFormGroup>
                             <CLabel htmlFor="ccmonth">Preferred Pickup Time</CLabel>
                             <CSelect custom name="ccmonth" id="ccmonth">
                               <option value="0">12:00 AM</option>
                               <option value="12">12:00 PM</option>
                               <option value="23">11:59 PM</option>
                             </CSelect>
                           </CFormGroup>
                         </CCol>
                       </CRow>
                     </CCardBody>
                   </CCard>
            </CModalBody>
            <CModalFooter>
                <CButton color="success" onClick={this.handlePickupProcess}>Schedule</CButton>
            </CModalFooter>
          </CModal>
     );
     ReactDOM.unmountComponentAtNode(document.getElementById('schedulePickup'));
     ReactDOM.render(this.element,document.getElementById('schedulePickup'));
  }

  handlePickupProcess(event)
  {
      const apiUrl = 'http://localhost:8080/notification/dropOffOrgs/?orgId='+'microsoft';
      axios.get(apiUrl).then((response) => {
        this.props.history.push({
          pathname: "/schedulePickup",
          state: response.data
        });
      });
  }

  handlePickupHistory(event)
  {
    const apiUrl = 'http://localhost:8080/tx/recovery/history/?orgId='+'microsoft'; //TODO: unmock
    axios.get(apiUrl).then((response) => {
          this.props.history.push({
            pathname: "/pickupHistory",
            state: response.data
          });
    });
  }

  render() {
     return (
          <div>
            <WaitOnData state={this.state}/>
          </div>
      );
  }
}

export default withRouter(Home)
