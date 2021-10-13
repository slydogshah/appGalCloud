import React from "react";
import ReactDOM from 'react-dom';
import { withRouter } from "react-router";
import { axios} from "../App"
// react plugin for creating charts
//import ChartistGraph from "react-chartist";
// @material-ui/core
import { makeStyles } from "@material-ui/core/styles";
import Icon from "@material-ui/core/Icon";
// @material-ui/icons
import Store from "@material-ui/icons/Store";
import Warning from "@material-ui/icons/Warning";
import DateRange from "@material-ui/icons/DateRange";
import LocalOffer from "@material-ui/icons/LocalOffer";
import Update from "@material-ui/icons/Update";
import ArrowUpward from "@material-ui/icons/ArrowUpward";
import AccessTime from "@material-ui/icons/AccessTime";
import Accessibility from "@material-ui/icons/Accessibility";
import BugReport from "@material-ui/icons/BugReport";
import Code from "@material-ui/icons/Code";
import Cloud from "@material-ui/icons/Cloud";
// core components
import GridItem from "../components/Grid/GridItem.js";
import GridContainer from "../components/Grid/GridContainer.js";
import Table from "../components/Table/Table.js";
import Tasks from "../components/Tasks/Tasks.js";
import CustomTabs from "../components/CustomTabs/CustomTabs.js";
//import Danger from "../components/Typography/Danger.js";
import Card from "../components/Card/Card.js";
import CardHeader from "../components/Card/CardHeader.js";
import CardIcon from "../components/Card/CardIcon.js";
import CardBody from "../components/Card/CardBody.js";
import CardFooter from "../components/Card/CardFooter.js";

import { bugs, website, server } from "../variables/general.js";


import styles from "../assets/jss/material-dashboard-react/views/dashboardStyle.js";

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
                CCallout,
              } from '@coreui/react'
import ChartLineSimple from '../views/charts/ChartLineSimple'
import CIcon from '@coreui/icons-react'

import { AppContext,store} from "./AppContext"
import DonutLargeOutlinedIcon from '@material-ui/icons/DonutLargeOutlined';
import Snackbar from "../components/Snackbar/Snackbar.js";

const useStyles = makeStyles(styles);

function DropOffView({state, props}) {
  //console.log(JSON.stringify(props.history.location.state.data.pending.length));
  const classes = useStyles();

  const inProgress = props.history.location.state.data.pending;
  var array = [];
  for (const [index, value] of inProgress.entries()) {
    const row = [];
    const org = value.pickupNotification.sourceOrg.orgName;
    const orgContact = value.pickupNotification.sourceOrg.orgContactEmail;
    const scheduled = value.when+" "+value.pickupNotification.scheduled;
    row.push(org);
    row.push(orgContact);
    row.push(scheduled);
    array.push(row);
  }

  const active = props.history.location.state.data.inProgress;
    var activeArray = [];
    for (const [index, value] of active.entries()) {
      const row = [];
      const org = value.pickupNotification.sourceOrg.orgName;
      const orgContact = value.pickupNotification.sourceOrg.orgContactEmail;
      const foodRunner = value.pickupNotification.foodRunner.profile.email;
      const time = value.estimatedDropOffTime;
      row.push(org);
      row.push(orgContact);
      row.push(foodRunner);
      row.push(time);
      row.push(FoodReceivedButton(props,value));
      activeArray.push(row);
    }

  const history = props.history.location.state.data.history;
      var historyArray = [];
      for (const [index, value] of history.entries()) {
        const row = [];
        const org = value.pickupNotification.sourceOrg.orgName;
        const orgContact = value.pickupNotification.sourceOrg.orgContactEmail;
        const foodRunner = value.pickupNotification.foodRunner.profile.email;
        row.push(org);
        row.push(orgContact);
        row.push(foodRunner);
        historyArray.push(row);
      }
  const deliveries = inProgress.length + active.length
  return (
        <>
                            <br/><br/><br/><br/><br/>
                            <div>
                                <CRow>
                                                                                                                                                     <CCol>
                                   <CCardGroup className="mb-4">
                                                                                    <CWidgetDropdown
                                                                                              color="gradient-primary"
                                                                                              header={deliveries}
                                                                                              text="DropOffs In-Progress"
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
                            </div>
                            <div>
                              <GridContainer>
                                <GridItem xs={12} sm={12} md={6}>
                                  <Card>
                                    <CardHeader color="primary">
                                      <h4 className={classes.cardTitleWhite}>Pending DropOffs</h4>
                                      <p className={classes.cardCategoryWhite}>

                                      </p>
                                    </CardHeader>
                                    <CardBody>
                                      <Table
                                        tableHeaderColor="warning"
                                        tableHead={["Pickup Organization", "Contact Email", "Scheduled Pickup"]}
                                        tableData={array}
                                      />
                                    </CardBody>
                                  </Card>
                                </GridItem>
                                <GridItem xs={12} sm={12} md={6}>
                                  <Card>
                                    <CardHeader color="primary">
                                      <h4 className={classes.cardTitleWhite}>DropOffs In-Progress</h4>
                                      <p className={classes.cardCategoryWhite}>

                                      </p>
                                    </CardHeader>
                                    <CardBody>
                                      <Table
                                        tableHeaderColor="warning"
                                        tableHead={["Pickup Organization", "Contact Email", "Food Runner", "Estimated Arrival", "Notify"]}
                                        tableData={activeArray}
                                      />
                                    </CardBody>
                                  </Card>
                                </GridItem>
                                <GridItem xs={12} sm={12} md={6}>
                                                                  <Card>
                                                                    <CardHeader color="primary">
                                                                      <h4 className={classes.cardTitleWhite}>DropOff History</h4>
                                                                      <p className={classes.cardCategoryWhite}>

                                                                      </p>
                                                                    </CardHeader>
                                                                    <CardBody>
                                                                      <Table
                                                                        tableHeaderColor="warning"
                                                                        tableHead={["Pickup Organization", "Contact Email", "Food Runner"]}
                                                                        tableData={historyArray}
                                                                      />
                                                                    </CardBody>
                                                                  </Card>
                                                                </GridItem>
                              </GridContainer>
                            </div>
                            </>
  );
}

