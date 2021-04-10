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
import { AppContext,store} from "./AppContext"


const WaitOnData = ({state, handleHistory}) => {
    if (state.data === null) {
      return <p>Loading...</p>;
    }
    return (
      <>
                <br/><br/><br/><br/>
                <CRow>
                <CCol>
                <CCardGroup className="mb-4">
                       <CWidgetDropdown
                                 color="gradient-primary"
                                 header={state.data.inProgress.length}
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
                                 <CDropdownItem onClick={handleHistory}>History</CDropdownItem>
                               </CDropdownMenu>
                             </CDropdown>
                           </CWidgetDropdown>
                </CCardGroup>
                </CCol>
                </CRow>
                <CRow>
                    <CCol>
                        <DropOffDash inProgress={state.data.inProgress}/>
                    </CCol>
                </CRow>
                </>
    )
}

class DropOffHome extends React.Component {

  element;
  constructor(props) {
      super(props);
      this.state = {data: null};
      this.handleHistory = this.handleHistory.bind(this);
      this.renderMyData();
  }

  renderMyData(){
      const orgId = store.getState().sourceOrg.orgId;
      //const orgId = "church";
      const apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/dropoff/?orgId="+orgId;
      axios.get(apiUrl).then((response) => {
          //console.log("MY_DATA: "+JSON.stringify(response.data));
          this.setState({data: response.data});
      });
    }

  handleHistory(event)
  {
        const orgId = store.getState().sourceOrg.orgId;
        //const orgId = "church";
        const apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/dropOff/history/?orgId="+orgId;
              axios.get(apiUrl).then((response) => {
                //console.log("MY_DATA: "+JSON.stringify(response.data));
                this.props.history.push({
                  pathname: "/dropOffHistory",
                  state: response.data
                });
              });
  }

  render() {
      return (
          <div>
                <WaitOnData state={this.state} handleHistory={this.handleHistory}/>
          </div>
      );
  }
}

export default withRouter(DropOffHome)
