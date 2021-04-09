import React from 'react';

//Application
const Login = React.lazy(() => import('./application/Login'));
const Home = React.lazy(() => import('./application/Home'));
const PickupHistory = React.lazy(() => import('./application/PickupHistory'));
const AddPickupDetails = React.lazy(() => import('./application/AddPickupDetails'));
const DropOffOptions = React.lazy(() => import('./application/DropOffOptions'));
const Profile = React.lazy(() => import('./application/Profile'));

const DropOffHome = React.lazy(() => import('./application/DropOffHome'));
const DropOffHistory = React.lazy(() => import('./application/DropOffHistory'));

const routes = [
  { path: '/dashboard', exact: true, name: 'Login', component: Login},
  { path: '/dashboard/home', exact: true, name: 'Home', component: Home },
  { path: '/dashboard/profile', exact: true, name: 'Profile', component: Profile },
  { path: '/dashboard/pickupHistory', exact: true, name: 'Pickup History', component: PickupHistory },
  { path: '/dashboard/addPickupDetails', exact: true, name: 'Add Pickup Details', component: AddPickupDetails },
  { path: '/dashboard/dropOffOptions', exact: true, name: 'DropOff Options', component: DropOffOptions },
  { path: '/dashboard/dropOffHome', exact: true, name: 'DropOff Home', component: DropOffHome },
  { path: '/dashboard/dropOffHistory', exact: true, name: 'DropOff History', component: DropOffHistory },
];

export default routes;
