import AsyncStorage from '@react-native-async-storage/async-storage';
import { initializeApp } from 'firebase/app';
import { getReactNativePersistence, initializeAuth } from 'firebase/auth';
import { getMessaging } from 'firebase/messaging';
import { Platform } from 'react-native';

const firebaseConfig = {
  apiKey: "YOUR_API_KEY",
  authDomain: "billingcore-dashboard.firebaseapp.com",
  projectId: "billingcore-dashboard",
  storageBucket: "billingcore-dashboard.appspot.com",
  messagingSenderId: "YOUR_MESSAGING_SENDER_ID",
  appId: "YOUR_APP_ID"
};

const app = initializeApp(firebaseConfig);

// Initialize auth with persistence
const auth = initializeAuth(app, {
  persistence: getReactNativePersistence(AsyncStorage)
});

// Initialize messaging only on web platform
const messaging = Platform.OS === 'web' ? getMessaging(app) : null;

export { auth, messaging };
export default app; 