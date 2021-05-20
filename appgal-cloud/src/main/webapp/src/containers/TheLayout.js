import React from 'react'
import { TheContent, TheFooter, TheHeader } from './index'

const TheLayout = (props) => {
  return (
    <div className="c-app c-default-layout">
         <TheHeader props={props} />
      <div className="c-wrapper">
        <div
          style={{
            background: '#a8a7c7',
          }}
          className="c-body"
        >
          <TheContent />
        </div>
        {/* <TheFooter/> */}
      </div>
    </div>
  )
}

export default TheLayout
