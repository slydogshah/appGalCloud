import React from "react";
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
import FoodReceivedButton from '../../application/FoodReceivedButton'

const useStyles = makeStyles(styles);

export default function DropOffTasks(props) {
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
  const { tasksIndexes, tasks, rtlActive, actions } = props;
  const tableCellClasses = classnames(classes.tableCell, {
    [classes.tableCellRTL]: rtlActive
  });
  if(actions.length > 0){
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


                      <TableCell className={classes.tableActions}>
                        <Tooltip
                          id="tooltip-top-start"
                          title="Indicate Food was Delivered"
                          placement="top"
                          classes={{ tooltip: classes.tooltip }}
                        >
                          <FoodReceivedButton value={actions[value]}/>
                        </Tooltip>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )
     }
     else{
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
}

DropOffTasks.propTypes = {
  tasksIndexes: PropTypes.arrayOf(PropTypes.number),
  tasks: PropTypes.arrayOf(PropTypes.node),
  rtlActive: PropTypes.bool,
  checkedIndexes: PropTypes.array
};
