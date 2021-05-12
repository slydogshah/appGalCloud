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
  CButton,
  CCallout,
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
import BugReport from "@material-ui/icons/BugReport";
import PropTypes from "prop-types";
import { makeStyles } from "@material-ui/core/styles";
import styles from "../assets/jss/material-dashboard-react/components/tasksStyle.js";
import classnames from "classnames";
import Checkbox from "@material-ui/core/Checkbox";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";
import Table from "@material-ui/core/Table";
import TableRow from "@material-ui/core/TableRow";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
// @material-ui/icons
import Edit from "@material-ui/icons/Edit";
import Close from "@material-ui/icons/Close";
import Check from "@material-ui/icons/Check";
// core components

const useStyles = makeStyles(styles);



PickupTasks.propTypes = {
  tasksIndexes: PropTypes.arrayOf(PropTypes.number),
  tasks: PropTypes.arrayOf(PropTypes.node),
  rtlActive: PropTypes.bool,
  checkedIndexes: PropTypes.array
};


function PickupTasks(props) {
  const classes = useStyles();
  const [checked, setChecked] = React.useState([...props.checkedIndexes]);
  const handleToggle = value => {
    const currentIndex = checked.indexOf(value);
    const newChecked = [...checked];
    if (currentIndex === -1) {
      newChecked.push(value);
    } else {
      newChecked.splice(currentIndex, 1);
    }
    setChecked(newChecked);
  };
  const { tasksIndexes, tasks, rtlActive} = props;
  const tableCellClasses = classnames(classes.tableCell, {
    [classes.tableCellRTL]: rtlActive
  });
  return (
              <Table className={classes.table}>
                <TableBody>
                  {tasksIndexes.map(value => (
                    <TableRow key={value} className={classes.tableRow}>
                      <TableCell className={tableCellClasses}>{tasks[value]}</TableCell>
                      <TableCell className={tableCellClasses}><div/></TableCell>
                      <TableCell className={tableCellClasses}><div/></TableCell>
                      <TableCell className={tableCellClasses}><div/></TableCell>
                      <TableCell className={tableCellClasses}><div/></TableCell>
                      <TableCell className={tableCellClasses}><div/></TableCell>
                                  <TableCell className={tableCellClasses}><div/></TableCell>
                                  <TableCell className={tableCellClasses}><div/></TableCell>
                                  <TableCell className={tableCellClasses}><div/></TableCell>
                                  <TableCell className={tableCellClasses}><div/></TableCell>
                                              <TableCell className={tableCellClasses}><div/></TableCell>
                                              <TableCell className={tableCellClasses}><div/></TableCell>
                                              <TableCell className={tableCellClasses}><div/></TableCell>
                                              <TableCell className={tableCellClasses}><div/></TableCell>
                                                          <TableCell className={tableCellClasses}><div/></TableCell>
                                                          <TableCell className={tableCellClasses}><div/></TableCell>
                                                          <TableCell className={tableCellClasses}><div/></TableCell>
                                                          <TableCell className={tableCellClasses}><div/></TableCell>
                                                                      <TableCell className={tableCellClasses}><div/></TableCell>
                                                                      <TableCell className={tableCellClasses}><div/></TableCell>
                                                                      <TableCell className={tableCellClasses}><div/></TableCell>
                                                                      <TableCell className={tableCellClasses}><div/></TableCell>




                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )
}


const WaitOnData = ({state, props, handlePickup, handlePickupHistory}) => {
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

    const history = state.data.history;
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
                    <div id="schedulePickup"></div>
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
                                   <CDropdown>
                                      <CDropdownToggle color="transparent">
                                        <CIcon name="cil-settings"/>
                                      </CDropdownToggle>
                                      <CDropdownMenu className="pt-0" placement="bottom-end">
                                       <CDropdownItem onClick={(e) => {
                                                props.history.push({
                                                   pathname: "/addPickupDetails"
                                                });
                                       }}>Schedule</CDropdownItem>
                                      </CDropdownMenu>
                                   </CDropdown>
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
                                                                   <PickupTasks
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
                                                                  <PickupTasks
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
    const orgId = store.getState().sourceOrg.orgId;
    const apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/recovery/?orgId="+orgId;
    axios.get(apiUrl).then((response) => {
        console.log("MY_DATA: "+JSON.stringify(response.data));
        this.setState({data: response.data});
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
      this.props.history.push({
                pathname: "/addPickupDetails"
      });
  }

  handlePickupHistory(event)
  {
    this.props.history.push({
                    pathname: "/pickupHistory"
          });
  }

  render() {
     return (
          <>
          <div>
            <WaitOnData state={this.state} props={this.props} handlePickup={this.handlePickup} handlePickupHistory={this.handlePickupHistory}/>
          </div>
          </>
      );
  }
}

export default withRouter(Home)
