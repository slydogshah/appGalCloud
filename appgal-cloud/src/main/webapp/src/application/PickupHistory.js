import React from "react";
import ReactDOM from 'react-dom';
import { withRouter } from "react-router";
import { axios} from "../App"
// @material-ui/core components
import { makeStyles } from "@material-ui/core/styles";
// core components
import GridItem from "../components/Grid/GridItem.js";
import GridContainer from "../components/Grid/GridContainer.js";
import Table from "../components/Table/Table.js";
import Card from "../components/Card/Card.js";
import CardHeader from "../components/Card/CardHeader.js";
import CardBody from "../components/Card/CardBody.js";
import { AppContext,store} from "./AppContext"

const styles = {
  cardCategoryWhite: {
    "&,& a,& a:hover,& a:focus": {
      color: "rgba(255,255,255,.62)",
      margin: "0",
      fontSize: "14px",
      marginTop: "0",
      marginBottom: "0"
    },
    "& a,& a:hover,& a:focus": {
      color: "#FFFFFF"
    }
  },
  cardTitleWhite: {
    color: "#FFFFFF",
    marginTop: "0px",
    minHeight: "auto",
    fontWeight: "300",
    fontFamily: "'Roboto', 'Helvetica', 'Arial', sans-serif",
    marginBottom: "3px",
    textDecoration: "none",
    "& small": {
      color: "#777",
      fontSize: "65%",
      fontWeight: "400",
      lineHeight: "1"
    }
  }
};

const useStyles = makeStyles(styles);

const WaitOnData = ({state}) => {
    if (state.data === null) {
       return(
         <>
          <br/>
                    <br/>
                    <br/>
                    <br/>
        </>
       )
    }
    else{
        return (
              <>
              <br/>
              <br/>
              <br/>
              <br/>
              <ClosedTransactionView closed={state.data}/>
              </>
        )
    }
}

const ClosedTransactionView = ({closed}) => {
  console.log("TABLE_LIST_INVOKED");
  const classes = useStyles();
  const txs = [];
  console.log("TXS: "+JSON.stringify(txs));
  for (const [index, value] of closed.entries()) {
    var email = value.pickupNotification.foodRunner.profile.email;
    var orgName = value.pickupNotification.dropOffOrg.orgName;
    var tx = [
        email,orgName
    ];
    txs.push(tx);
  }
  console.log("LATERTXS: "+JSON.stringify(txs));
  return (
    <>
    <br/><br/><br/>
    <GridContainer>
      <GridItem xs={12} sm={12} md={12}>
        <Card>
          <CardHeader color="primary">
            <h4 className={classes.cardTitleWhite}>Pickup History</h4>
            <p className={classes.cardCategoryWhite}>
            </p>
          </CardHeader>
          <CardBody>
            <Table
              tableHeaderColor="primary"
              tableHead={["FoodRunner", "Drop Off Organization"]}
              tableData={txs}
            />
          </CardBody>
        </Card>
      </GridItem>
    </GridContainer>
    </>
  );
}


class PickupHistory extends React.Component
{
    constructor(props)
    {
        super(props);
        this.state = {data: null};
        this.renderMyData();
    }

    renderMyData()
    {
        const orgId = store.getState().sourceOrg.orgId;
        const apiUrl = window.location.protocol +"//"+window.location.hostname+"/tx/recovery/history/?orgId="+orgId;
        console.log(apiUrl);
        axios.get(apiUrl).then((response) => {
            this.setState({data: response.data});
        });
    }

    render()
    {
        return(
            <>
                <WaitOnData state={this.state} />
            </>
        );
    }
}

export default withRouter(PickupHistory)