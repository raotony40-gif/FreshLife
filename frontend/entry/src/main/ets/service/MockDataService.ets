import { CartItem, OrderSummary, Product } from '../model/AppModels';

export class MockDataService {
  static getProducts(): Product[] {
    return [
      {
        id: 1,
        name: '西红柿',
        category: '蔬菜',
        price: 4.90,
        stock: 200,
        imageUrl: '',
        description: '新鲜西红柿，适合炒菜、煮汤和凉拌。',
        status: 1
      },
      {
        id: 2,
        name: '鸡蛋',
        category: '蛋奶',
        price: 12.80,
        stock: 300,
        imageUrl: '',
        description: '新鲜鸡蛋，家庭日常烹饪必备。',
        status: 1
      },
      {
        id: 3,
        name: '牛奶',
        category: '蛋奶',
        price: 6.90,
        stock: 180,
        imageUrl: '',
        description: '营养纯牛奶，早餐和日常补充都适合。',
        status: 1
      },
      {
        id: 4,
        name: '苹果',
        category: '水果',
        price: 8.80,
        stock: 160,
        imageUrl: '',
        description: '脆甜多汁苹果，适合家庭日常水果补充。',
        status: 1
      },
      {
        id: 5,
        name: '鸡胸肉',
        category: '肉禽',
        price: 15.90,
        stock: 120,
        imageUrl: '',
        description: '低脂鸡胸肉，适合煎、炒和健身餐。',
        status: 1
      }
    ];
  }

  static getProductDetail(): Product {
    return MockDataService.getProducts()[0];
  }

  static getCartItems(): CartItem[] {
    return [
      {
        cartId: 1,
        productId: 1,
        productName: '西红柿',
        productImageUrl: '',
        productPrice: 4.90,
        quantity: 2,
        subtotal: 9.80
      },
      {
        cartId: 2,
        productId: 4,
        productName: '苹果',
        productImageUrl: '',
        productPrice: 8.80,
        quantity: 1,
        subtotal: 8.80
      }
    ];
  }

  static getOrders(): OrderSummary[] {
    return [
      {
        orderId: 1,
        orderNo: 'FL202606110001',
        payAmount: 18.60,
        status: 'WAIT_PAY',
        items: [
          {
            productName: '西红柿',
            quantity: 2,
            subtotal: 9.80
          },
          {
            productName: '苹果',
            quantity: 1,
            subtotal: 8.80
          }
        ]
      }
    ];
  }
}
