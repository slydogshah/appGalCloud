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

const usersData = [
    {id: 0, email: 'John Doe', phone: '2018/01/01', role: 'Guest', status: 'Pending'},
    {id: 1, email: 'Samppa Nori', phone: '2018/01/01', role: 'Member', status: 'Active'},
    {id: 2, email: 'Estavan Lykos', phone: '2018/02/01', role: 'Staff', status: 'Banned'},
    {id: 3, email: 'Chetan Mohamed', phone: '2018/02/01', role: 'Admin', status: 'Inactive'},
    {id: 4, email: 'Derick Maximinus', phone: '2018/03/01', role: 'Member', status: 'Pending'},
    {id: 5, email: 'Friderik Dávid', phone: '2018/01/21', role: 'Staff', status: 'Active'}
  ]

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
    axios.post(apiUrl,{"email":"Do","password":"do","statusCode":null,"profile":null}).then((response) => {
          history.push({
            pathname: "/schedulePickup",
            state: response.data
            });
    });
    //alert('BLAh');
}

class Main extends React.Component
{
    constructor(props)
    {
        super(props);
        this.show = this.show.bind(this);
        this.state = {payload:props.location.state};
    }

    show(event)
    {
        const payload = (
               <CAlert
               color="warning"
               >
                  {JSON.stringify(this.state.payload)}
              </CAlert>
        );
        ReactDOM.unmountComponentAtNode(document.getElementById('payload'));
        ReactDOM.render(payload,document.getElementById('payload'));
    }

    render()
    {
        const activeFoodRunners = this.props.location.state.activeFoodRunners.profile;
        return(
           <>
           <div id="payload"/>
           <CCard>
           <CCardHeader>
           <CForm>
             <CButton color="success" block size="sm" onClick={this.show}>Schedule Pickup</CButton>
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
                          <CBadge color={getBadge(item.status)}>
                            {item.status}
                          </CBadge>
                        </td>
                      )
                   }}
                />
                </CCardBody>
                <CCardHeader>
                   <CForm>
                     <CButton color="success" block size="sm" onClick={this.show}>Schedule Pickup</CButton>
                   </CForm>
                </CCardHeader>
                </CCard>
                </>
        );
    }
}

export default withRouter(Main)

/*
const [details, setDetails] = useState([])

  const toggleDetails = (index) => {
      const position = details.indexOf(index)
      let newDetails = details.slice()
      if (position !== -1) {
        newDetails.splice(position, 1)
      } else {
        newDetails = [...details, index]
      }
      setDetails(newDetails)
  }


  const fields = [
      { key: 'name', _style: { width: '40%'} },
      'registered',
      { key: 'role', _style: { width: '20%'} },
      { key: 'status', _style: { width: '20%'} },
      {
        key: 'show_details',
        label: '',
        _style: { width: '1%' },
        sorter: false,
        filter: false
      }
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
*/

 /*
 <CDataTable
       items={usersData}
       fields={fields}
       columnFilter
       tableFilter
       footer
       itemsPerPageSelect
       itemsPerPage={5}
       hover
       sorter
       pagination
       scopedSlots = {{
         'status':
           (item)=>(
             <td>
               <CBadge color={getBadge(item.status)}>
                 {item.status}
               </CBadge>
             </td>
           ),
         'show_details':
           (item, index)=>{
             return (
               <td className="py-2">
                 <CButton
                   color="primary"
                   variant="outline"
                   shape="square"
                   size="sm"
                   onClick={()=>{toggleDetails(index)}}
                 >
                   {details.includes(index) ? 'Hide' : 'Show'}
                 </CButton>
               </td>
               )
           },
         'details':
             (item, index)=>{
               return (
               <CCollapse show={details.includes(index)}>
                 <CCardBody>
                   <h4>
                     {item.username}
                   </h4>
                   <p className="text-muted">User since: {item.registered}</p>
                   <CButton size="sm" color="info">
                     User Settings
                   </CButton>
                   <CButton size="sm" color="danger" className="ml-1">
                     Delete
                   </CButton>
                 </CCardBody>
               </CCollapse>
             )
           }
       }}
     />
*/