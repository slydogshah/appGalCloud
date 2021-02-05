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

//<DropOffOrgsView dropOffOrgs={this.props.location.state.dropOffOrgs} history={this.props.history}/>

/*const components = []
              for (const [index, value] of this.props.dropOffOrgs.entries()) {
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
                                              <CDropdown>
                                                <CDropdownToggle color="transparent">
                                                  <CIcon name="cil-settings"/>
                                                </CDropdownToggle>
                                                <CDropdownMenu className="pt-0" placement="bottom-end">
                                                  <CDropdownItem onClick={this.handleConfirm}>Confirm</CDropdownItem>
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
              )*/
const WaitOnData = ({state, handleConfirm}) => {
    if (state.data === null) {
          return <p>Loading...</p>;
    }

    return (
          <>
            <div>Bindaas Bhiddu...I_LOUUUUUE YOU....</div>
          </>
    )
}


class DropOffOrgsView extends React.Component {
    constructor(props) {
        super(props);
        console.log("DropOffOrgsView : "+JSON.stringify(this.props));
        this.handleConfirm = this.handleConfirm.bind(this);

        this.state = {data: null};
        this.renderMyData();
    }

    renderMyData(){
            const apiUrl = 'http://localhost:8080/notification/dropOffOrgs/?orgId='+'microsoft';
            axios.get(apiUrl).then((response) => {
                this.setState({data: response.data});
            });
    }

    handleConfirm(event)
    {
      const apiUrl = 'http://localhost:8080/notification/schedulePickup/';
      axios.post(apiUrl,{orgId: 'microsoft'}).then((response) => {
        this.props.history.push({
          pathname: "/dashboard",
          state: response.data
        });
      });
    }

    render()
    {
        return (
                <>
                  <div>
                    <WaitOnData state={this.state} handleConfirm={this.handleConfirm} />
                  </div>
                </>
              )
    }
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
        const apiUrl = 'http://localhost:8080/notification/dropOffOrgs/?orgId='+'microsoft';
        axios.get(apiUrl).then((response) => {
            this.setState({data: response.data});
        });
    }


    render() {
      return (
        <>
          <div>
            <WaitOnData state={this.state} handleConfirm={this.handleConfirm} />
          </div>
        </>
      )
    }
}

export default SchedulePickup