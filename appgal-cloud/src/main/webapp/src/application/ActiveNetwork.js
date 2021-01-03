import React from 'react'
import { useState } from "react";
import ReactDOM from 'react-dom';
import { withRouter } from "react-router";
import axios from 'axios'
import {
  CBadge,
  CCard,
  CCardBody,
  CCardHeader,
  CCardFooter,
  CCol,
  CDataTable,
  CRow,
  CButton,
  CAlert,
  CForm,
  CCollapse
} from '@coreui/react'

const fields = [
          { key: 'email', label:'Email', _style: { width: '40%'} },
          { key: 'mobile', label:'Phone'},
          { key: 'status', label:'Status', _style: { width: '20%'} }
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

const handle = (history) => {
    const apiUrl = 'http://localhost:8080/registration/login/';
    axios.post(apiUrl,{"email":"b@z.com","password":"by"}).then((response) => {
          history.push({
            pathname: "/schedulePickup",
            state: response.data
          });
    });
}

class ActiveNetwork extends React.Component
{
    constructor(props)
    {
        super(props);
        this.state = {payload:props.location.state};
    }

    render()
    {
        const activeFoodRunners = this.props.location.state;
        return(
           <CCard>
               <CCardHeader>
                   <CForm>
                     <CButton color="success" block size="sm" onClick={()=>handle(this.props.history)}>Schedule Pickup</CButton>
                   </CForm>
               </CCardHeader>
               <CCardBody>
                   <CDataTable
                          items={activeFoodRunners}
                          fields={fields}
                          hover
                          scopedSlots = {{
                            'status':
                              (item)=>(
                                <td>
                                  <CBadge color="danger">
                                    In-Transit
                                  </CBadge>
                                </td>
                              )
                           }}
                        />
               </CCardBody>
               <CCardFooter>
                   <CForm>
                     <CButton color="success" block size="sm" onClick={()=>handle(this.props.history)}>Schedule Pickup</CButton>
                   </CForm>
               </CCardFooter>
           </CCard>
        );
    }
}

export default withRouter(ActiveNetwork)