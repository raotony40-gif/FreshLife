import { OrderItem, OrderSummary } from '../model/AppModels';
import { MockDataService } from '../service/MockDataService';
import { FormatUtils } from '../utils/FormatUtils';

@Component
export struct OrderPage {
  navPathStack: NavPathStack = new NavPathStack();
  private orders: OrderSummary[] = MockDataService.getOrders();

  build() {
    NavDestination() {
      List({ space: 12 }) {
        ForEach(this.orders, (order: OrderSummary) => {
          ListItem() {
            Column({ space: 12 }) {
              Row() {
                Text(order.orderNo)
                  .fontSize(16)
                  .fontWeight(FontWeight.Bold)
                  .fontColor('#17251C');
                Blank();
                Text(order.status)
                  .fontSize(13)
                  .fontColor('#2F7D4A');
              }
              .width('100%');

              ForEach(order.items, (item: OrderItem) => {
                Row() {
                  Text(`${item.productName} x ${item.quantity}`)
                    .fontSize(14)
                    .fontColor('#3D4A42');
                  Blank();
                  Text(FormatUtils.money(item.subtotal))
                    .fontSize(14)
                    .fontColor('#3D4A42');
                }
                .width('100%');
              }, (item: OrderItem) => item.productName);

              Divider();

              Row() {
                Text('实付')
                  .fontSize(14)
                  .fontColor('#6B766F');
                Blank();
                Text(FormatUtils.money(order.payAmount))
                  .fontSize(20)
                  .fontWeight(FontWeight.Bold)
                  .fontColor('#D9480F');
              }
              .width('100%');
            }
            .width('100%')
            .padding(14)
            .borderRadius(12)
            .backgroundColor('#FFFFFF');
          }
        }, (order: OrderSummary) => order.orderId.toString());
      }
      .width('100%')
      .height('100%')
      .padding(16)
      .backgroundColor('#F7FAF5');
    }
    .title('订单');
  }
}
