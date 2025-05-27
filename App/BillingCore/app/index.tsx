import { Ionicons } from '@expo/vector-icons';
import { Dimensions, ScrollView, StyleSheet, View } from 'react-native';
import { LineChart } from 'react-native-chart-kit';
import { Card, Text, useTheme } from 'react-native-paper';
import dummyData from './data/dummyData';
import useStore from './store/useStore';

const Dashboard = () => {
  const theme = useTheme();
  const { user } = useStore();
  const screenWidth = Dimensions.get('window').width;

  const chartData = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [
      {
        data: [20, 45, 28, 80, 99, 43],
        color: (opacity = 1) => theme.colors.primary,
        strokeWidth: 2,
      },
    ],
  };

  const chartConfig = {
    backgroundGradientFrom: theme.colors.background,
    backgroundGradientTo: theme.colors.background,
    color: (opacity = 1) => theme.colors.primary,
    strokeWidth: 2,
    barPercentage: 0.5,
    useShadowColorFromDataset: false,
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text variant="headlineMedium">Welcome, {user?.name}</Text>
        <Text variant="bodyLarge" style={{ opacity: 0.7 }}>
          Here's your business overview
        </Text>
      </View>

      <View style={styles.statsContainer}>
        <Card style={styles.statCard}>
          <Card.Content>
            <Text variant="titleMedium">Total Sales</Text>
            <Text variant="headlineMedium">₹{dummyData.sales.total.toLocaleString()}</Text>
          </Card.Content>
        </Card>

        <Card style={styles.statCard}>
          <Card.Content>
            <Text variant="titleMedium">Active Products</Text>
            <Text variant="headlineMedium">{dummyData.products.length}</Text>
          </Card.Content>
        </Card>
      </View>

      <Card style={styles.chartCard}>
        <Card.Content>
          <Text variant="titleMedium" style={styles.chartTitle}>Sales Trend</Text>
          <LineChart
            data={chartData}
            width={screenWidth - 48}
            height={220}
            chartConfig={chartConfig}
            bezier
            style={styles.chart}
          />
        </Card.Content>
      </Card>

      <Card style={styles.recentActivityCard}>
        <Card.Content>
          <Text variant="titleMedium" style={styles.cardTitle}>Recent Activity</Text>
          {dummyData.actionLogs.slice(0, 5).map((log, index) => (
            <View key={index} style={styles.activityItem}>
              <Ionicons name="time-outline" size={20} color={theme.colors.primary} />
              <View style={styles.activityContent}>
                <Text variant="bodyMedium">{log.action}</Text>
                <Text variant="bodySmall" style={{ opacity: 0.7 }}>
                  {log.timestamp}
                </Text>
              </View>
            </View>
          ))}
        </Card.Content>
      </Card>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
  },
  header: {
    marginBottom: 24,
  },
  statsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 24,
  },
  statCard: {
    flex: 1,
    marginHorizontal: 4,
  },
  chartCard: {
    marginBottom: 24,
  },
  chartTitle: {
    marginBottom: 16,
  },
  chart: {
    marginVertical: 8,
    borderRadius: 16,
  },
  recentActivityCard: {
    marginBottom: 24,
  },
  cardTitle: {
    marginBottom: 16,
  },
  activityItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 16,
  },
  activityContent: {
    marginLeft: 12,
    flex: 1,
  },
});

export default Dashboard; 