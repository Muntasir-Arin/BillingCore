export const branches = [
  { id: 1, name: 'Main Branch', location: 'Dhaka' },
  { id: 2, name: 'Chittagong Branch', location: 'Chittagong' },
  { id: 3, name: 'Sylhet Branch', location: 'Sylhet' },
];

export const employees = [
  { id: 1, name: 'Rafiq Ahmed', branchId: 1, role: 'Sales Executive' },
  { id: 2, name: 'Fatima Khan', branchId: 2, role: 'Sales Manager' },
  { id: 3, name: 'Imran Hossain', branchId: 3, role: 'Sales Executive' },
];

export const products = [
  { id: 1, name: 'ASUS Laptop', category: 'Electronics', price: 89000, stock: 15, branchId: 1 },
  { id: 2, name: 'iPhone 13', category: 'Electronics', price: 120000, stock: 8, branchId: 1 },
  { id: 3, name: 'Samsung TV', category: 'Electronics', price: 65000, stock: 5, branchId: 2 },
  { id: 4, name: 'Office Chair', category: 'Furniture', price: 12000, stock: 20, branchId: 3 },
];

export const customerGroups = [
  { id: 1, name: 'Tech Lovers', discount: 5 },
  { id: 2, name: 'Regular Customers', discount: 2 },
  { id: 3, name: 'VIP', discount: 10 },
];

export const sales = [
  {
    id: 1,
    date: '2024-03-15',
    productId: 1,
    employeeId: 1,
    branchId: 1,
    customerGroupId: 1,
    amount: 89000,
    quantity: 1,
  },
  {
    id: 2,
    date: '2024-03-15',
    productId: 2,
    employeeId: 2,
    branchId: 2,
    customerGroupId: 2,
    amount: 120000,
    quantity: 1,
  },
  // Add more sales data as needed
];

export const actionLogs = [
  {
    id: 1,
    timestamp: '2024-03-15T10:30:00',
    actionType: 'Sale',
    employeeId: 1,
    branchId: 1,
    description: 'Sold ASUS laptop to Tech Lovers group',
  },
  {
    id: 2,
    timestamp: '2024-03-15T11:15:00',
    actionType: 'Stock Update',
    employeeId: 2,
    branchId: 2,
    description: 'Updated Samsung TV stock to 5 units',
  },
  // Add more logs as needed
];

const dummyData = {
  branches,
  employees,
  products,
  customerGroups,
  sales,
  actionLogs,
};

export default dummyData; 