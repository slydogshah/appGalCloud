import React, { Component, useContext } from 'react'
import { HashRouter, Route, Switch, Redirect} from 'react-router-dom'
import './scss/style.scss'
import { AppContext} from "./application/AppContext"

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

//https://ui.dev/react-router-v5-protected-routes-authentication/
// Requirement 3.
// It checks if the user is authenticated, if they are,
// it renders the "component" prop. If not, it redirects
// the user to /login.
function PrivateRoute ({ children, ...rest }) {
  const auth = useContext(AppContext).auth;
  return (
    <Route {...rest} render={(props) => {
      return auth === true
      ? children
      : <Redirect to='/profile' />
    }} />
  )
}



const appData = {auth:false,counter:0};

//class App extends Component {
//const App = () => {
export default function App() {
    return (
      <AppContext.Provider value={appData}>
          <HashRouter>
            <React.Suspense fallback={loading}>
              <Switch>
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

                <PrivateRoute path="/home" name="Home" render={PrivateRoute}>
                    <Home/>
                </PrivateRoute>
              </Switch>
            </React.Suspense>
          </HashRouter>
      </AppContext.Provider>
    );
}

//export default App
