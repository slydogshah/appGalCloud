import React from 'react'
import { useSelector, useDispatch } from 'react-redux'
import {
  CHeader,
  CToggler,
  CHeaderBrand,
  CHeaderNav,
  CHeaderNavItem,
  CHeaderNavLink,
  CSubheader,
  CBreadcrumbRouter,
  CLink,
} from '@coreui/react'
import CIcon from '@coreui/icons-react'

// routes config
import routes from '../routes'

import {
  TheHeaderDropdown,
  TheHeaderDropdownMssg,
  TheHeaderDropdownNotif,
  TheHeaderDropdownTasks,
} from './index'
import { useLocation } from 'react-router-dom'

const TheHeader = () => {
  const location = useLocation();
  if(location.pathname == "/")
  {
    return(
        <CHeader withSubheader>
          <CHeaderBrand className="mx-auto d-lg-none" to="/">
            <CIcon name="logo" height="48" alt="Logo" />
          </CHeaderBrand>
        </CHeader>
    );
  }
  else
  {
      return (
            <CHeader withSubheader>
              <CHeaderBrand className="mx-auto d-lg-none" to="/">
                <CIcon name="logo" height="48" alt="Logo" />
              </CHeaderBrand>

              <CHeaderNav className="d-md-down-none mr-auto">
                <CHeaderNavItem className="px-3">
                  <CHeaderNavLink to="/home">Home</CHeaderNavLink>
                </CHeaderNavItem>
              </CHeaderNav>

              <CHeaderNav className="px-3">
                <TheHeaderDropdown />
              </CHeaderNav>
            </CHeader>
      );
  }
}

export default TheHeader
