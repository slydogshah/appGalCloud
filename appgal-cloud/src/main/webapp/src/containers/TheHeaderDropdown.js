import React, { useState } from 'react'
import ReactDOM from 'react-dom';
import { withRouter } from "react-router";
import {
  CBadge,
  CDropdown,
  CDropdownItem,
  CDropdownMenu,
  CDropdownToggle,
  CButton,
  CImg,
  CLink
} from '@coreui/react'
import CIcon from '@coreui/icons-react'

//TODO: hook the actual logout routine

class TheHeaderDropdown extends React.Component {
   constructor(props) {
       super(props);
   }

  render(){
      return (
        <CDropdown
          inNav
          className="c-header-nav-items mx-2"
          direction="down"
        >
          <CDropdownToggle className="c-header-nav-link" caret={false}>
            <div className="c-avatar">
              <CImg
                src={'images/dropdown-arrow.svg'}
                className="c-avatar-img"
                alt="options"
              />
            </div>
          </CDropdownToggle>
          <CDropdownMenu className="pt-0" placement="bottom-end">
            <CDropdownItem>
              <CIcon name="cil-lock-locked" className="mfe-2" />
              <CLink to="/home" className="btn btn-primary">Home</CLink>
            </CDropdownItem>
            <CDropdownItem>
              <CIcon name="cil-user" className="mfe-2" />
              <CLink to="/profile" className="btn btn-primary">Profile</CLink>
            </CDropdownItem>
            <CDropdownItem divider />
            <CDropdownItem>
              <CIcon name="cil-lock-locked" className="mfe-2" />
              <CLink to="/" className="btn btn-primary">Logout</CLink>
            </CDropdownItem>
          </CDropdownMenu>
        </CDropdown>
      );
  }
}

export default TheHeaderDropdown
