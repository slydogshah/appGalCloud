import React from "react";
import { withRouter } from "react-router";
import axios from 'axios'
// @material-ui/core components
import { makeStyles } from "@material-ui/core/styles";
import InputLabel from "@material-ui/core/InputLabel";
// core components
import GridItem from "../components/Grid/GridItem.js";
import GridContainer from "../components/Grid/GridContainer.js";
import CustomInput from "../components/CustomInput/CustomInput.js";
import Button from "../components/CustomButtons/Button.js";
import Card from "../components/Card/Card.js";
import CardHeader from "../components/Card/CardHeader.js";
import CardAvatar from "../components/Card/CardAvatar.js";
import CardBody from "../components/Card/CardBody.js";
import CardFooter from "../components/Card/CardFooter.js";

import { AppContext,store} from "./AppContext"

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

function ProfileView({state, props}) {
    //console.log(JSON.stringify(props.history.location.state.data.pending.length));
    const classes = useStyles();
    const profile = props.history.location.state.data;
    const data = state.data;
    const newPassword = null;
    const confirmNewPassword = null;
    state = {data: data, newPassword: newPassword, confirmNewPassword: confirmNewPassword};
    return (
        <>
        <br/><br/><br/>
        <div>
          <GridContainer>
            <GridItem xs={12} sm={12} md={8}>
              <Card>
                <CardHeader color="primary">
                  <h4 className={classes.cardTitleWhite}>Update Profile</h4>
                </CardHeader>
                <CardBody>
                  <GridContainer>
                    <GridItem xs={12} sm={12} md={4}>
                      <CustomInput
                        labelText="Email address"
                        id="email"
                        formControlProps={{
                          fullWidth: true
                        }}
                        inputProps={{
                            value:profile.email,
                            disabled: true
                        }}
                      />
                    </GridItem>
                  </GridContainer>
                  <GridContainer>
                    <GridItem xs={12} sm={12} md={6}>
                      <CustomInput
                        labelText="New Password"
                        id="newPassword"
                        formControlProps={{
                          fullWidth: true
                        }}
                        inputProps={{
                            onChange:(event) => {
                                const target = event.target;
                                const value = target.value;
                                const name = target.name;
                                //console.log("VALUE: "+value);
                                const data = state.data;
                                const newPassword = value;
                                const confirmNewPassword = state.confirmNewPassword;
                                state = {data: data, newPassword: newPassword, confirmNewPassword: confirmNewPassword};
                            }
                        }}
                      />
                    </GridItem>
                    <GridItem xs={12} sm={12} md={6}>
                      <CustomInput
                        labelText="Confirm New Password"
                        id="confirmNewPassword"
                        formControlProps={{
                          fullWidth: true
                        }}
                        inputProps={{
                            onChange:(event) => {
                                const target = event.target;
                                const value = target.value;
                                const name = target.name;
                                //console.log("VALUE: "+value);
                                const data = state.data;
                                const newPassword = state.newPassword;
                                const confirmNewPassword = value;
                                state = {data: data, newPassword: newPassword, confirmNewPassword: confirmNewPassword};
                            }
                        }}
                      />
                    </GridItem>
                  </GridContainer>
                </CardBody>
                <CardFooter>
                  <Button color="primary" onClick={(e) => {
                        //console.log("PASS:" + state.newPassword);
                        //console.log("PASS2:" + state.confirmNewPassword);
                        const email = profile.email;
                        const payload = {
                            "email":email,
                            "password":state.newPassword
                        };
                        const apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/newPassword/";
                        axios.post(apiUrl,payload).then((response) => {
                            //console.log("MY_DATA: "+JSON.stringify(response.data));
                            //TODO: unhardcode
                            const producer = true;
                            const orgId = store.getState().sourceOrg.orgId;
                            const apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/recovery/?orgId="+orgId;
                            //console.log(apiUrl);
                            axios.get(apiUrl).then((response) => {
                                if(producer)
                                {
                                       props.history.push({
                                         pathname: "/home",
                                         state: { data: response.data }
                                       });
                                }
                                else
                                {
                                        props.history.push({
                                          pathname: "/dropOffHome",
                                          state: { data: response.data }
                                        });
                                }
                            });
                        });
                    }}>Update</Button>
                </CardFooter>
              </Card>
            </GridItem>
          </GridContainer>
        </div>
        </>
    );
}

class Profile extends React.Component {
    constructor(props) {
            super(props);
            this.state = {data: null};
        }

        render() {
           return (
                <>
                    <div id="parent">
                      <ProfileView state={this.state} props={this.props} />
                    </div>
                </>
            );
        }
}

export default withRouter(Profile)