import 'react-app-polyfill/ie11' // For IE 11 support
import 'antd/dist/antd.css' //antd Css link
import 'react-app-polyfill/stable'
import 'core-js'
import './polyfill'
import React from 'react'
import ReactDOM from 'react-dom'

import { icons } from './assets/icons'

React.icons = icons

ReactDOM.render(
  document.getElementById('root')
)
