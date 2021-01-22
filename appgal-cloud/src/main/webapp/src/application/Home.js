import React, { useEffect, useState, createRef, lazy } from 'react'
import ReactDOM from 'react-dom';
import { withRouter } from "react-router";
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

const PendingTransactionView = () => {
    const txs = []
    const elements = [
        {id: 0, name: 'John Doe', registered: '2018/01/01', role: 'Guest', status: 'Pending'},
        {id: 1, name: 'Samppa Nori', registered: '2018/01/01', role: 'Member', status: 'Active'},
        {id: 2, name: 'Estavan Lykos', registered: '2018/02/01', role: 'Staff', status: 'Banned'},
        {id: 3, name: 'Chetan Mohamed', registered: '2018/02/01', role: 'Admin', status: 'Inactive'},
        {id: 4, name: 'Derick Maximinus', registered: '2018/03/01', role: 'Member', status: 'Pending'},
        {id: 5, name: 'Friderik Dávid', registered: '2018/01/21', role: 'Staff', status: 'Active'},
        {id: 6, name: 'Yiorgos Avraamu', registered: '2018/01/01', role: 'Member', status: 'Active'},
        {id: 7, name: 'Avram Tarasios', registered: '2018/02/01', role: 'Staff', status: 'Banned'},
        {id: 8, name: 'Quintin Ed', registered: '2018/02/01', role: 'Admin', status: 'Inactive'},
        {id: 9, name: 'Enéas Kwadwo', registered: '2018/03/01', role: 'Member', status: 'Pending'},
        {id: 10, name: 'Agapetus Tadeáš', registered: '2018/01/21', role: 'Staff', status: 'Active'},
        {id: 11, name: 'Carwyn Fachtna', registered: '2018/01/01', role: 'Member', status: 'Active'},
        {id: 12, name: 'Nehemiah Tatius', registered: '2018/02/01', role: 'Staff', status: 'Banned'},
        {id: 13, name: 'Ebbe Gemariah', registered: '2018/02/01', role: 'Admin', status: 'Inactive'},
        {id: 14, name: 'Eustorgios Amulius', registered: '2018/03/01', role: 'Member', status: 'Pending'},
        {id: 15, name: 'Leopold Gáspár', registered: '2018/01/21', role: 'Staff', status: 'Active'},
        {id: 16, name: 'Pompeius René', registered: '2018/01/01', role: 'Member', status: 'Active'},
        {id: 17, name: 'Paĉjo Jadon', registered: '2018/02/01', role: 'Staff', status: 'Banned'},
        {id: 18, name: 'Micheal Mercurius', registered: '2018/02/01', role: 'Admin', status: 'Inactive'},
        {id: 19, name: 'Ganesha Dubhghall', registered: '2018/03/01', role: 'Member', status: 'Pending'},
        {id: 20, name: 'Hiroto Šimun', registered: '2018/01/21', role: 'Staff', status: 'Active'},
        {id: 21, name: 'Vishnu Serghei', registered: '2018/01/01', role: 'Member', status: 'Active'},
        {id: 22, name: 'Zbyněk Phoibos', registered: '2018/02/01', role: 'Staff', status: 'Banned'},
        {id: 23, name: 'Aulus Agmundr', registered: '2018/01/01', role: 'Member', status: 'Pending'},
        {id: 42, name: 'Ford Prefect', registered: '2001/05/25', role: 'Alien', status: 'Don\'t panic!'}
      ]
    for (const [index, value] of elements.entries()) {
        txs.push(
             <div className="progress-group mb-4">
                    <div className="progress-group-prepend">
                      <span className="progress-group-text">
                        Monday
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

class Home extends React.Component {
  element;
  constructor(props) {
      super(props);
      //console.log("State: "+this.props.location.state);
      this.state = {username:'',password:'',isModalOpen:false};
      this.handlePickup = this.handlePickup.bind(this);
      this.handlePickupProcess = this.handlePickupProcess.bind(this);
      this.handlePickupHistory = this.handlePickupHistory.bind(this);
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
     /*const element = (
                      <CAlert
                      color="dark"
                      closeButton
                      >
                         blah
                     </CAlert>
                  );*/
     ReactDOM.unmountComponentAtNode(document.getElementById('schedulePickup'));
     ReactDOM.render(this.element,document.getElementById('schedulePickup'));
  }

  handlePickupProcess(event)
  {
      this.props.history.push({
                  pathname: "/schedulePickup",
                  state: ""
                });
  }

  handlePickupHistory(event)
    {
        this.props.history.push({
                    pathname: "/pickupHistory",
                    state: ""
                  });
    }

  render() {
      return (
          <>
          <div id="schedulePickup"></div>
          <CRow>
          <CCol>
          <CCardGroup className="mb-4">
                 <CWidgetDropdown
                           color="gradient-primary"
                           header={this.props.location.state.inProgress.length}
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
                           <CDropdownItem onClick={this.handlePickup}>Schedule</CDropdownItem>
                           <CDropdownItem onClick={this.handlePickupHistory}>History</CDropdownItem>
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
                                  Pickups In-Progress
                                </CCardHeader>
                                <CCardBody>
                                  <CRow>
                                    <CCol xs="12" md="6" xl="6">

                                      <CRow>
                                        <CCol sm="6">
                                          <CCallout color="info">
                                            <small className="text-muted">Pending</small>
                                            <br />
                                            <strong className="h4">{this.props.location.state.pending.length}</strong>
                                          </CCallout>
                                        </CCol>
                                      </CRow>

                                      <hr className="mt-0" />
                                      <PendingTransactionView/>
                                    </CCol>

                                    <CCol xs="12" md="6" xl="6">

                                      <CRow>
                                        <CCol sm="6">
                                          <CCallout color="success">
                                            <small className="text-muted">Accepted</small>
                                            <br />
                                            <strong className="h4">{this.props.location.state.inProgress.length}</strong>
                                          </CCallout>
                                        </CCol>
                                      </CRow>

                                      <hr className="mt-0" />

                                      <div className="progress-group mb-4">
                                        <div className="progress-group-header">
                                          <CIcon className="progress-group-icon" name="cil-user" />
                                          <span className="title">Male</span>
                                          <span className="ml-auto font-weight-bold">43%</span>
                                        </div>
                                        <div className="progress-group-bars">
                                          <CProgress className="progress-xs" color="warning" value="43" />
                                        </div>
                                      </div>
                                      <div className="progress-group mb-5">
                                        <div className="progress-group-header">
                                          <CIcon className="progress-group-icon" name="cil-user-female" />
                                          <span className="title">Female</span>
                                          <span className="ml-auto font-weight-bold">37%</span>
                                        </div>
                                        <div className="progress-group-bars">
                                          <CProgress className="progress-xs" color="warning" value="37" />
                                        </div>
                                      </div>
                                      <div className="progress-group">
                                        <div className="progress-group-header">
                                          <CIcon className="progress-group-icon" name="cil-globe-alt" />
                                          <span className="title">Organic Search</span>
                                          <span className="ml-auto font-weight-bold">191,235 <span className="text-muted small">(56%)</span></span>
                                        </div>
                                        <div className="progress-group-bars">
                                          <CProgress className="progress-xs" color="success" value="56" />
                                        </div>
                                      </div>


                                      <div className="progress-group">
                                        <div className="progress-group-header">
                                          <CIcon name="cib-facebook" className="progress-group-icon" />
                                          <span className="title">Facebook</span>
                                          <span className="ml-auto font-weight-bold">51,223 <span className="text-muted small">(15%)</span></span>
                                        </div>
                                        <div className="progress-group-bars">
                                          <CProgress className="progress-xs" color="success" value="15" />
                                        </div>
                                      </div>
                                      <div className="progress-group">
                                        <div className="progress-group-header">
                                          <CIcon name="cib-twitter" className="progress-group-icon" />
                                          <span className="title">Twitter</span>
                                          <span className="ml-auto font-weight-bold">37,564 <span className="text-muted small">(11%)</span></span>
                                        </div>
                                        <div className="progress-group-bars">
                                          <CProgress className="progress-xs" color="success" value="11" />
                                        </div>
                                      </div>
                                      <div className="progress-group">
                                        <div className="progress-group-header">
                                          <CIcon name="cib-linkedin" className="progress-group-icon" />
                                          <span className="title">LinkedIn</span>
                                          <span className="ml-auto font-weight-bold">27,319 <span className="text-muted small">(8%)</span></span>
                                        </div>
                                        <div className="progress-group-bars">
                                          <CProgress className="progress-xs" color="success" value="8" />
                                        </div>
                                      </div>
                                      <div className="divider text-center">
                                        <CButton color="link" size="sm" className="text-muted">
                                          <CIcon name="cil-options" />
                                        </CButton>
                                      </div>

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

export default withRouter(Home)
