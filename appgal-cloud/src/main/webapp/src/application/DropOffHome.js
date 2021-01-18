import React, { useEffect, useState, createRef } from 'react'
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
  CButton
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import WidgetsDropdown from './WidgetsDropdown'
import DropOffDash from './DropOffDash'
import Modals from '../views/notifications/modals/Modals'
import ChartLineSimple from '../views/charts/ChartLineSimple'
import ChartBarSimple from '../views/charts/ChartBarSimple'

class Home extends React.Component {

  element;
  constructor(props) {
      super(props);
      this.state = {username:'',password:'',isModalOpen:false};
      this.handlePickup = this.handlePickup.bind(this);
      this.handlePickupProcess = this.handlePickupProcess.bind(this);
      this.handleHistory = this.handleHistory.bind(this);
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

  handleHistory(event)
    {
        this.props.history.push({
                    pathname: "/dropOffHistory",
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
                           header="50"
                           text="Deliveries In-Progress"
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
                           <CDropdownItem onClick={this.handleHistory}>History</CDropdownItem>
                         </CDropdownMenu>
                       </CDropdown>
                     </CWidgetDropdown>
          </CCardGroup>
          </CCol>
          </CRow>
          <CRow>
                <CCol>
                    <DropOffDash/>
                </CCol>
                </CRow>
          </>
      );
  }
}

export default withRouter(Home)
