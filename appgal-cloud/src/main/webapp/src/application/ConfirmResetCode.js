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

function ConfirmResetCodeView({state, props}) {
    const classes = useStyles();
    const resetEmail = props.history.location.state.data.email;
    return (
        <>
        <br/><br/><br/>
        <div>
          <GridContainer>
            <GridItem xs={12} sm={12} md={8}>
              <Card>
                <CardHeader color="primary">
                  <h4 className={classes.cardTitleWhite}>Confirm Reset Code</h4>
                </CardHeader>
                <CardBody>
                  <GridContainer>
                    <GridItem xs={12} sm={12} md={4}>
                      <CustomInput
                        labelText="Reset Code"
                        id="resetCode"
                        formControlProps={{
                          fullWidth: true
                        }}
                        inputProps={{
                           onChange:(event) => {
                               const target = event.target;
                               const value = target.value;
                               const name = target.name;
                               state.resetCode = value;
                           }
                       }}
                      />
                    </GridItem>
                  </GridContainer>
                </CardBody>
                <CardFooter>
                  <Button color="primary" onClick={(e) => {
                        const payload = {
                           email:resetEmail,
                           resetCode:state.resetCode
                        };
                        var apiUrl = window.location.protocol +"//"+window.location.hostname+"/registration/verifyResetCode/";
                        axios.post(apiUrl,payload).then((response) => {
                            const propagation = {
                              email:resetEmail
                            };
                            props.history.push({
                                pathname: "/resetPassword",
                                state: { data: propagation }
                            });
                        });
                    }}>Confirm</Button>
                </CardFooter>
              </Card>
            </GridItem>
          </GridContainer>
        </div>
        </>
    );
}

class ConfirmResetCode extends React.Component {
    constructor(props) {
            super(props);
            this.state = {data: null};
        }

        render() {
           return (
                <>
                    <div id="parent">
                      <ConfirmResetCodeView state={this.state} props={this.props} />
                    </div>
                </>
            );
        }
}

export default withRouter(ConfirmResetCode)