import { router } from 'expo-router';
import { signOut } from 'firebase/auth';
import { StyleSheet, View } from 'react-native';
import { Button, List, Switch, Text, useTheme } from 'react-native-paper';
import { auth } from './config/firebase';
import useStore from './store/useStore';

const Settings = () => {
  const theme = useTheme();
  const { user, setUser } = useStore();

  const handleLogout = async () => {
    try {
      await signOut(auth);
      setUser(null);
      router.replace('/login');
    } catch (error) {
      console.error('Error signing out:', error);
    }
  };

  return (
    <View style={styles.container}>
      <Text variant="headlineMedium" style={styles.title}>
        Settings
      </Text>

      <List.Section>
        <List.Subheader>Account</List.Subheader>
        <List.Item
          title="Email"
          description={user?.email}
          left={props => <List.Icon {...props} icon="email" />}
        />
        <List.Item
          title="Name"
          description={user?.name}
          left={props => <List.Icon {...props} icon="account" />}
        />
      </List.Section>

      <List.Section>
        <List.Subheader>Preferences</List.Subheader>
        <List.Item
          title="Dark Mode"
          left={props => <List.Icon {...props} icon="theme-light-dark" />}
          right={() => <Switch value={theme.dark} onValueChange={() => {}} />}
        />
        <List.Item
          title="Notifications"
          left={props => <List.Icon {...props} icon="bell" />}
          right={() => <Switch value={true} onValueChange={() => {}} />}
        />
      </List.Section>

      <List.Section>
        <List.Subheader>About</List.Subheader>
        <List.Item
          title="Version"
          description="1.0.0"
          left={props => <List.Icon {...props} icon="information" />}
        />
        <List.Item
          title="Terms of Service"
          left={props => <List.Icon {...props} icon="file-document" />}
          onPress={() => {}}
        />
        <List.Item
          title="Privacy Policy"
          left={props => <List.Icon {...props} icon="shield-account" />}
          onPress={() => {}}
        />
      </List.Section>

      <Button
        mode="outlined"
        onPress={handleLogout}
        style={styles.logoutButton}
        icon="logout"
      >
        Logout
      </Button>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
  },
  title: {
    marginBottom: 16,
  },
  logoutButton: {
    marginTop: 24,
  },
});

export default Settings; 