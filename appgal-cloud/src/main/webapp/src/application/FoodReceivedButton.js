import React, { useEffect, useState, createRef, lazy } from 'react'
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
  CBadge,
  CButtonGroup,
  CCallout
} from '@coreui/react'
import { AppContext,store} from "./AppContext"
import DropOffHome from './DropOffHome'

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
                            //ReactDOM.render(DropOffHome,document.getElementById('inProgress'));
                            this.props.history.push({
                                                pathname: "/dropOffHome"
                                              });
              }).catch(err => {
               //TODO
               console.log(JSON.stringify(err));
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