(this["webpackJsonp@coreui/coreui-free-react-admin-template"]=this["webpackJsonp@coreui/coreui-free-react-admin-template"]||[]).push([[45],{786:function(e,t,r){"use strict";r.r(t);var s=r(20),a=r(162),i=r(163),n=r(165),d=r(164),o=r(1),u=r.n(o),l=r(19),c=r(653),m=r.n(c),g=r(622),h=[{id:0,name:"John Doe",registered:"2018/01/01",role:"Guest",status:"Pending"},{id:1,name:"Samppa Nori",registered:"2018/01/01",role:"Member",status:"Active"},{id:2,name:"Estavan Lykos",registered:"2018/02/01",role:"Staff",status:"Banned"},{id:3,name:"Chetan Mohamed",registered:"2018/02/01",role:"Admin",status:"Inactive"},{id:4,name:"Derick Maximinus",registered:"2018/03/01",role:"Member",status:"Pending"},{id:5,name:"Friderik D\xe1vid",registered:"2018/01/21",role:"Staff",status:"Active"},{id:6,name:"Yiorgos Avraamu",registered:"2018/01/01",role:"Member",status:"Active"},{id:7,name:"Avram Tarasios",registered:"2018/02/01",role:"Staff",status:"Banned"},{id:8,name:"Quintin Ed",registered:"2018/02/01",role:"Admin",status:"Inactive"},{id:9,name:"En\xe9as Kwadwo",registered:"2018/03/01",role:"Member",status:"Pending"},{id:10,name:"Agapetus Tade\xe1\u0161",registered:"2018/01/21",role:"Staff",status:"Active"},{id:11,name:"Carwyn Fachtna",registered:"2018/01/01",role:"Member",status:"Active"},{id:12,name:"Nehemiah Tatius",registered:"2018/02/01",role:"Staff",status:"Banned"},{id:13,name:"Ebbe Gemariah",registered:"2018/02/01",role:"Admin",status:"Inactive"},{id:14,name:"Eustorgios Amulius",registered:"2018/03/01",role:"Member",status:"Pending"},{id:15,name:"Leopold G\xe1sp\xe1r",registered:"2018/01/21",role:"Staff",status:"Active"},{id:16,name:"Pompeius Ren\xe9",registered:"2018/01/01",role:"Member",status:"Active"},{id:17,name:"Pa\u0109jo Jadon",registered:"2018/02/01",role:"Staff",status:"Banned"},{id:18,name:"Micheal Mercurius",registered:"2018/02/01",role:"Admin",status:"Inactive"},{id:19,name:"Ganesha Dubhghall",registered:"2018/03/01",role:"Member",status:"Pending"},{id:20,name:"Hiroto \u0160imun",registered:"2018/01/21",role:"Staff",status:"Active"},{id:21,name:"Vishnu Serghei",registered:"2018/01/01",role:"Member",status:"Active"},{id:22,name:"Zbyn\u011bk Phoibos",registered:"2018/02/01",role:"Staff",status:"Banned"},{id:23,name:"Aulus Agmundr",registered:"2018/01/01",role:"Member",status:"Pending"},{id:42,name:"Ford Prefect",registered:"2001/05/25",role:"Alien",status:"Don't panic!"}],f=[{key:"name",_style:{width:"40%"}},"registered",{key:"role",_style:{width:"20%"}},{key:"status",_style:{width:"20%"}},{key:"show_details",label:"",_style:{width:"1%"},sorter:!1,filter:!1}],b=function(e){switch(e){case"Active":return"success";case"Inactive":return"secondary";case"Pending":return"warning";case"Banned":return"danger";default:return"primary"}},p=function(e){Object(n.a)(r,e);var t=Object(d.a)(r);function r(e){return Object(a.a)(this,r),t.call(this,e)}return Object(i.a)(r,[{key:"render",value:function(){var e=this;return Object(s.jsx)(g.y,{items:h,fields:f,columnFilter:!0,tableFilter:!0,footer:!0,itemsPerPageSelect:!0,itemsPerPage:50,hover:!0,sorter:!0,pagination:!0,scopedSlots:{status:function(e){return Object(s.jsx)("td",{children:Object(s.jsx)(g.b,{color:b(e.status),children:e.status})})},show_details:function(t,r){return Object(s.jsx)("td",{className:"py-2",children:Object(s.jsx)(g.f,{color:"primary",variant:"outline",shape:"square",size:"sm",onClick:function(){var t;t=e.props.history,m.a.post("http://localhost:8080/registration/login/",{email:"Do",password:"do",statusCode:null,profile:null}).then((function(e){t.push({pathname:"/schedulePickup",state:e.data})}))},children:"Show"})})}}})}}]),r}(u.a.Component);t.default=Object(l.i)(p)}}]);
//# sourceMappingURL=45.cb2d234a.chunk.js.map