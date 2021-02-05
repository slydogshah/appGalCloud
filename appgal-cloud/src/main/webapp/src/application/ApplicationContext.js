/*import * as React from 'react'
const CountContext = React.createContext()

function useCount() {
  const context = React.useContext(CountContext)
  if (!context) {
    throw new Error(`useCount must be used within a CountProvider`)
  }

  const [count, setCount] = React.useState(0);

  const value = React.useMemo(() => [count, setCount], [count]);
  console.log("************VALUE*************************");
  console.log(value);

  const value2 = React.useMemo(() => [count, setCount], [count]);
  console.log("************VALUE*************************");
  console.log(value2);
  console.log("*************************************");

  return context
}
function CountProvider(props) {
  const [count, setCount] = React.useState(0)
  const value = React.useMemo(() => [count, setCount], [count])
  return <CountContext.Provider value={value} {...props} />
}
//export {CountProvider, useCount}
export default CountContext*/