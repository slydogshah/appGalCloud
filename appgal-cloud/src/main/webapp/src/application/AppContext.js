import React, { useContext, createContext } from 'react'

export const AppContext = React.createContext();
const AppContextConsumer = AppContext.Consumer

export { AppContextConsumer };