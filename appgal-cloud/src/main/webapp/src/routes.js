import React from 'react';

//Application
const Login = React.lazy(() => import('./application/Login'));
const Registration = React.lazy(() => import('./application/Registration'));
const Home = React.lazy(() => import('./application/Home'));
const SchedulePickup = React.lazy(() => import('./application/SchedulePickup'));
const PickupHistory = React.lazy(() => import('./application/PickupHistory'));
const DropOffHome = React.lazy(() => import('./application/DropOffHome'));
const DropOffHistory = React.lazy(() => import('./application/DropOffHistory'));
const AddPickupDetails = React.lazy(() => import('./application/AddPickupDetails'));
const DropOffOptions = React.lazy(() => import('./application/DropOffOptions'));

const routes = [
  { path: '/', exact: true, name: 'Login', component: Login},
  { path: '/registration', exact: true, name: 'Schedule Pickup', component: Registration },
  { path: '/home', exact: true, name: 'Home', component: Home },
  { path: '/schedulePickup', exact: true, name: 'Schedule Pickup', component: SchedulePickup },
  { path: '/pickupHistory', exact: true, name: 'Pickup History', component: PickupHistory },
  { path: '/dropOffHome', exact: true, name: 'DropOff Home', component: DropOffHome },
  { path: '/dropOffHistory', exact: true, name: 'DropOff History', component: DropOffHistory },
  { path: '/addPickupDetails', exact: true, name: 'Add Pickup Details', component: AddPickupDetails },
  { path: '/dropOffOptions', exact: true, name: 'DropOff Options', component: DropOffOptions },
];

export default routes;
