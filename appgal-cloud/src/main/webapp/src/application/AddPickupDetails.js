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


class AddPickupDetails extends React.Component
{
    constructor(props) {
        super(props);
    }

    render() {
      return (
        <div>
            <CRow>
                <CCol xs="12" sm="6">
                  <CCard>
                    <CCardHeader>
                      Pickup Details
                    </CCardHeader>
                    <CCardBody>
                      <CRow>
                          <CCol>
                              <CFormGroup>
                                  <CLabel htmlFor="foodType">Food Type</CLabel>
                                  <CSelect custom name="foodType" id="foodType">
                                      <option value="VEG">VEG</option>
                                      <option value="NON_VEG">NON-VEG</option>
                                  </CSelect>
                              </CFormGroup>
                          </CCol>
                      </CRow>
                      <CRow>
                          <CCol>
                            <CFormGroup>
                                <CLabel htmlFor="file-multiple-input" variant="custom-file">
                                    Food Picture...
                                </CLabel>
                            </CFormGroup>
                          </CCol>
                      </CRow>
                    </CCardBody>
                  </CCard>
                </CCol>
            </CRow>
        </div>
      )
    }
}

export default AddPickupDetails