function FoodReceivedButton(props,tx){
    var element = (
        <div className="progress-group-prepend">
            <span className="progress-group-text">
                <CButton color="success" onClick={(event)=>{
                    notifyFoodDelivery(props,tx);
                }}>Food Received</CButton>
            </span>
         </div>
    );
    return element;
}

function notifyFoodDelivery(props,tx)
    {
             const payload = {
                txId:tx.id
             };

            //show progress bar
                                                                                 var element = (
                                                                                         <Snackbar
                                                                                           place="tc"
                                                                                           color="info"
                                                                                           icon={DonutLargeOutlinedIcon}
                                                                                           message="Loading...."
                                                                                           open={true}
                                                                                         />
                                                                                 );
                                                                                 ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                                                                                 ReactDOM.render(element,document.getElementById('progress'));
             const apiUrl = window.location.protocol +"//"+window.location.hostname+"/activeNetwork/notifyDelivery/";
             axios.post(apiUrl,payload).then((response) => {
                    ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                    const orgId = store.getState().sourceOrg.orgId;
                    const apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/dropoff/?orgId="+orgId;
                    axios.get(apiUrl).then((response) => {
                        props.history.push({
                          pathname: "/dropOffHome",
                          state: { data: response.data }
                        });
                    });

              }).catch(err => {
                ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
               var element = (
                                                                                       <Snackbar
                                                                                         place="tc"
                                                                                         color="danger"
                                                                                         icon={DonutLargeOutlinedIcon}
                                                                                         message="500: Unknown System Error...."
                                                                                         open={true}
                                                                                         close
                                                                                         closeNotification={() => {
                                                                                           ReactDOM.unmountComponentAtNode(document.getElementById('unknown_error'));
                                                                                         }}
                                                                                       />
                                                                               );
                                                                               ReactDOM.unmountComponentAtNode(document.getElementById('unknown_error'));
                                                                               ReactDOM.render(element,document.getElementById('unknown_error'));
              });
    }

class DropOffHome extends React.Component {
    constructor(props) {
        super(props);
        this.state = {data: null};
    }

    render() {
       return (
            <>
                <div id="unknown_error"/>
                <div id="progress"/>
                <div id="parent">
                  <DropOffView state={this.state} props={this.props} />
                </div>
            </>
        );
    }
}

export default withRouter(DropOffHome)
