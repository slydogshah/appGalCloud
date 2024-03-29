import React, { Component, Suspense, useContext, ReactReduxContext } from 'react'
import { BrowserRouter, Router, Route, Switch, Redirect} from 'react-router-dom'
import './scss/style.scss'
import { AppContext,store} from "./application/AppContext"
import axiosLib  from 'axios';

const loading = (
  <div className="pt-3 text-center">
    <div className="sk-spinner sk-spinner-pulse"></div>
  </div>
)


// Containers
const TheLayout = React.lazy(() => import('./containers/TheLayout'))

// Pages
const Page404 = React.lazy(() => import('./views/pages/page404/Page404'))
const Page500 = React.lazy(() => import('./views/pages/page500/Page500'))
const Home = React.lazy(() => import('./application/Home'))
const PickupHistory = React.lazy(() => import('./application/PickupHistory'))

//Configure Axios
export const axios = axiosLib.create({
    transformRequest: [axiosLib.defaults.transformRequest[0], (data, headers) => {

       // mutate headers logic
       const bearerToken = store.getState().bearer;
       if(bearerToken != null){
           headers.Bearer = store.getState().bearer;
           headers.Principal = store.getState().email;
           //alert(JSON.stringify(headers));
       }

        return data;
    }],
});


//https://ui.dev/react-router-v5-protected-routes-authentication/
function PrivateRoute ({ children, ...rest }) {
  const auth = store.getState().auth;
  //console.log("STORE_STATE: "+JSON.stringify(store.getState()));
  return (
    <Route {...rest} render={() => {
      return auth === true
      ? children
      : <Redirect to='/' />
    }} />
  )
}

export default function App() {
    return (
          <AppContext.Provider store={store}>
              <BrowserRouter>
                    <Suspense fallback={loading}>
                               <Route
                                  exact
                                  path="/404"
                                  name="Page 404"
                                  render={(props) => <Page404 {...props} />}
                                />
                                <Route
                                  exact
                                  path="/500"
                                  name="Page 500"
                                  render={(props) => <Page500 {...props} />}
                                />
                                <Route
                                  path="/"
                                  name="Login"
                                  render={(props) => <TheLayout {...props} />}
                                />
                    </Suspense>
              </BrowserRouter>
          </AppContext.Provider>
    );
}

//export default App
