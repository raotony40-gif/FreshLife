export interface Product {
  id: number;
  name: string;
  category: string;
  price: number;
  stock: number;
  imageUrl: string;
  description: string;
  status: number;
}

export interface CartItem {
  cartId: number;
  productId: number;
  productName: string;
  productImageUrl: string;
  productPrice: number;
  quantity: number;
  subtotal: number;
}

export interface OrderItem {
  productName: string;
  quantity: number;
  subtotal: number;
}

export interface OrderSummary {
  orderId: number;
  orderNo: string;
  payAmount: number;
  status: string;
  items: OrderItem[];
}
