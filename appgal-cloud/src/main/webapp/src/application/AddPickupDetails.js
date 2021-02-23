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
                          Credit Card
                          <small> Form</small>
                          <DocsLink name="-Input"/>
                        </CCardHeader>
                        <CCardBody>
                          <CRow>
                            <CCol xs="12">
                              <CFormGroup>
                                <CLabel htmlFor="name">Name</CLabel>
                                <CInput id="name" placeholder="Enter your name" required />
                              </CFormGroup>
                            </CCol>
                          </CRow>
                          <CRow>
                            <CCol xs="12">
                              <CFormGroup>
                                <CLabel htmlFor="ccnumber">Credit Card Number</CLabel>
                                <CInput id="ccnumber" placeholder="0000 0000 0000 0000" required />
                              </CFormGroup>
                            </CCol>
                          </CRow>
                          <CRow>
                            <CCol xs="4">
                              <CFormGroup>
                                <CLabel htmlFor="ccmonth">Month</CLabel>
                                <CSelect custom name="ccmonth" id="ccmonth">
                                  <option value="VEG">VEG</option>
                                  <option value="NON_VEG">NON-VEG</option>
                                </CSelect>
                              </CFormGroup>
                            </CCol>
                            <CCol xs="4">
                              <CFormGroup>
                                <CLabel htmlFor="ccyear">Year</CLabel>
                                <CSelect custom name="ccyear" id="ccyear">
                                  <option>2017</option>
                                  <option>2018</option>
                                  <option>2019</option>
                                  <option>2020</option>
                                  <option>2021</option>
                                  <option>2022</option>
                                  <option>2023</option>
                                  <option>2024</option>
                                  <option>2025</option>
                                  <option>2026</option>
                                </CSelect>
                              </CFormGroup>
                            </CCol>
                            <CCol md="3">
                            <CLabel>Multiple File input</CLabel>
                          </CCol>
                          <CCol xs="12" md="9">
                            <CInputFile
                              id="file-multiple-input"
                              name="file-multiple-input"
                              multiple
                              custom
                            />
                            <CLabel htmlFor="file-multiple-input" variant="custom-file">
                              Choose Files...
                            </CLabel>
                          </CCol>
                          </CRow>
                        </CCardBody>
                      </CCard>
                    </CCol>
                    <CCol xs="12" sm="6">
                      <CCard>
                        <CCardHeader>
                          Company
                          <small> Form</small>
                        </CCardHeader>
                        <CCardBody>
                          <CFormGroup>
                            <CLabel htmlFor="company">Company</CLabel>
                            <CInput id="company" placeholder="Enter your company name" />
                          </CFormGroup>
                          <CFormGroup>
                            <CLabel htmlFor="vat">VAT</CLabel>
                            <CInput id="vat" placeholder="DE1234567890" />
                          </CFormGroup>
                          <CFormGroup>
                            <CLabel htmlFor="street">Street</CLabel>
                            <CInput id="street" placeholder="Enter street name" />
                          </CFormGroup>
                          <CFormGroup row className="my-0">
                            <CCol xs="8">
                              <CFormGroup>
                                <CLabel htmlFor="city">City</CLabel>
                                <CInput id="city" placeholder="Enter your city" />
                              </CFormGroup>
                            </CCol>
                            <CCol xs="4">
                              <CFormGroup>
                                <CLabel htmlFor="postal-code">Postal Code</CLabel>
                                <CInput id="postal-code" placeholder="Postal Code" />
                              </CFormGroup>
                            </CCol>
                          </CFormGroup>
                          <CFormGroup>
                            <CLabel htmlFor="country">Country</CLabel>
                            <CInput id="country" placeholder="Country name" />
                          </CFormGroup>
                        </CCardBody>
                      </CCard>
                    </CCol>
                  </CRow>
        </div>
      )
    }
}

export default AddPickupDetails