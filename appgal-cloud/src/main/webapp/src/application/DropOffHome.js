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
import Modals from '../views/notifications/modals/Modals'
import ChartLineSimple from '../views/charts/ChartLineSimple'
import ChartBarSimple from '../views/charts/ChartBarSimple'
import { AppContext,store} from "./AppContext"
import GridItem from "../components/Grid/GridItem.js";
import GridContainer from "../components/Grid/GridContainer.js";
import CustomTabs from "../components/CustomTabs/CustomTabs.js";
import DropOffTasks from "../components/Tasks/DropOffTasks.js";
import BugReport from "@material-ui/icons/BugReport";

const WaitOnData = ({state, handleHistory}) => {
    if (state.data === null) {
      return <p>Loading...</p>;
    }

    const inProgress = state.data.pending;
    var array = [];
    var tasksIndexes = [];
    for (const [index, value] of inProgress.entries()) {
        const org = value.pickupNotification.sourceOrg.orgName;
        array.push(org);
        tasksIndexes.push(index);
    }
    var actionsArray = [];
    for (var i=0;i<1;i++) {
            const actionRow = ["action"];
            actionsArray.push(actionRow);
    }
    return (
      <>
                <br/><br/><br/><br/>
                <CRow>
                <CCol>
                <CCardGroup className="mb-4">
                       <CWidgetDropdown
                                 color="gradient-primary"
                                 header={state.data.pending.length}
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
                           </CWidgetDropdown>
                </CCardGroup>
                </CCol>
                </CRow>
                <CRow>
                    <CCol>
                        <GridContainer>
                                                     <GridItem xs={12} sm={12} md={6}>
                                                       <CustomTabs
                                                         title="Delivery Status"
                                                         headerColor="primary"
                                                         tabs={[
                                                           {
                                                             tabName: "In-Progress",
                                                             tabIcon: BugReport,
                                                             tabContent: (
                                                               <DropOffTasks
                                                                 checkedIndexes={[0]}
                                                                 tasksIndexes={tasksIndexes}
                                                                 tasks={array}
                                                                 actions={inProgress}
                                                               />
                                                             )
                                                           },
                                                         ]}
                                                       />
                                                     </GridItem>
                                                 </GridContainer>
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
      this.renderMyData();
      return (
          <div id="inProgress">
                <WaitOnData state={this.state} handleHistory={this.handleHistory}/>
          </div>
      );
  }
}

export default withRouter(DropOffHome)
