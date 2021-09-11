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

import { AppContext,store} from "../application/AppContext"

const TheHeader = (props) => {
  const location = useLocation();
  const auth = store.getState().auth;




  if(location.pathname == "/")
  {
    return(
        <>
            <CHeader withSubheader>
                          <div class="logo-header">
                              <img src="images/jen-logo.PNG" alt=""
                                                            />
                          </div>
                       </CHeader>
        </>
    );
  }
  else if(auth === true)
  {
      return (
            <CHeader withSubheader>
              <div class="logo-header">
                  <img src="images/jen-logo.PNG" alt=""
                  />
              </div>

              <CHeaderNav className="d-md-down-none mr-auto">
                  <CHeaderNavItem className="px-3">
                  </CHeaderNavItem>
              </CHeaderNav>

              <CHeaderNav className="px-3">
                <TheHeaderDropdown props={props}/>
              </CHeaderNav>
           </CHeader>
      );
  }
  else{
   return (
               <CHeader withSubheader>
                 <div class="logo-header">
                     <img src="images/jen-logo.PNG" alt=""
                     />
                 </div>
              </CHeader>
         );
  }
}

export default TheHeader
