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
import CIcon from '@coreui/icons-react'
import WidgetsDropdown from './WidgetsDropdown'
import Modals from '../views/notifications/modals/Modals'
import ChartLineSimple from '../views/charts/ChartLineSimple'
import ChartBarSimple from '../views/charts/ChartBarSimple'
import { AppContext,store} from "./AppContext"

const fields = [
          { key: 'dropOffNotification', label:'Organization', _style: { width: '40%'} },
          { key: 'state', label:'Status', _style: { width: '20%'} }
]

const getBadge = (status)=>{
         switch (status) {
           case 'Active': return 'success'
           case 'Inactive': return 'secondary'
           case 'Pending': return 'warning'
           case 'Banned': return 'danger'
           default: return 'primary'
         }
     }

const ClosedTransactionView = ({closed}) => {
    console.log("******CLOSED_TX_VIEW****************");
    console.log(JSON.stringify(closed));
    const txs = []
    for (const [index, value] of closed.entries()) {
        txs.push(
             <div className="progress-group mb-4">
                    <div className="progress-group-prepend">
                      <span className="progress-group-text">
                        {value.pickupNotification.dropOffOrg.orgName}
                      </span>
                    </div>
                    <div className="progress-group-prepend">
                      <span className="progress-group-text">

                      </span>
                    </div>
                    <div className="progress-group-bars">
                      <CProgress className="progress-xs" color="info" value="34" />
                      <CProgress className="progress-xs" color="danger" value="78" />
                    </div>
              </div>
         )
    }
    return(
        <div>
            {txs}
        </div>
    )
}

const WaitOnData = ({state}) => {
    if (state.data === null) {
          return <p>Loading...</p>;
    }
    return (
          <>
          <br/>
          <br/>
          <br/>
          <br/>
          <ClosedTransactionView closed={state.data}/>
          </>
    )
}

class PickupHistory extends React.Component
{
    constructor(props)
    {
        super(props);
        this.state = {data: null};
        this.renderMyData();
    }

    renderMyData()
    {
        const orgId = store.getState().sourceOrg.orgId;
        const apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/recovery/history/?orgId="+orgId;
        axios.get(apiUrl).then((response) => {
            this.setState({data: response.data});
        });
    }

    render()
    {
        return(
            <>
                <WaitOnData state={this.state} />
            </>
        );
    }
}

export default withRouter(PickupHistory)