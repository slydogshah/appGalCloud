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
  console.log("PATH: "+location.pathname);
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
                  <CHeaderNavLink to="/dashboard">Dashboard</CHeaderNavLink>
                </CHeaderNavItem>
              </CHeaderNav>

              <CHeaderNav className="px-3">
                <TheHeaderDropdown />
              </CHeaderNav>

              <CSubheader
                      style={{
                        background: '#2d247f',
                      }}
                      className="px-3 justify-content-between"
                    >
                      <CBreadcrumbRouter
                        className="border-0 c-subheader-nav m-0 px-0 px-md-3 "
                        routes={routes}
                      />
                    </CSubheader>
            </CHeader>
      );
  }
}

export default TheHeader
