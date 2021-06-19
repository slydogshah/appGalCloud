import React from 'react'
import ReactDOM from 'react-dom';
import axios from 'axios'
import { withRouter } from "react-router";
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

import { makeStyles } from "@material-ui/core/styles";
import GridItem from "../components/Grid/GridItem.js";
import GridContainer from "../components/Grid/GridContainer.js";
import CustomInput from "../components/CustomInput/CustomInput.js";
import Button from "../components/CustomButtons/Button.js";
import Card from "../components/Card/Card.js";
import CardHeader from "../components/Card/CardHeader.js";
import CardAvatar from "../components/Card/CardAvatar.js";
import CardBody from "../components/Card/CardBody.js";
import CardFooter from "../components/Card/CardFooter.js";
import DonutLargeOutlinedIcon from '@material-ui/icons/DonutLargeOutlined';
import Snackbar from "../components/Snackbar/Snackbar.js";

const styles = {
  cardCategoryWhite: {
    color: "rgba(255,255,255,.62)",
    margin: "0",
    fontSize: "14px",
    marginTop: "0",
    marginBottom: "0"
  },
  cardTitleWhite: {
    color: "#FFFFFF",
    marginTop: "0px",
    minHeight: "auto",
    fontWeight: "300",
    fontFamily: "'Roboto', 'Helvetica', 'Arial', sans-serif",
    marginBottom: "3px",
    textDecoration: "none"
  }
};

const useStyles = makeStyles(styles);

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


    store.setState(state => ({
                          ...state,
                          upload: thumb
                        }));

    return (<img src={thumb}
      alt={file.name}
      className="img-thumbnail mt-2"
      height={200}
      width={200} />);
  }
}

