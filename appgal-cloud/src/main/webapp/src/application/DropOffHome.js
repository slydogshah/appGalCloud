import React, { useEffect, useState, createRef } from 'react'
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
  CButton
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import WidgetsDropdown from './WidgetsDropdown'
import DropOffDash from './DropOffDash'
import Modals from '../views/notifications/modals/Modals'
import ChartLineSimple from '../views/charts/ChartLineSimple'
import ChartBarSimple from '../views/charts/ChartBarSimple'

class DropOffHome extends React.Component {

  element;
  constructor(props) {
      super(props);
      console.log("DropOffHome: "+JSON.stringify(this.props.location.state));
      this.state = {username:'',password:'',isModalOpen:false};
      this.handleHistory = this.handleHistory.bind(this);
  }

  handleHistory(event)
  {
        const apiUrl = 'http://localhost:8080/tx/recovery/history/?orgId='+'microsoft';
              axios.get(apiUrl).then((response) => {
                this.props.history.push({
                  pathname: "/dropOffHistory",
                  state: response.data
                });
              });
  }

  render() {
      return (
          <>
          <CRow>
          <CCol>
          <CCardGroup className="mb-4">
                 <CWidgetDropdown
                           color="gradient-primary"
                           header={this.props.location.state.inProgress.length}
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
                    <DropOffDash inProgress={this.props.location.state.inProgress}/>
                </CCol>
                </CRow>
          </>
      );
  }
}

export default withRouter(DropOffHome)
