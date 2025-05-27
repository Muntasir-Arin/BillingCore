import { Ionicons } from '@expo/vector-icons';
import { FlatList, StyleSheet, View } from 'react-native';
import { Card, Text, useTheme } from 'react-native-paper';
import dummyData from './data/dummyData';

const Products = () => {
  const theme = useTheme();

  const renderProductItem = ({ item }: { item: typeof dummyData.products[0] }) => (
    <Card style={styles.productCard}>
      <Card.Content>
        <View style={styles.productHeader}>
          <Ionicons name="cube-outline" size={24} color={theme.colors.primary} />
          <View style={styles.productInfo}>
            <Text variant="titleMedium">{item.name}</Text>
            <Text variant="bodyMedium" style={styles.price}>
              ₹{item.price.toLocaleString()}
            </Text>
          </View>
        </View>
        <View style={styles.stockInfo}>
          <Text variant="bodyMedium">Stock: {item.stock}</Text>
          <Text
            variant="bodyMedium"
            style={[
              styles.status,
              { color: item.stock > 0 ? theme.colors.primary : theme.colors.error },
            ]}
          >
            {item.stock > 0 ? 'In Stock' : 'Out of Stock'}
          </Text>
        </View>
      </Card.Content>
    </Card>
  );

  return (
    <View style={styles.container}>
      <Text variant="headlineMedium" style={styles.title}>
        Products
      </Text>
      <FlatList
        data={dummyData.products}
        renderItem={renderProductItem}
        keyExtractor={(item) => item.id.toString()}
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
  productCard: {
    marginBottom: 12,
  },
  productHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  productInfo: {
    marginLeft: 12,
    flex: 1,
  },
  price: {
    opacity: 0.7,
  },
  stockInfo: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  status: {
    fontWeight: 'bold',
  },
});

export default Products; 