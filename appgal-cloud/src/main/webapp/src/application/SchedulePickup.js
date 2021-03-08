import React from 'react'
import axios from 'axios'
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

const WaitOnData = ({state}) => {
    if (state.data === null) {
          return <p>Loading...</p>;
    }

    const components = []
                  for (const [index, value] of state.data.dropOffOrgs.entries()) {
                      components.push(
                           <CRow>
                                     <CCol>
                                     <CCardGroup className="mb-4">
                                            <CWidgetDropdown
                                                      color="gradient-primary"
                                                      header={value.orgName}
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
        console.log("SchedulePickup: "+JSON.stringify(this.props.location.state));
        this.state = {data: null};
        this.renderMyData();
    }

    renderMyData(){
        //TODO: unmock
        const apiUrl = window.location.protocol +"//"+process.env.WDS_SOCKET_HOST+"/notification/dropOffOrgs/?orgId='+'microsoft'";
        axios.get(apiUrl).then((response) => {
            this.setState({data: response.data});
        });
    }


    render() {
      return (
        <div>
            <WaitOnData state={this.state}/>
        </div>
      )
    }
}

export default SchedulePickup