import React, { Component } from 'react';


const ApplicationContext = React.createContext();
const planets = [
          {
            name: 'earth',
            occupied: false,
          },
          {
            name: 'mars',
            occupied: false,
          },
          {
            name: 'piers morgan`s forehead',
            occupied: true,
          }
        ];

const PrintApplicationContext = () => {
    const { data } = React.useContext(ApplicationContext);
    console.log(JSON.stringify(data));
}

export default PrintApplicationContext;