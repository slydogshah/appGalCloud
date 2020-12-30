import React from 'react'
import {
  CBadge,
  CCard,
  CCardBody,
  CCardHeader,
  CCol,
  CDataTable,
  CRow,
  CButton
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
{key:'orgContactEmail',label:'Contact'},
{key: 'producer',label: 'Producer'}
]

/*const sourceOrgFields = [{key:'orgId',label:'Org'},
{key:'orgName',label:'Name'},
{key:'orgContactEmail',label:'Contact'}
]*/

class Main extends React.Component
{
    constructor(props) {
        super(props);
        console.log("Constructor: "+JSON.stringify(this.props.location.state.sourceOrgs));
    }
    render() {
      const sourceOrgs = this.props.location.state.sourceOrgs;
      return (
        <>
        <template>
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
                >
                    <template>
                            <td class="py-2">
                              <CButton
                                color="primary"
                                variant="outline"
                                square
                                size="sm"
                                onClick="toggleDetails(item, index)"
                              >
                                {'Show'}
                              </CButton>
                            </td>
                          </template>
                </CDataTable>
                </CCardBody>
              </CCard>
            </CCol>
          </CRow>
         </template>
        </>
      )
    }
}

export default Main