import React from 'react';

//Application
const ActiveNetwork = React.lazy(() => import('./application/ActiveNetwork'));
const SchedulePickup = React.lazy(() => import('./application/SchedulePickup'));
const Registration = React.lazy(() => import('./application/Registration'));
const Home = React.lazy(() => import('./application/Home'));
const PickupHistory = React.lazy(() => import('./application/PickupHistory'));
const DropOffHome = React.lazy(() => import('./application/DropOffHome'));
const DropOffHistory = React.lazy(() => import('./application/DropOffHistory'));
const AddPickupDetails = React.lazy(() => import('./application/AddPickupDetails'));

const routes = [
  { path: '/', exact: true, name: 'Home', component: Home},
  { path: '/activeNetwork', exact: true, name: 'Active Network', component: ActiveNetwork },
  { path: '/registration', exact: true, name: 'Schedule Pickup', component: Registration },
  { path: '/schedulePickup', exact: true, name: 'Schedule Pickup', component: SchedulePickup },
  { path: '/home', exact: true, name: 'Home', component: Home },
  { path: '/pickupHistory', exact: true, name: 'Pickup History', component: PickupHistory },
  { path: '/dropOffHome', exact: true, name: 'DropOff Home', component: DropOffHome },
  { path: '/dropOffHistory', exact: true, name: 'DropOff History', component: DropOffHistory },
  { path: '/addPickupDetails', exact: true, name: 'Add Pickup Details', component: AddPickupDetails },
];

export default routes;
