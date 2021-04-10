import React, { useContext, createContext, ReactReduxContext } from 'react'

export const AppContext = React.createContext();

export const store = (initialState => {
  let value = initialState;
  let listeners = [];

  const getState = () => value;

  const setState = fn => {
    value = fn(value);

    //console.log("*******SET_STATE_INVOKE***********"+JSON.stringify(getState()));

    listeners.forEach(l => l(value));
  };

  const subscribe = listener => {
    listeners.push(listener);
    return () =>
      (listeners = listeners.filter(f => f !== listener));
  };
  return { getState, setState, subscribe };
})({ auth: false }); //pass initial state to IIFE

function Provider({ store, children }) {
  //console.log("STORE_PROVIDER_IS_ACTIVEs");

  const [state, setState] = React.useState(
    store.getState()
  );
  React.useEffect(
    () =>
      store.subscribe(() => {

        //console.log("REDUX_STORE_IS_ACTIVEs");

        const lastState = store.getState();
        //if a lot of setState calls are made synchronously
        //  do not update dom but let it batch update state
        //  before triggering a render
        Promise.resolve().then(() => {
          if (lastState === store.getState()) {
            setState(store.getState());
          }
        });
      }),
    [store]
  );
}

const AppContextConsumer = AppContext.Consumer
export { AppContextConsumer };