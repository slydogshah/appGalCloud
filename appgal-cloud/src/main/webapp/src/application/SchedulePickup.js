import React from 'react'
import {
  CBadge,
  CCard,
  CCardBody,
  CCardHeader,
  CCol,
  CDataTable,
  CRow
} from '@coreui/react'

import { DocsLink } from 'src/reusable'

import usersData from 'src/views/users/UsersData'

const getBadge = status => {
  switch (status) {
    case 'Active': return 'success'
    case 'Inactive': return 'secondary'
    case 'Pending': return 'warning'
    case 'Banned': return 'danger'
    default: return 'primary'
  }
}
const fields = ['name','registered', 'role', 'status']


const sourceOrgFields = [{key:'orgId',label:'Org'},
{key:'orgName',label:'Name'},
{key:'orgContactEmail',label:'Contact'}]

class SchedulePickup extends React.Component
{
    constructor(props) {
        super(props);
        console.log("Constructor: "+JSON.stringify(this.props.location.state));
    }
    render() {
      const sourceOrgs = this.props.location.state.sourceOrgs;
      return (
        <>
          <CRow>
            <CCol xs="12" lg="6">
              <CCard>
                <CCardHeader>
                  Simple Table
                  <DocsLink name="CModal"/>
                </CCardHeader>
                <CCardBody>
                <CDataTable
                  items={sourceOrgs}
                  fields={sourceOrgFields}
                />
                </CCardBody>
              </CCard>
            </CCol>

            <CCol xs="12" lg="6">
              <CCard>
                <CCardHeader>
                  Striped Table
                </CCardHeader>
                <CCardBody>
                <CDataTable
                  items={sourceOrgs}
                  fields={sourceOrgFields}
                  striped
                />
                </CCardBody>
              </CCard>
            </CCol>
          </CRow>

          <CRow>

            <CCol xs="12" lg="6">
              <CCard>
                <CCardHeader>
                  Condensed Table
                </CCardHeader>
                <CCardBody>
                <CDataTable
                  items={sourceOrgs}
                  fields={sourceOrgFields}
                  size="sm"
                />
                </CCardBody>
              </CCard>
            </CCol>

            <CCol xs="12" lg="6">
              <CCard>
                <CCardHeader>
                  Bordered Table
                </CCardHeader>
                <CCardBody>
                <CDataTable
                  items={sourceOrgs}
                  fields={sourceOrgFields}
                  bordered
                />
                </CCardBody>
              </CCard>
            </CCol>

          </CRow>

          <CRow>
            <CCol>
              <CCard>
                <CCardHeader>
                  Combined All Table
                </CCardHeader>
                <CCardBody>
                <CDataTable
                  items={sourceOrgs}
                  fields={sourceOrgFields}
                  hover
                  striped
                  bordered
                  size="sm"
                />
                </CCardBody>
              </CCard>
            </CCol>
          </CRow>
            <CRow>
            <CCol>
              <CCard>
                <CCardHeader>
                  Combined All dark Table
                </CCardHeader>
                <CCardBody>
                <CDataTable
                  items={sourceOrgs}
                  fields={sourceOrgFields}
                  dark
                  hover
                  striped
                  bordered
                  size="sm"
                />
                </CCardBody>
              </CCard>
            </CCol>
          </CRow>
        </>
      )
    }
}

export default SchedulePickup