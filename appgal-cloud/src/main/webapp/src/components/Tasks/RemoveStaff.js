import React, { useEffect, useState, createRef, lazy } from 'react'
import ReactDOM from 'react-dom';
import { withRouter } from "react-router";
import axios from 'axios'
import PropTypes from "prop-types";
import classnames from "classnames";
// @material-ui/core components
import { makeStyles } from "@material-ui/core/styles";
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
import styles from "../../assets/jss/material-dashboard-react/components/tasksStyle.js";

import GridItem from "../Grid/GridItem.js";
import GridContainer from "../Grid/GridContainer.js";
import Button from "../CustomButtons/Button.js";

import { AppContext,store} from "../../application/AppContext"
import AddAlert from "@material-ui/icons/AddAlert";
import Snackbar from "../Snackbar/Snackbar.js";
import DonutLargeOutlinedIcon from '@material-ui/icons/DonutLargeOutlined';

import ScheduleButton from '../../application/ScheduleButton'

import CustomTabs from "../CustomTabs/CustomTabs.js";
import BugReport from "@material-ui/icons/BugReport";
import AddStaff from "./AddStaff.js";
import {
  CAlert,
  CRow,
  CCol,
  CCard,
} from '@coreui/react'

const useStyles = makeStyles(styles);

function schedulePickup(props,history,pickupNotificationId,dropOffOrgId,enableOfflineCommunitySupport)
{
}

export default function RemoveStaff(props) {
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
              const { tasksIndexes, tasks, rtlActive, actions, status,orgIds,pickupNotificationId,history,buttonTitle,state } = props;
              const tableCellClasses = classnames(classes.tableCell, {
                [classes.tableCellRTL]: rtlActive
              });

              return (    <>
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

                                    <TableCell className={classes.tableActions}>
                                                        <Tooltip
                                                          id="tooltip-top-start"
                                                          title={buttonTitle}
                                                          placement="top"
                                                          classes={{ tooltip: classes.tooltip }}
                                                        >
                                                          <Button color="primary" onClick={(e) => {
                                                            const email = tasks[value];


                                                            const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/staff/?email="+email;
                                                            axios.delete(apiUrl).then((response) => {
                                                                    const orgProfiles = response.data;

                                                                    var element = (
                                                                         <AddStaffView state={state} props={props} orgProfiles={orgProfiles}/>
                                                                     );
                                                                     ReactDOM.unmountComponentAtNode(document.getElementById('addStaff'));
                                                                     ReactDOM.render(element,document.getElementById('addStaff'));
                                                            });
                                                          }}>{buttonTitle}</Button>
                                                        </Tooltip>
                                                    </TableCell>

                                </TableRow>
                              ))}
                            </TableBody>
                          </Table>
                          </>
                        );
}

function AddStaffView({state, props, orgProfiles}) {

   const orgArray = [];
   const orgTaskIndex = [];

   for (const [index, value] of orgProfiles.entries()) {
      const email = value.email;
      const row = [email];
      orgArray.push(row);
      orgTaskIndex.push(index);
   }
   const widget = (<>
   <CRow>
                              <CCol>
                                  <GridContainer>
                                                          <GridItem xs={12} sm={12} md={6}>
                                                            <CustomTabs
                                                              headerColor="primary"
                                                              tabs={[
                                                                {
                                                                  tabName: "Staff",
                                                                  tabIcon: BugReport,
                                                                  tabContent: (
                                                                    <RemoveStaff
                                                                      checkedIndexes={[0]}
                                                                      tasksIndexes={orgTaskIndex}
                                                                      tasks={orgArray}
                                                                      status={true}
                                                                      history={props.history}
                                                                      buttonTitle="Remove"
                                                                      state={state}
                                                                    />
                                                                  )
                                                                },
                                                                {
                                                                  tabName: "Add a Staff Member",
                                                                  tabIcon: BugReport,
                                                                  tabContent: (
                                                                    <AddStaff
                                                                      checkedIndexes={[0]}
                                                                      tasksIndexes={orgTaskIndex}
                                                                      tasks={orgArray}
                                                                      status={true}
                                                                      history={props.history}
                                                                      buttonTitle="Add"
                                                                      state={state}
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
             );
             return widget;

}


RemoveStaff.propTypes = {
  tasksIndexes: PropTypes.arrayOf(PropTypes.number),
  tasks: PropTypes.arrayOf(PropTypes.node),
  rtlActive: PropTypes.bool,
  checkedIndexes: PropTypes.array
};
