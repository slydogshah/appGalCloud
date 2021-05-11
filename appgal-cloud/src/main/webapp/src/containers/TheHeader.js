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
  CImg,
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

  //console.log("LOCATION: "+location.pathname);


  if(location.pathname == "/")
  {
    return(
        <>
            <CHeader withSubheader>
                          <div class="logo-header">
                              <a href="index.html"
                                ><img src="images/jen-logo.PNG" alt=""
                              /></a>
                          </div>
                       </CHeader>
        </>
    );
  }
  else
  {
      return (
            <CHeader withSubheader>
              <div class="logo-header">
                  <a href="index.html"
                    ><img src="images/jen-logo.PNG" alt=""
                  /></a>
              </div>

              <CHeaderNav className="d-md-down-none mr-auto">
                  <CHeaderNavItem className="px-3">
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
