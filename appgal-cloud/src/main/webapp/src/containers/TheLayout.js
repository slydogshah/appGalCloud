import React from 'react'
import { TheContent, TheFooter, TheHeader } from './index'

const TheLayout = () => {
  return (
    <div className="c-app c-default-layout">
      <TheHeader/>
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