function RenderForm({state,props})
{
    const classes = useStyles();
    const renderForm = ({ values, handleSubmit, setFieldValue }) => {
                                           return (
                                             <>
                                             <form onSubmit={handleSubmit}>
                                             <div>
                                               <GridContainer>
                                                 <GridItem xs={12} sm={12} md={8}>
                                                   <Card>
                                                     <CardHeader color="primary">
                                                       <h4 className={classes.cardTitleWhite}>Schedule A Pickup</h4>
                                                     </CardHeader>
                                                     <CardBody>
                                                       <GridContainer>
                                                         <GridItem xs={12} sm={12} md={4}>
                                                           <CLabel htmlFor="ccmonth">Preferred Pickup Time</CLabel>
                                                            <CSelect custom name="time" id="ccmonth" onChange={(event) => {
                                                                const target = event.target;
                                                                const value = target.value;
                                                                const name = target.name;
                                                                state.[name] = value;
                                                            }}>
                                                              <option value="0">--Today--</option>
                                                              <option value="0:0">12:00 AM</option>
                                                              <option value="0:1">1:00 AM</option>
                                                              <option value="0:2">2:00 AM</option>
                                                              <option value="0:3">3:00 AM</option>
                                                              <option value="0:4">4:00 AM</option>
                                                              <option value="0:5">5:00 AM</option>
                                                              <option value="0:6">6:00 AM</option>
                                                              <option value="0:7">7:00 AM</option>
                                                              <option value="0:8">8:00 AM</option>
                                                              <option value="0:9">9:00 AM</option>
                                                              <option value="0:10">10:00 AM</option>
                                                              <option value="0:11">11:00 AM</option>
                                                              <option value="0:12">12:00 PM</option>
                                                              <option value="0:13">1:00 PM</option>
                                                              <option value="0:14">2:00 PM</option>
                                                              <option value="0:15">3:00 PM</option>
                                                              <option value="0:16">4:00 PM</option>
                                                              <option value="0:17">5:00 PM</option>
                                                              <option value="0:18">6:00 PM</option>
                                                              <option value="0:19">7:00 PM</option>
                                                              <option value="0:20">8:00 PM</option>
                                                              <option value="0:21">9:00 PM</option>
                                                              <option value="0:22">10:00 PM</option>
                                                              <option value="0:23">11:00 PM</option>
                                                              <option value="1">--Tomorrow--</option>
                                                              <option value="1:0">12:00 AM</option>
                                                            <option value="1:1">1:00 AM</option>
                                                            <option value="1:2">2:00 AM</option>
                                                            <option value="1:3">3:00 AM</option>
                                                            <option value="1:4">4:00 AM</option>
                                                            <option value="1:5">5:00 AM</option>
                                                            <option value="1:6">6:00 AM</option>
                                                            <option value="1:7">7:00 AM</option>
                                                            <option value="1:8">8:00 AM</option>
                                                            <option value="1:9">9:00 AM</option>
                                                            <option value="1:10">10:00 AM</option>
                                                            <option value="1:11">11:00 AM</option>
                                                            <option value="1:12">12:00 PM</option>
                                                            <option value="1:13">1:00 PM</option>
                                                            <option value="1:14">2:00 PM</option>
                                                            <option value="1:15">3:00 PM</option>
                                                            <option value="1:16">4:00 PM</option>
                                                            <option value="1:17">5:00 PM</option>
                                                            <option value="1:18">6:00 PM</option>
                                                            <option value="1:19">7:00 PM</option>
                                                            <option value="1:20">8:00 PM</option>
                                                            <option value="1:21">9:00 PM</option>
                                                            <option value="1:22">10:00 PM</option>
                                                            <option value="1:23">11:00 PM</option>
                                                         </CSelect>
                                                         </GridItem>
                                                       </GridContainer>
                                                       <GridContainer>
                                                         <GridItem xs={12} sm={12} md={6}>
                                                            <CLabel htmlFor="foodType">Food Type</CLabel>
                                                           <CSelect custom name="foodType" id="foodType" onChange={(event) => {
                                                                const target = event.target;
                                                                const value = target.value;
                                                                const name = target.name;
                                                                state.[name] = value;
                                                           }}>
                                                               <option value="0">--Select--</option>
                                                               <option value="VEG">VEG</option>
                                                               <option value="NON_VEG">NON-VEG</option>
                                                           </CSelect>
                                                         </GridItem>
                                                       </GridContainer>
                                                       <GridContainer>
                                                         <GridItem xs={12} sm={12} md={6}>
                                                            <div className="form-group">
                                                              <label for="file">File upload</label>
                                                              <input id="file" name="file" type="file" onChange={(event) => {
                                                                const fileUpload = event.currentTarget.files[0];
                                                                setFieldValue("file", fileUpload);
                                                                state.upload = fileUpload;
                                                              }} className="form-control" />
                                                              <Thumb file={values.file}/>
                                                            </div>
                                                         </GridItem>
                                                       </GridContainer>
                                                     </CardBody>
                                                     <CardFooter>
                                                       <Button color="primary" onClick={(e) => {
                                                                //show progress bar
                                                                var element = (
                                                                        <Snackbar
                                                                          place="tc"
                                                                          color="info"
                                                                          icon={DonutLargeOutlinedIcon}
                                                                          message="Starting a Pickup Request...."
                                                                          open={true}
                                                                        />
                                                                );
                                                                ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                                                                ReactDOM.render(element,document.getElementById('progress'));
                                                                const payload = {
                                                                            orgId: store.getState().sourceOrg.orgId,
                                                                            time: state.time,
                                                                            foodPic:store.getState().upload,
                                                                            foodType: state.foodType
                                                                        };



                                                                const apiUrl = window.location.protocol +"//"+window.location.hostname+"/notification/addPickupDetails/";
                                                                axios.post(apiUrl,payload).then((response) => {
                                                                    ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                                                                    props.history.push({
                                                                                    pathname: "/dropOffOptions",
                                                                                    state: { data: response.data }
                                                                                  });
                                                                }).catch(err => {
                                                                            ReactDOM.unmountComponentAtNode(document.getElementById('progress'));
                                                                            var element = (
                                                                                                                                     <Snackbar
                                                                                                                                       place="tc"
                                                                                                                                       color="danger"
                                                                                                                                       icon={DonutLargeOutlinedIcon}
                                                                                                                                       message="500: Unknown System Error...."
                                                                                                                                       open={true}
                                                                                                                                       close
                                                                                                                                       closeNotification={() => {
                                                                                                                                         ReactDOM.unmountComponentAtNode(document.getElementById('unknown_error'));
                                                                                                                                       }}
                                                                                                                                     />
                                                                                                                             );
                                                                                                                             ReactDOM.unmountComponentAtNode(document.getElementById('unknown_error'));
                                                                                                                             ReactDOM.render(element,document.getElementById('unknown_error'));
                                                                });
                                                         }}>Update</Button>
                                                     </CardFooter>
                                                   </Card>
                                                 </GridItem>
                                               </GridContainer>
                                             </div>
                                          </form>
                                        </>
                                   );
                               }



            return (
              <>
              <br/><br/><br/><br/><br/><br/>
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
                  render={renderForm}
                />
              </div>
            </>
         );
}


class AddPickupDetails extends React.Component
{
    constructor(props)
    {
        super(props);
        this.state = {
            foodType: '',
            file: null,
            loading: false,
            thumb: undefined,
            upload: null
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleDetails = this.handleDetails.bind(this);
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        this.setState({
                  [name]: value
        });
    }

    handleDetails(event) {
        const payload = {
            orgId: store.getState().sourceOrg.orgId,
            foodType: this.state.foodType,
            foodPic: store.getState().upload
        };

        const apiUrl = window.location.protocol +"//"+window.location.hostname+"/notification/addPickupDetails/";
        axios.post(apiUrl,payload).then((response) => {
              this.props.history.push({
                pathname: "/dropOffOptions",
                state: { data: response.data }
              });
        }).catch(err => {

            //TODO
            console.log(JSON.stringify(err));
        });
    }

    render() {
        return(
            <>
                <div id="unknown_error"/>
                <div id="progress"/>
                <div id="parent">
                    <RenderForm state={this.state} props={this.props}/>
                </div>
            </>
        );
    }

}

export default withRouter(AddPickupDetails)