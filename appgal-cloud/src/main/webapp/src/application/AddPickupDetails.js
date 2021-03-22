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
    CContainer,
    CWidgetDropdown
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import WidgetsDropdown from './WidgetsDropdown'
import ChartLineSimple from '../views/charts/ChartLineSimple'

import { DocsLink } from 'src/reusable'


class AddPickupDetails extends React.Component
{
    constructor(props) {
        super(props);
        this.handleDetails = this.handleDetails.bind(this);
    }

    handleDetails(event) {
        const apiUrl = window.location.protocol +"//"+window.location.hostname+"/notification/dropOffOrgs/?orgId=microsoft";
        axios.get(apiUrl).then((response) => {
              this.props.history.push({
                pathname: "/dropOffOptions",
                state: { data: response.data }
              });
        });
    }

    render() {
      return (
        <div className="c-app c-default-layout flex-row align-items-center">
                  <CContainer>
                    <CRow className="justify-content-center">
                      <CCol md="9" lg="7" xl="6">
                        <CCard className="mx-4">
                          <CCardBody className="p-4">
                            <CForm>
                              <h1>Add Pickup Details</h1>
                              <CLabel htmlFor="foodType">Food Type</CLabel>
                              <CInputGroup className="mb-3">
                                <CSelect custom name="foodType" id="foodType">
                                    <option value="VEG">VEG</option>
                                    <option value="NON_VEG">NON-VEG</option>
                                </CSelect>
                              </CInputGroup>
                              <CInputGroup className="mb-3">
                                <CInputGroupPrepend>
                                  <CInputGroupText>
                                    <CIcon name="cil-lock-locked" />
                                  </CInputGroupText>
                                </CInputGroupPrepend>
                                <CLabel htmlFor="file-multiple-input" variant="custom-file">
                                        Food Picture...
                                </CLabel>
                              </CInputGroup>
                              <CButton color="primary" className="px-4" onClick={this.handleDetails}>Send</CButton>
                              <div id="errorAlert" />
                            </CForm>
                          </CCardBody>
                        </CCard>
                      </CCol>
                    </CRow>
                  </CContainer>
                </div>
      )
    }
}

export default AddPickupDetails