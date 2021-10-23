import React from 'react'
import ReactDOM from 'react-dom';
import { useSelector, useDispatch } from 'react-redux'
import {
  CHeader,
  CToggler,
  CHeaderBrand,
  CHeaderNav,
  CHeaderNavItem,
  CHeaderNavLink,
  CSubheader,
  CBreadcrumbRouter,
  CImg,
  CLink,
} from '@coreui/react'
import CIcon from '@coreui/icons-react'

// routes config
import routes from '../routes'

import {
  TheHeaderDropdown,
  TheHeaderDropdownMssg,
  TheHeaderDropdownNotif,
  TheHeaderDropdownTasks,
} from './index'
import { useLocation } from 'react-router-dom'

import { AppContext,store} from "../application/AppContext"

import styles from "../assets/jss/material-dashboard-react/components/headerLinksStyle.js";
import { makeStyles } from "@material-ui/core/styles";
import { axios} from "../App"

import DonutLargeOutlinedIcon from '@material-ui/icons/DonutLargeOutlined';
import Snackbar from "../components/Snackbar/Snackbar.js";

const useStyles = makeStyles(styles);

const TheHeader = (props) => {
  const location = useLocation();
  const auth = store.getState().auth;

  const classes = useStyles();




  if(location.pathname == "/")
  {
    return(
        <>


        </>
    );
  }
  else if(auth)
  {
      const producer = store.getState().sourceOrg.producer;
      if(producer){
      return (
            <header class="site-header mo-left header-transparent">

                                               <div class="sticky-header main-bar-wraper navbar-expand-lg">
                                                 <div class="main-bar clearfix">
                                                   <div class="container clearfix">



                                                     <button
                                                       class="navbar-toggler collapsed navicon justify-content-end"
                                                       type="button"
                                                       data-toggle="collapse"
                                                       data-target="#navbarNavDropdown"
                                                       aria-controls="navbarNavDropdown"
                                                       aria-expanded="false"
                                                       aria-label="Toggle navigation"
                                                     >
                                                       <span></span>
                                                       <span></span>
                                                       <span></span>
                                                     </button>

                                                     <div class="extra-nav">
                                                         <div class="extra-cell">
                                                           <a class="btn btn-corner gradient btn-primary"
                                                             onClick={(e) => {
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
                                                                       const orgId = store.getState().sourceOrg.orgId;
                                                                       const apiUrl = window.location.protocol +"//"+window.location.hostname+"/notification/addPickupDetails/?orgId="+orgId;
                                                                       axios.get(apiUrl).then((response) => {
                                                                           //alert(JSON.stringify(response.data));
                                                                           ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                                                                           const timeOptions = new Map(response.data);
                                                                           props.props.history.push({
                                                                            pathname: "/addPickupDetails",
                                                                            state: { data: response.data }
                                                                           });
                                                                       }).catch(err => {
                                                                           //alert(JSON.stringify(err));
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
                                                              }}
                                                           >
                                                                <i class="fa fa-angle-right m-r10"/>
                                                                Schedule a Pickup
                                                           </a>
                                                         </div>
                                                     </div>



                                                                                                           <div
                                                                                                             class="header-nav navbar-collapse collapse justify-content-end"
                                                                                                             id="navbarNavDropdown"
                                                                                                           >

                                                                                                             <ul class="nav navbar-nav navbar">
                                                                                                               <li class="active">
                                                                                                                 <a onClick={(event)=>{
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
                                                                                                                                                                        props.props.history.push({
                                                                                                                                                                          pathname: "/home",
                                                                                                                                                                          state: { data: response.data }
                                                                                                                                                                        });
                                                                                                                                                                 }
                                                                                                                                                                 else
                                                                                                                                                                 {
                                                                                                                                                                         props.props.history.push({
                                                                                                                                                                           pathname: "/dropOffHome",
                                                                                                                                                                           state: { data: response.data }
                                                                                                                                                                         });
                                                                                                                                                                 }
                                                                                                                                                             });
                                                                                                                                               }}>Home</a>
                                                                                                               </li>
                                                                                                               <li class="active">
                                                                                                                 <a onClick={(event)=>{
                                                                                                                                                     const email = store.getState().email;
                                                                                                                                                     const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/profile/?email="+email;
                                                                                                                                                     axios.get(apiUrl).then((response) => {
                                                                                                                                                         store.getState().auth = true;
                                                                                                                                                         props.props.history.push({
                                                                                                                                                          pathname: "/profile",
                                                                                                                                                          state: { data: response.data }
                                                                                                                                                        });
                                                                                                                                                     });
                                                                                                                                               }}>Account</a>
                                                                                                               </li>
                                                                                                               <li class="active">
                                                                                                                    <a onClick={(event)=>{
                                                                                                                        props.props.history.push({
                                                                                                                             pathname: "/"}
                                                                                                                        );


                                                                                                                    }}>Logout</a>
                                                                                                               </li>
                                                                                                             </ul>
                                                                                                             <div class="dlab-social-icon">
                                                                                                               <ul>
                                                                                                                 <li>
                                                                                                                   <a class="fa fa-facebook" href="javascript:void(0);"></a>
                                                                                                                 </li>
                                                                                                                 <li>
                                                                                                                   <a class="fa fa-twitter" href="javascript:void(0);"></a>
                                                                                                                 </li>
                                                                                                                 <li>
                                                                                                                   <a class="fa fa-linkedin" href="javascript:void(0);"></a>
                                                                                                                 </li>
                                                                                                                 <li>
                                                                                                                   <a class="fa fa-instagram" href="javascript:void(0);"></a>
                                                                                                                 </li>
                                                                                                               </ul>
                                                                                                             </div>
                                                                                                           </div>
                                                                                                     </div>


                                                 </div>
                                               </div>

                                             </header>
      );
    }else{
            return (
                        <header class="site-header mo-left header-transparent">

                                                           <div class="sticky-header main-bar-wraper navbar-expand-lg">
                                                             <div class="main-bar clearfix">
                                                               <div class="container clearfix">



                                                                 <button
                                                                   class="navbar-toggler collapsed navicon justify-content-end"
                                                                   type="button"
                                                                   data-toggle="collapse"
                                                                   data-target="#navbarNavDropdown"
                                                                   aria-controls="navbarNavDropdown"
                                                                   aria-expanded="false"
                                                                   aria-label="Toggle navigation"
                                                                 >
                                                                   <span></span>
                                                                   <span></span>
                                                                   <span></span>
                                                                 </button>





                                                                                                                       <div
                                                                                                                         class="header-nav navbar-collapse collapse justify-content-end"
                                                                                                                         id="navbarNavDropdown"
                                                                                                                       >

                                                                                                                         <ul class="nav navbar-nav navbar">
                                                                                                                           <li class="active">
                                                                                                                             <a onClick={(event)=>{
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
                                                                                                                                                                                    props.props.history.push({
                                                                                                                                                                                      pathname: "/home",
                                                                                                                                                                                      state: { data: response.data }
                                                                                                                                                                                    });
                                                                                                                                                                             }
                                                                                                                                                                             else
                                                                                                                                                                             {
                                                                                                                                                                                     props.props.history.push({
                                                                                                                                                                                       pathname: "/dropOffHome",
                                                                                                                                                                                       state: { data: response.data }
                                                                                                                                                                                     });
                                                                                                                                                                             }
                                                                                                                                                                         });
                                                                                                                                                           }}>Home</a>
                                                                                                                           </li>
                                                                                                                           <li class="active">
                                                                                                                             <a onClick={(event)=>{
                                                                                                                                                                 const email = store.getState().email;
                                                                                                                                                                 const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/profile/?email="+email;
                                                                                                                                                                 axios.get(apiUrl).then((response) => {
                                                                                                                                                                     store.getState().auth = true;
                                                                                                                                                                     props.props.history.push({
                                                                                                                                                                      pathname: "/profile",
                                                                                                                                                                      state: { data: response.data }
                                                                                                                                                                    });
                                                                                                                                                                 });
                                                                                                                                                           }}>Account</a>
                                                                                                                           </li>
                                                                                                                           <li class="active">
                                                                                                                                <a onClick={(event)=>{
                                                                                                                                    props.props.history.push({
                                                                                                                                         pathname: "/"}
                                                                                                                                    );


                                                                                                                                }}>Logout</a>
                                                                                                                           </li>
                                                                                                                         </ul>
                                                                                                                         <div class="dlab-social-icon">
                                                                                                                           <ul>
                                                                                                                             <li>
                                                                                                                               <a class="fa fa-facebook" href="javascript:void(0);"></a>
                                                                                                                             </li>
                                                                                                                             <li>
                                                                                                                               <a class="fa fa-twitter" href="javascript:void(0);"></a>
                                                                                                                             </li>
                                                                                                                             <li>
                                                                                                                               <a class="fa fa-linkedin" href="javascript:void(0);"></a>
                                                                                                                             </li>
                                                                                                                             <li>
                                                                                                                               <a class="fa fa-instagram" href="javascript:void(0);"></a>
                                                                                                                             </li>
                                                                                                                           </ul>
                                                                                                                         </div>
                                                                                                                       </div>
                                                                                                                 </div>


                                                             </div>
                                                           </div>

                                                         </header>
                  );
    }
  }
  else{
   return (
               <header class="site-header mo-left header-transparent">

                                   <div class="sticky-header main-bar-wraper navbar-expand-lg">
                                     <div class="main-bar clearfix">
                                       <div class="container clearfix">



                                         <button
                                           class="navbar-toggler collapsed navicon justify-content-end"
                                           type="button"
                                           data-toggle="collapse"
                                           data-target="#navbarNavDropdown"
                                           aria-controls="navbarNavDropdown"
                                           aria-expanded="false"
                                           aria-label="Toggle navigation"
                                         >
                                           <span></span>
                                           <span></span>
                                           <span></span>
                                         </button>

                                        </div>
                                     </div>
                                   </div>
                                    <br/><br/><br/><br/><br/>
                                 </header>
         );
  }
}

export default TheHeader
