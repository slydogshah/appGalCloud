(this["webpackJsonp@coreui/coreui-free-react-admin-template"]=this["webpackJsonp@coreui/coreui-free-react-admin-template"]||[]).push([[24],{627:function(e,t,o){"use strict";var r=o(44),a=o(20),n=o(159),s=(o(1),o(626)),c=o(628),l=function(e){var t=e.borderColor,o=e.backgroundColor,l=e.pointHoverBackgroundColor,i=e.dataPoints,d=e.label,b=e.pointed,j=Object(n.a)(e,["borderColor","backgroundColor","pointHoverBackgroundColor","dataPoints","label","pointed"]),h=l||("transparent"!==o?o:t),p=[{data:i,borderColor:Object(s.getColor)(t),backgroundColor:Object(s.getColor)(o),pointBackgroundColor:Object(s.getColor)(h),pointHoverBackgroundColor:Object(s.getColor)(h),label:d}],x={scales:{xAxes:[{offset:!0,gridLines:{color:"transparent",zeroLineColor:"transparent"},ticks:{fontSize:2,fontColor:"transparent"}}],yAxes:[{display:!1,ticks:{display:!1,min:Math.min.apply(Math,i)-5,max:Math.max.apply(Math,i)+5}}]},elements:{line:{borderWidth:1},point:{radius:4,hitRadius:10,hoverRadius:4}}},m={scales:{xAxes:[{display:!1}],yAxes:[{display:!1}]},elements:{line:{borderWidth:2},point:{radius:0,hitRadius:10,hoverRadius:4}}},O=function(){var e=b?x:m;return Object.assign({},e,{maintainAspectRatio:!1,legend:{display:!1}})}(),g=Object(s.deepObjectsMerge)(p,j.datasets||{}),u=Object(s.deepObjectsMerge)(O,j.options||{});return Object(a.jsx)(c.c,Object(r.a)(Object(r.a)({},j),{},{datasets:g,options:u,labels:d}))};l.defaultProps={borderColor:"rgba(255,255,255,.55)",backgroundColor:"transparent",dataPoints:[10,22,34,46,58,70,46,23,45,78,34,12],label:"Sales"},t.a=l},631:function(e,t,o){"use strict";var r=o(44),a=o(20),n=o(159),s=(o(1),o(626)),c=o(628),l=function(e){var t=e.backgroundColor,o=e.pointHoverBackgroundColor,l=e.dataPoints,i=e.label,d=(e.pointed,Object(n.a)(e,["backgroundColor","pointHoverBackgroundColor","dataPoints","label","pointed"])),b=[{data:l,backgroundColor:Object(s.getColor)(t),pointHoverBackgroundColor:Object(s.getColor)(o),label:i,barPercentage:.5,categoryPercentage:1}],j={maintainAspectRatio:!1,legend:{display:!1},scales:{xAxes:[{display:!1}],yAxes:[{display:!1}]}};return Object(a.jsx)(c.a,Object(r.a)(Object(r.a)({},d),{},{datasets:b,options:j,labels:i}))};l.defaultProps={backgroundColor:"rgba(0,0,0,.2)",dataPoints:[10,22,34,46,58,70,46,23,45,78,34,12],label:"Sales"},t.a=l},639:function(e,t,o){"use strict";o.r(t);var r=o(20),a=(o(1),o(620)),n=o(625),s=o(627),c=o(631);t.default=function(){return Object(r.jsxs)(a.wb,{children:[Object(r.jsx)(a.u,{sm:"6",lg:"3",children:Object(r.jsx)(a.Ub,{color:"gradient-primary",header:"9.823",text:"Members online",footerSlot:Object(r.jsx)(s.a,{pointed:!0,className:"c-chart-wrapper mt-3 mx-3",style:{height:"70px"},dataPoints:[65,59,84,84,51,55,40],pointHoverBackgroundColor:"primary",label:"Members",labels:"months"}),children:Object(r.jsxs)(a.z,{children:[Object(r.jsx)(a.E,{color:"transparent",children:Object(r.jsx)(n.a,{name:"cil-settings"})}),Object(r.jsxs)(a.D,{className:"pt-0",placement:"bottom-end",children:[Object(r.jsx)(a.C,{children:"Action"}),Object(r.jsx)(a.C,{children:"Another action"}),Object(r.jsx)(a.C,{children:"Something else here..."}),Object(r.jsx)(a.C,{disabled:!0,children:"Disabled action"})]})]})})}),Object(r.jsx)(a.u,{sm:"6",lg:"3",children:Object(r.jsx)(a.Ub,{color:"gradient-info",header:"9.823",text:"Members online",footerSlot:Object(r.jsx)(s.a,{pointed:!0,className:"mt-3 mx-3",style:{height:"70px"},dataPoints:[1,18,9,17,34,22,11],pointHoverBackgroundColor:"info",options:{elements:{line:{tension:1e-5}}},label:"Members",labels:"months"}),children:Object(r.jsxs)(a.z,{children:[Object(r.jsx)(a.E,{caret:!1,color:"transparent",children:Object(r.jsx)(n.a,{name:"cil-location-pin"})}),Object(r.jsxs)(a.D,{className:"pt-0",placement:"bottom-end",children:[Object(r.jsx)(a.C,{children:"Action"}),Object(r.jsx)(a.C,{children:"Another action"}),Object(r.jsx)(a.C,{children:"Something else here..."}),Object(r.jsx)(a.C,{disabled:!0,children:"Disabled action"})]})]})})}),Object(r.jsx)(a.u,{sm:"6",lg:"3",children:Object(r.jsx)(a.Ub,{color:"gradient-warning",header:"9.823",text:"Members online",footerSlot:Object(r.jsx)(s.a,{className:"mt-3",style:{height:"70px"},backgroundColor:"rgba(255,255,255,.2)",dataPoints:[78,81,80,45,34,12,40],options:{elements:{line:{borderWidth:2.5}}},pointHoverBackgroundColor:"warning",label:"Members",labels:"months"}),children:Object(r.jsxs)(a.z,{children:[Object(r.jsx)(a.E,{color:"transparent",children:Object(r.jsx)(n.a,{name:"cil-settings"})}),Object(r.jsxs)(a.D,{className:"pt-0",placement:"bottom-end",children:[Object(r.jsx)(a.C,{children:"Action"}),Object(r.jsx)(a.C,{children:"Another action"}),Object(r.jsx)(a.C,{children:"Something else here..."}),Object(r.jsx)(a.C,{disabled:!0,children:"Disabled action"})]})]})})}),Object(r.jsx)(a.u,{sm:"6",lg:"3",children:Object(r.jsx)(a.Ub,{color:"gradient-danger",header:"9.823",text:"Members online",footerSlot:Object(r.jsx)(c.a,{className:"mt-3 mx-3",style:{height:"70px"},backgroundColor:"rgb(250, 152, 152)",label:"Members",labels:"months"}),children:Object(r.jsxs)(a.z,{children:[Object(r.jsx)(a.E,{caret:!0,className:"text-white",color:"transparent",children:Object(r.jsx)(n.a,{name:"cil-settings"})}),Object(r.jsxs)(a.D,{className:"pt-0",placement:"bottom-end",children:[Object(r.jsx)(a.C,{children:"Action"}),Object(r.jsx)(a.C,{children:"Another action"}),Object(r.jsx)(a.C,{children:"Something else here..."}),Object(r.jsx)(a.C,{disabled:!0,children:"Disabled action"})]})]})})})]})}}}]);
//# sourceMappingURL=24.33aee7f9.chunk.js.map