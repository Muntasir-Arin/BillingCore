import { Ionicons } from '@expo/vector-icons';
import { FlatList, StyleSheet, View } from 'react-native';
import { Card, Text, useTheme } from 'react-native-paper';
import dummyData from './data/dummyData';

const Logs = () => {
  const theme = useTheme();

  const renderLogItem = ({ item }: { item: typeof dummyData.actionLogs[0] }) => (
    <Card style={styles.logCard}>
      <Card.Content>
        <View style={styles.logHeader}>
          <Ionicons name="time-outline" size={20} color={theme.colors.primary} />
          <Text variant="titleMedium" style={styles.timestamp}>
            {item.timestamp}
          </Text>
        </View>
        <Text variant="bodyLarge" style={styles.action}>
          {item.action}
        </Text>
        <Text variant="bodyMedium" style={styles.details}>
          {item.details}
        </Text>
      </Card.Content>
    </Card>
  );

  return (
    <View style={styles.container}>
      <Text variant="headlineMedium" style={styles.title}>
        Action Logs
      </Text>
      <FlatList
        data={dummyData.actionLogs}
        renderItem={renderLogItem}
        keyExtractor={(item, index) => index.toString()}
        contentContainerStyle={styles.list}
      />
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
  list: {
    paddingBottom: 16,
  },
  logCard: {
    marginBottom: 12,
  },
  logHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  timestamp: {
    marginLeft: 8,
  },
  action: {
    marginBottom: 4,
  },
  details: {
    opacity: 0.7,
  },
});

export default Logs; 