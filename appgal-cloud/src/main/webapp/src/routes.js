import React from 'react';

//Application
const Login = React.lazy(() => import('./application/Login'));
const Home = React.lazy(() => import('./application/Home'));
const PickupHistory = React.lazy(() => import('./application/PickupHistory'));
const DropOffHome = React.lazy(() => import('./application/DropOffHome'));
const DropOffHistory = React.lazy(() => import('./application/DropOffHistory'));
const AddPickupDetails = React.lazy(() => import('./application/AddPickupDetails'));
const DropOffOptions = React.lazy(() => import('./application/DropOffOptions'));
const Profile = React.lazy(() => import('./application/Profile'));

const routes = [
  { path: '/', exact: true, name: 'Login', component: Login},
  { path: '/home', exact: true, name: 'Home', component: Home },
  { path: '/pickupHistory', exact: true, name: 'Pickup History', component: PickupHistory },
  { path: '/addPickupDetails', exact: true, name: 'Add Pickup Details', component: AddPickupDetails },
  { path: '/dropOffOptions', exact: true, name: 'DropOff Options', component: DropOffOptions },
  { path: '/dropOffHome', exact: true, name: 'DropOff Home', component: DropOffHome },
  { path: '/dropOffHistory', exact: true, name: 'DropOff History', component: DropOffHistory },
  { path: '/profile', exact: true, name: 'Profile', component: Profile },
];

export default routes;
