import React, { useState } from 'react'
import ReactDOM from 'react-dom';
import { withRouter } from "react-router";
import {
  CBadge,
  CDropdown,
  CDropdownItem,
  CDropdownMenu,
  CDropdownToggle,
  CButton,
  CImg,
  CLink
} from '@coreui/react'
import CIcon from '@coreui/icons-react'

import classNames from "classnames";
// @material-ui/core components
import { makeStyles } from "@material-ui/core/styles";
import MenuItem from "@material-ui/core/MenuItem";
import MenuList from "@material-ui/core/MenuList";
import Grow from "@material-ui/core/Grow";
import Paper from "@material-ui/core/Paper";
import ClickAwayListener from "@material-ui/core/ClickAwayListener";
import Hidden from "@material-ui/core/Hidden";
import Poppers from "@material-ui/core/Popper";
import Divider from "@material-ui/core/Divider";
// @material-ui/icons
import Person from "@material-ui/icons/Person";
import Notifications from "@material-ui/icons/Notifications";
import Dashboard from "@material-ui/icons/Dashboard";
import Search from "@material-ui/icons/Search";
// core components
import CustomInput from "../components/CustomInput/CustomInput.js";
import Button from "../components/CustomButtons/Button.js";

import styles from "../assets/jss/material-dashboard-react/components/headerLinksStyle.js";
import { axios} from "../App"
import { AppContext,store} from "../application/AppContext"

const useStyles = makeStyles(styles);


//TODO: hook the actual logout routine



//class TheHeaderDropdown extends React.Component {
export default function AdminNavbarLinks(props) {
        const classes = useStyles();
        const [openNotification, setOpenNotification] = React.useState(null);
        const [openProfile, setOpenProfile] = React.useState(null);
        const handleClickNotification = event => {
          if (openNotification && openNotification.contains(event.target)) {
            setOpenNotification(null);
          } else {
            setOpenNotification(event.currentTarget);
          }
        };
        const handleCloseNotification = () => {
          setOpenNotification(null);
        };
        const handleClickProfile = event => {
          if (openProfile && openProfile.contains(event.target)) {
            setOpenProfile(null);
          } else {
            setOpenProfile(event.currentTarget);
          }
        };
        const handleCloseProfile = () => {
          store.getState().auth = false;
          setOpenProfile(null);
        };
      return (
        <div className={classes.manager}>
                <Button
                  color={window.innerWidth > 959 ? "transparent" : "white"}
                  justIcon={window.innerWidth > 959}
                  simple={!(window.innerWidth > 959)}
                  aria-owns={openProfile ? "profile-menu-list-grow" : null}
                  aria-haspopup="true"
                  onClick={handleClickProfile}
                  className={classes.buttonLink}
                >
                  <Person className={classes.icons} />
                  <Hidden mdUp implementation="css">
                    <p className={classes.linkText}>Profile</p>
                  </Hidden>
                </Button>
                <Poppers
                  open={Boolean(openProfile)}
                  anchorEl={openProfile}
                  transition
                  disablePortal
                  className={
                    classNames({ [classes.popperClose]: !openProfile }) +
                    " " +
                    classes.popperNav
                  }
                >
                  {({ TransitionProps, placement }) => (
                    <Grow
                      {...TransitionProps}
                      id="profile-menu-list-grow"
                      style={{
                        transformOrigin:
                          placement === "bottom" ? "center top" : "center bottom"
                      }}
                    >
                      <Paper>
                        <ClickAwayListener onClickAway={handleCloseProfile}>
                          <MenuList role="menu">
                            <MenuItem
                              onClick={handleCloseProfile}
                              className={classes.dropdownItem}
                            >
                              <CLink className={classes.dropdownItem} onClick={(event)=>{
                                            const orgId = store.getState().sourceOrg.orgId;
                                            const producer = store.getState().sourceOrg.producer;
                                            var apiUrl;
                                            if(producer)
                                            {
                                                apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/recovery/?orgId="+orgId;
                                            }
                                            else
                                            {
                                                apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/dropoff/?orgId="+orgId;
                                            }
                                            axios.get(apiUrl).then((response) => {
                                                //console.log(JSON.stringify(props));
                                                store.getState().auth = true;
                                                if(producer)
                                                {
                                                       props.props.props.history.push({
                                                         pathname: "/home",
                                                         state: { data: response.data }
                                                       });
                                                }
                                                else
                                                {
                                                        props.props.props.history.push({
                                                          pathname: "/dropOffHome",
                                                          state: { data: response.data }
                                                        });
                                                }
                                            });
                              }}>Home</CLink>
                            </MenuItem>
                            <MenuItem
                              onClick={handleCloseProfile}
                              className={classes.dropdownItem}
                            >
                              <CLink className={classes.dropdownItem} onClick={(event)=>{
                                    const email = store.getState().email;
                                    const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/profile/?email="+email;
                                    axios.get(apiUrl).then((response) => {
                                        store.getState().auth = true;
                                        props.props.props.history.push({
                                         pathname: "/profile",
                                         state: { data: response.data }
                                       });
                                    });
                              }}>Account</CLink>
                            </MenuItem>
                            <Divider light />
                            <MenuItem
                              onClick={handleCloseProfile}
                              className={classes.dropdownItem}
                            >
                              <CLink to="/" className={classes.dropdownItem}>Logout</CLink>
                            </MenuItem>
                          </MenuList>
                        </ClickAwayListener>
                      </Paper>
                    </Grow>
                  )}
                </Poppers>
           </div>
      );
  }
