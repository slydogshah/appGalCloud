import React from 'react'
import ReactDOM from 'react-dom';
import Select from 'react-select'
import { axios} from "../App"
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
    CAlert,
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

function RenderOption(key,value){
    return (
        <>
        <option value={key}>{value}</option>
        </>
      );
}

function RenderForm({state,props})
{
    const classes = useStyles();

    console.log(JSON.stringify(props.history.location.state.data));
    const options = props.history.location.state.data;
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
                                                            <Select
                                                                name="time"
                                                                id="time"
                                                                maxMenuHeight={220}
                                                                menuPlacement="auto"
                                                                options={options}
                                                                onChange={(event) => {
                                                                   state.['time'] = event.value;
                                                               }}
                                                            />
                                                         <div id="time_is_required"/>
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
                                                           <div id="food_type_is_required"/>
                                                         </GridItem>
                                                       </GridContainer>

                                                     </CardBody>
                                                     <CardFooter>
                                                       <Button color="primary" onClick={(e) => {
                                                                ReactDOM.unmountComponentAtNode(document.getElementById('time_is_required'));
                                                                ReactDOM.unmountComponentAtNode(document.getElementById('food_type_is_required'));
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
                                                                    if(err.response != null && err.response.status == 400)
                                                                    {
                                                                        if(err.response.data.timeIsRequired)
                                                                        {
                                                                            const element = (
                                                                                                  <CAlert
                                                                                                  color="warning"
                                                                                                  >
                                                                                                     'Preferred Pickup Time' is required
                                                                                                 </CAlert>
                                                                                              );
                                                                            ReactDOM.render(element,document.getElementById('time_is_required'));
                                                                        }
                                                                        if(err.response.data.foodTypeIsRequired)
                                                                        {
                                                                            const element = (
                                                                                                  <CAlert
                                                                                                  color="warning"
                                                                                                  >
                                                                                                     'Food Type' is required
                                                                                                 </CAlert>
                                                                                              );
                                                                            ReactDOM.render(element,document.getElementById('food_type_is_required'));
                                                                        }
                                                                    }
                                                                    else{

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
                                                                    }
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