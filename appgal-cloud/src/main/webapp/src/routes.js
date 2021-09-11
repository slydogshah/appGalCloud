import React from 'react';

//Application
const Login = React.lazy(() => import('./application/Login'));
const ForgotPassword = React.lazy(() => import('./application/ForgotPassword'));
const ConfirmResetCode = React.lazy(() => import('./application/ConfirmResetCode'));
const ResetPassword = React.lazy(() => import('./application/ResetPassword'));
const Home = React.lazy(() => import('./application/Home'));
const PickupHistory = React.lazy(() => import('./application/PickupHistory'));
const AddPickupDetails = React.lazy(() => import('./application/AddPickupDetails'));
const DropOffOptions = React.lazy(() => import('./application/DropOffOptions'));
const Profile = React.lazy(() => import('./application/Profile'));
const StaffResetPassword = React.lazy(() => import('./application/StaffResetPassword'));

const DropOffHome = React.lazy(() => import('./application/DropOffHome'));
const DropOffHistory = React.lazy(() => import('./application/DropOffHistory'));

const routes = [
  { path: '/', exact: true, name: 'Login', component: Login},
  { path: '/forgotPassword', exact: true, name: 'Forgot Password', component: ForgotPassword},
  { path: '/resetPassword', exact: true, name: 'Reset Password', component: ResetPassword},
  { path: '/staffResetPassword', exact: true, name: 'Reset Password', component: StaffResetPassword},
  { path: '/confirmResetCode', exact: true, name: 'Confirm Reset Code', component: ConfirmResetCode},
  { path: '/home', exact: true, name: 'Home', component: Home },
  { path: '/profile', exact: true, name: 'Profile', component: Profile },
  { path: '/pickupHistory', exact: true, name: 'Pickup History', component: PickupHistory },
  { path: '/addPickupDetails', exact: true, name: 'Add Pickup Details', component: AddPickupDetails },
  { path: '/dropOffOptions', exact: true, name: 'DropOff Options', component: DropOffOptions },
  { path: '/dropOffHome', exact: true, name: 'DropOff Home', component: DropOffHome },
  { path: '/dropOffHistory', exact: true, name: 'DropOff History', component: DropOffHistory },
];

export default routes;
