import React, { useEffect, useState, createRef } from 'react'
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
  CDropdownItem
} from '@coreui/react'
import WidgetsBrand from './WidgetsBrand'
import WidgetsDropdown from './WidgetsDropdown'
import Dash from './Dash'

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
    alert("Bhenchod");
}

const Widgets = () => {
  return (
      <>
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
