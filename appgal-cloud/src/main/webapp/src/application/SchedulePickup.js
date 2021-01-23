import React from 'react'
import {
  CBadge,
  CCard,
  CCardBody,
  CCardHeader,
  CCol,
  CDataTable,
  CButton,
    CCollapse,
    CDropdownItem,
    CDropdownMenu,
    CDropdownToggle,
    CFade,
    CForm,
    CFormGroup,
    CFormText,
    CValidFeedback,
    CInvalidFeedback,
    CTextarea,
    CInput,
    CInputFile,
    CInputCheckbox,
    CInputRadio,
    CInputGroup,
    CInputGroupAppend,
    CInputGroupPrepend,
    CDropdown,
    CInputGroupText,
    CLabel,
    CSelect,
    CRow,
    CSwitch,
    CCardFooter,
    CProgress,
    CCardGroup,
    CWidgetDropdown
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import WidgetsDropdown from './WidgetsDropdown'
import ChartLineSimple from '../views/charts/ChartLineSimple'

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

const DroppOfOrgsView = ({dropOffOrgs}) => {
    const components = []
    for (const [index, value] of dropOffOrgs.entries()) {
        components.push(
             <CRow>
                       <CCol>
                       <CCardGroup className="mb-4">
                              <CWidgetDropdown
                                        color="gradient-primary"
                                        text="Schedule Pickup"
                                        footerSlot={
                                          <ChartLineSimple
                                            pointed
                                            className="c-chart-wrapper mt-3 mx-3"
                                            style={{height: '70px'}}
                                            dataPoints={[65, 59, 84, 84, 51, 55, 40]}
                                            pointHoverBackgroundColor="primary"
                                            label="Members"
                                            labels="months"
                                          />
                                        }
                                      >
                                    <CDropdown>
                                      <CDropdownToggle color="transparent">
                                        <CIcon name="cil-settings"/>
                                      </CDropdownToggle>
                                      <CDropdownMenu className="pt-0" placement="bottom-end">
                                        <CDropdownItem onClick="">Confirm</CDropdownItem>
                                      </CDropdownMenu>
                                    </CDropdown>
                                  </CWidgetDropdown>
                       </CCardGroup>
                       </CCol>
                       </CRow>
         )
    }
    return(
        <div>
            {components}
        </div>
    )
}

class SchedulePickup extends React.Component
{
    constructor(props) {
        super(props);
        console.log("Constructor: "+JSON.stringify(this.props.location.state));
    }
    render() {
      return (
        <>
          <DroppOfOrgsView dropOffOrgs={this.props.location.state.dropOffOrgs} />
        </>
      )
    }
}

export default SchedulePickup

/*
<CRow>
              <CCol xs="12" md="4">
                        <CCard>
                          <CCardHeader>
                            Schedule Pickup
                          </CCardHeader>
                          <CCardBody>
                            <CForm action="" method="post" className="form-horizontal">
                              <CFormGroup row>
                                <CCol md="12">
                                  <CInputGroup>
                                    <CInputGroupPrepend>
                                      <CInputGroupText>
                                        <CIcon name="cil-user" />
                                      </CInputGroupText>
                                    </CInputGroupPrepend>
                                    <CLabel htmlFor="ccmonth">{value.orgName}</CLabel>
                                  </CInputGroup>
                                </CCol>
                              </CFormGroup>
                            </CForm>
                          </CCardBody>
                          <CCardFooter>
                            <CButton type="submit" size="sm" color="success"><CIcon name="cil-scrubber" /> Submit</CButton>
                          </CCardFooter>
                        </CCard>
                      </CCol>
           </CRow>*/