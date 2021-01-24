import React, { lazy } from 'react'
import {
  CBadge,
  CButton,
  CButtonGroup,
  CCard,
  CCardBody,
  CCardFooter,
  CCardHeader,
  CCol,
  CProgress,
  CRow,
  CCallout
} from '@coreui/react'
import CIcon from '@coreui/icons-react'

const InProgressTransactionView = ({inProgress}) => {
    const txs = []
    for (const [index, value] of inProgress.entries()) {
        txs.push(
             <div className="progress-group mb-4">
                    <div className="progress-group-prepend">
                      <span className="progress-group-text">
                        {value.state}
                      </span>
                    </div>
                    <div className="progress-group-bars">
                      <CProgress className="progress-xs" color="info" value="34" />
                      <CProgress className="progress-xs" color="danger" value="78" />
                    </div>
              </div>
         )
    }
    return(
        <div>
            {txs}
        </div>
    )
}

const DropOffDash = ({inProgress}) => {
  return (
    <>
      <CRow>
        <CCol>
          <CCard>
            <CCardHeader>
              Deliveries In-Progress
            </CCardHeader>
            <CCardBody>
              <CRow>
                <CCol xs="12" md="6" xl="6">

                  <CRow>
                    <CCol sm="6">
                      <CCallout color="info">
                        <small className="text-muted">In-Progress</small>
                        <br />
                        <strong className="h4">{inProgress.length}</strong>
                      </CCallout>
                    </CCol>
                  </CRow>

                  <hr className="mt-0" />
                  <InProgressTransactionView inProgress={inProgress} />
                </CCol>
              </CRow>
            </CCardBody>
          </CCard>
        </CCol>
      </CRow>
    </>
  )
}

export default DropOffDash
