import React, { useEffect, useState, createRef } from 'react'
import ReactDOM from 'react-dom';
import {
  CCardGroup,
  CCardFooter,
  CCol,
  CLink,
  CRow,
  CWidgetProgress,
  CWidgetIcon,
  CWidgetProgressIcon,
  CWidgetSimple,
  CWidgetBrand,
  CHeaderNavLink,
  CProgress,
  CNav,
  CNavLink,
  CWidgetDropdown,
  CDropdown,
  CDropdownMenu,
  CDropdownToggle,
  CDropdownItem,
  CAlert,
  CModal,
  CModalHeader,
  CModalTitle,
  CModalBody,
  CCard,
  CCardHeader,
  CCardBody,
  CFormGroup,
  CLabel,
  CInput,
  CSelect
} from '@coreui/react'
import WidgetsBrand from './WidgetsBrand'
import WidgetsDropdown from './WidgetsDropdown'
import Dash from './Dash'
import Modals from '../views/notifications/modals/Modals'

import classNames from 'classnames'
/*import {
  CRow,
  CCol,
  CCard,
  CCardHeader,
  CCardBody
} from '@coreui/react'
*/
import { rgbToHex } from '@coreui/utils'
import { DocsLink } from 'src/reusable'

import ChartLineSimple from '../views/charts/ChartLineSimple'
import ChartBarSimple from '../views/charts/ChartBarSimple'
import ThemeColor from '../views/theme/colors/Colors.js'

import CIcon from '@coreui/icons-react'

function schedulePickup()
{
    const element = (
                     <CModal
                       size="sm"
                       show="true"
                       color="success"
                     >
                       <CModalHeader closeButton>
                         <CModalTitle>Schedule A Pickup</CModalTitle>
                       </CModalHeader>
                       <CModalBody>
                            <CCard>
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
                                                  <option value="1">1</option>
                                                  <option value="2">2</option>
                                                  <option value="3">3</option>
                                                  <option value="4">4</option>
                                                  <option value="5">5</option>
                                                  <option value="6">6</option>
                                                  <option value="7">7</option>
                                                  <option value="8">8</option>
                                                  <option value="9">9</option>
                                                  <option value="10">10</option>
                                                  <option value="11">11</option>
                                                  <option value="12">12</option>
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
                                            <CCol xs="4">
                                              <CFormGroup>
                                                <CLabel htmlFor="cvv">CVV/CVC</CLabel>
                                                <CInput id="cvv" placeholder="123" required/>
                                              </CFormGroup>
                                            </CCol>
                                          </CRow>
                                        </CCardBody>
                                      </CCard>
                       </CModalBody>
                     </CModal>
                 );
   ReactDOM.unmountComponentAtNode(document.getElementById('schedulePickup'));
   ReactDOM.render(element,document.getElementById('schedulePickup'));
}

const Widgets = () => {
  return (
      <>
      <div id="schedulePickup"></div>
      <CRow>
      <CCol>
      <CCardGroup className="mb-4">
             <CWidgetDropdown
                       color="gradient-primary"
                       header="50"
                       text="Pickups In-Progress"
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
                       <CDropdownItem onClick={schedulePickup}>Schedule</CDropdownItem>
                       <CDropdownItem onClick={schedulePickup}>History</CDropdownItem>
                     </CDropdownMenu>
                   </CDropdown>
                 </CWidgetDropdown>
      </CCardGroup>
      </CCol>
      </CRow>
      <CRow>
            <CCol>
                <Dash/>
            </CCol>
            </CRow>
      </>
  )
}

export default Widgets
