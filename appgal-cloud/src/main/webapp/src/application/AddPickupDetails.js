import React from 'react'
import axios from 'axios'
import {
  CBadge,
  CCard,
  CCardBody,
  CCardHeader,
  CCol,
  CDataTable,
  CButton,
    CCollapse,
    CDropdownItem,
    CDropdownMenu,
    CDropdownToggle,
    CFade,
    CForm,
    CFormGroup,
    CFormText,
    CValidFeedback,
    CInvalidFeedback,
    CTextarea,
    CInput,
    CInputFile,
    CInputCheckbox,
    CInputRadio,
    CInputGroup,
    CInputGroupAppend,
    CInputGroupPrepend,
    CDropdown,
    CInputGroupText,
    CLabel,
    CSelect,
    CRow,
    CSwitch,
    CCardFooter,
    CProgress,
    CCardGroup,
    CContainer,
    CWidgetDropdown
} from '@coreui/react'
import { Formik } from "formik";
import CIcon from '@coreui/icons-react'
import WidgetsDropdown from './WidgetsDropdown'
import ChartLineSimple from '../views/charts/ChartLineSimple'

import { DocsLink } from 'src/reusable'
import { AppContext,store} from "./AppContext"

class Thumb extends React.Component {
  state = {
    loading: false,
    thumb: undefined,
  };

  componentWillReceiveProps(nextProps) {
    if (!nextProps.file) { return; }

    this.setState({ loading: true }, () => {
      let reader = new FileReader();

      reader.onloadend = () => {
        this.setState({ loading: false, thumb: reader.result });
      };

      reader.readAsDataURL(nextProps.file);
    });
  }

  render() {
    const { file } = this.props;
    const { loading, thumb } = this.state;

    if (!file) { return null; }

    if (loading) { return <p>loading...</p>; }

    return (<img src={thumb}
      alt={file.name}
      className="img-thumbnail mt-2"
      height={200}
      width={200} />);
  }
}


class AddPickupDetails extends React.Component
{
    /*constructor(props) {
        super(props);
        this.state = {
            foodType: '',
            file: null,
            loading: false,
            thumb: undefined
        };
        this.handleDetails = this.handleDetails.bind(this);
    }

    handleDetails(event) {
        alert(
          JSON.stringify(this.state)
        );
        const orgId = store.getState().sourceOrg.orgId;
        const apiUrl = window.location.protocol +"//"+window.location.hostname+"/notification/dropOffOrgs/?orgId="+orgId;
        axios.get(apiUrl).then((response) => {
              this.props.history.push({
                pathname: "/dropOffOptions",
                state: { data: response.data }
              });
        });
    }

    render() {
      return (
        <Formik initialValues={{ file: null }}
        onSubmit={(values) => {
                    alert(
                      JSON.stringify(
                        {
                          fileName: values.file.name,
                          type: values.file.type,
                          size: `${values.file.size} bytes`
                        },
                        null,
                        2
                      )
                    );
                  }}
        render={(values,handleSubmit,setFieldValue) => {
            return (
                <form onSubmit={handleSubmit}>
                <div className="c-app c-default-layout flex-row align-items-center">
                  <CContainer>
                    <CRow className="justify-content-center">
                      <CCol md="9" lg="7" xl="6">
                        <CCard className="mx-4">
                          <CCardBody className="p-4">
                              <h1>Add Pickup Details</h1>
                              <CLabel htmlFor="foodType">Food Type</CLabel>
                              <CInputGroup className="mb-3">
                                <CSelect custom name="foodType" id="foodType">
                                    <option value="0">--Select--</option>
                                    <option value="VEG">VEG</option>
                                    <option value="NON_VEG">NON-VEG</option>
                                </CSelect>
                              </CInputGroup>
                              <CInputGroup className="mb-3">
                                <CInputGroupPrepend>
                                  <CInputGroupText>
                                    <CIcon name="cil-lock-locked" />
                                  </CInputGroupText>
                                </CInputGroupPrepend>
                                <div className="form-group">
                                    <label for="file">File upload</label>
                                    <input id="file" name="file" type="file" onChange={async event => {
                                      await setFieldValue("file", event.currentTarget.files[0]);
                                    }} className="form-control" />
                                    <Thumb file={values.file} />
                                </div>
                              </CInputGroup>
                              <div id="errorAlert" />
                              <button type="submit" className="btn btn-primary">Send</button>
                          </CCardBody>
                        </CCard>
                      </CCol>
                    </CRow>
                  </CContainer>
                </div>
                </form>
            );
        }}/>
      )
    }*/

    constructor(props)
    {
        super(props);
        this.state = {
            foodType: '',
            file: null,
            loading: false,
            thumb: undefined
        };
        this.handleDetails = this.handleDetails.bind(this);
    }

    handleDetails(event) {
        /*alert(
          JSON.stringify(this.state)
        );
        const orgId = store.getState().sourceOrg.orgId;
        const apiUrl = window.location.protocol +"//"+window.location.hostname+"/notification/dropOffOrgs/?orgId="+orgId;
        axios.get(apiUrl).then((response) => {
              this.props.history.push({
                pathname: "/dropOffOptions",
                state: { data: response.data }
              });
        });*/
    }

    render() {
        return (
          <div className="container">
            <Formik
              initialValues={{ file: null }}
              onSubmit={(values) => {
                alert(
                  JSON.stringify(
                    {
                      fileName: values.file.name,
                      type: values.file.type,
                      size: `${values.file.size} bytes`
                    },
                    null,
                    2
                  )
                );
              }}
              render={({ values, handleSubmit, setFieldValue }) => {
                return (
                  <form onSubmit={handleSubmit}>
                    <div className="form-group">
                      <label for="file">File upload</label>
                      <input id="file" name="file" type="file" onChange={(event) => {
                        setFieldValue("file", event.currentTarget.files[0]);
                      }} className="form-control" />
                      <Thumb file={values.file} />
                    </div>
                    <button type="submit" className="btn btn-primary">submit</button>
                  </form>
                );
              }} />
          </div>
        );
    }

}

export default AddPickupDetails