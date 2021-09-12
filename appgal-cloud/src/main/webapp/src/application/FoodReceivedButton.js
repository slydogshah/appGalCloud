import React, { useEffect, useState, createRef, lazy } from 'react'
import ReactDOM from 'react-dom';
import { withRouter } from "react-router";
import { axios} from "../App"
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
import DropOffHome from './DropOffHome'
import GridItem from "../components/Grid/GridItem.js";
import GridContainer from "../components/Grid/GridContainer.js";
import CustomTabs from "../components/CustomTabs/CustomTabs.js";
import DropOffTasks from "../components/Tasks/DropOffTasks.js";
import BugReport from "@material-ui/icons/BugReport";
import ChartLineSimple from '../views/charts/ChartLineSimple'

/*function ReRender(txs){
    console.log("*****************RERENDER*****************: "+txs);

    const inProgress = txs.pending;
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

    const history = txs.history;
    var historyArray = [];
    var historyIndexes = [];
    for (const [index, value] of history.entries()) {
        const org = value.pickupNotification.sourceOrg.orgName;
        historyArray.push(org);
        historyIndexes.push(index);
    }
    return (
      <>
                <br/><br/><br/><br/>
                <CRow>
                <CCol>
                <CCardGroup className="mb-4">
                       <CWidgetDropdown
                                 color="gradient-primary"
                                 header={txs.pending.length}
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
                                                           {
                                                            tabName: "History",
                                                            tabIcon: BugReport,
                                                            tabContent: (
                                                              <DropOffTasks
                                                                checkedIndexes={[0]}
                                                                tasksIndexes={historyIndexes}
                                                                tasks={historyArray}
                                                                actions={[]}
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
}*/

class FoodReceivedButton extends React.Component
{
    constructor(props)
    {
        super(props);
        //console.log("State: "+JSON.stringify(this.props.value));
        this.notifyFoodDelivery = this.notifyFoodDelivery.bind(this);
    }

    notifyFoodDelivery(event)
    {
             const tx = this.props.value;

             //console.log(JSON.stringify(payload));

             const apiUrl = window.location.protocol +"//"+window.location.hostname+"/activeNetwork/notifyDelivery/";
                      axios.post(apiUrl,tx).then((response) => {
                            console.log(JSON.stringify(response.data));

                            //ReactDOM.unmountComponentAtNode(document.getElementById('inProgress'));
                            //ReactDOM.render(ReRender(response.data),document.getElementById('inProgress'));
                            /*this.props.history.push({
                                    pathname: "/dropOffHome",
                                    metadata: {reload:true}
                             });*/
                             this.renderMyData();
              }).catch(err => {
               //TODO
               console.log(JSON.stringify(err));
              });
    }

    renderMyData(){
            const orgId = store.getState().sourceOrg.orgId;
            //const orgId = "church";
            const apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/dropoff/?orgId="+orgId;
            axios.get(apiUrl).then((response) => {
                //console.log("MY_DATA: "+JSON.stringify(response.data));
                this.setState({data: response.data});
                this.props.history.push({
                       pathname: "/dropOffHome"
                });
            });
      }

    render()
    {
        return(
            <>
                <div className="progress-group-prepend">
                    <span className="progress-group-text">
                        <CButton color="success" onClick={this.notifyFoodDelivery}>Food Received</CButton>
                    </span>
                </div>
            </>
        );
    }
}

export default withRouter(FoodReceivedButton)