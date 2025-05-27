import { Ionicons } from '@expo/vector-icons';
import { Stack, Tabs } from 'expo-router';
import { onAuthStateChanged } from 'firebase/auth';
import { useEffect } from 'react';
import { useColorScheme } from 'react-native';
import { MD3DarkTheme, MD3LightTheme, PaperProvider } from 'react-native-paper';
import { auth } from './config/firebase';
import useStore from './store/useStore';

const RootLayout = () => {
  const colorScheme = useColorScheme();
  const { theme, setTheme, isAuthenticated, setUser } = useStore();

  useEffect(() => {
    setTheme(colorScheme === 'dark' ? 'dark' : 'light');

    const unsubscribe = onAuthStateChanged(auth, (user) => {
      if (user) {
        setUser({
          email: user.email || '',
          name: user.displayName || 'Owner',
        });
      } else {
        setUser(null);
      }
    });

    return () => unsubscribe();
  }, [colorScheme]);

  const paperTheme = theme === 'dark' ? MD3DarkTheme : MD3LightTheme;

  return (
    <PaperProvider theme={paperTheme}>
      {!isAuthenticated ? (
        <Stack screenOptions={{ headerShown: false }}>
          <Stack.Screen name="login" />
        </Stack>
      ) : (
        <Tabs
          screenOptions={{
            tabBarActiveTintColor: theme === 'dark' ? '#fff' : '#000',
            tabBarInactiveTintColor: theme === 'dark' ? '#666' : '#999',
            tabBarStyle: {
              backgroundColor: theme === 'dark' ? '#1a1a1a' : '#fff',
            },
          }}
        >
          <Tabs.Screen
            name="index"
            options={{
              title: 'Dashboard',
              tabBarIcon: ({ color, size }) => (
                <Ionicons name="stats-chart" size={size} color={color} />
              ),
            }}
          />
          <Tabs.Screen
            name="logs"
            options={{
              title: 'Action Logs',
              tabBarIcon: ({ color, size }) => (
                <Ionicons name="list" size={size} color={color} />
              ),
            }}
          />
          <Tabs.Screen
            name="products"
            options={{
              title: 'Products',
              tabBarIcon: ({ color, size }) => (
                <Ionicons name="cube" size={size} color={color} />
              ),
            }}
          />
          <Tabs.Screen
            name="settings"
            options={{
              title: 'Settings',
              tabBarIcon: ({ color, size }) => (
                <Ionicons name="settings-outline" size={size} color={color} />
              ),
            }}
          />
        </Tabs>
      )}
    </PaperProvider>
  );
};

export default RootLayout;
