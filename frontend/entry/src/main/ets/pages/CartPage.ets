import { CartItem } from '../model/AppModels';
import { MockDataService } from '../service/MockDataService';
import { FormatUtils } from '../utils/FormatUtils';
import { RouteConstants } from '../utils/RouteConstants';

@Component
export struct CartPage {
  navPathStack: NavPathStack = new NavPathStack();
  private cartItems: CartItem[] = MockDataService.getCartItems();

  private totalAmount(): number {
    return this.cartItems.reduce((sum: number, item: CartItem) => sum + item.subtotal, 0);
  }

  build() {
    NavDestination() {
      Column({ space: 12 }) {
        List({ space: 12 }) {
          ForEach(this.cartItems, (item: CartItem) => {
            ListItem() {
              Row({ space: 12 }) {
                Column() {
                  Text(item.productName.substring(0, 1))
                    .fontSize(24)
                    .fontWeight(FontWeight.Bold)
                    .fontColor('#2F7D4A');
                }
                .width(56)
                .height(56)
                .borderRadius(10)
                .justifyContent(FlexAlign.Center)
                .alignItems(HorizontalAlign.Center)
                .backgroundColor('#E8F5E9');

                Column({ space: 6 }) {
                  Text(item.productName)
                    .fontSize(17)
                    .fontWeight(FontWeight.Medium);
                  Text(`${FormatUtils.money(item.productPrice)} x ${item.quantity}`)
                    .fontSize(13)
                    .fontColor('#6B766F');
                  Text(FormatUtils.money(item.subtotal))
                    .fontSize(16)
                    .fontWeight(FontWeight.Bold)
                    .fontColor('#D9480F');
                }
                .layoutWeight(1)
                .alignItems(HorizontalAlign.Start);
              }
              .padding(14)
              .borderRadius(12)
              .backgroundColor('#FFFFFF');
            }
          }, (item: CartItem) => item.cartId.toString());
        }
        .layoutWeight(1)
        .width('100%');

        Row() {
          Column({ space: 4 }) {
            Text('合计')
              .fontSize(13)
              .fontColor('#6B766F');
            Text(FormatUtils.money(this.totalAmount()))
              .fontSize(22)
              .fontWeight(FontWeight.Bold)
              .fontColor('#D9480F');
          }
          .alignItems(HorizontalAlign.Start);
          Blank();
          Button('去下单')
            .width(128)
            .height(46)
            .backgroundColor('#2F7D4A')
            .onClick(() => {
              this.navPathStack.pushPathByName(RouteConstants.ORDER, null);
            });
        }
        .width('100%')
        .padding(14)
        .borderRadius(12)
        .backgroundColor('#FFFFFF');
      }
      .width('100%')
      .height('100%')
      .padding(16)
      .backgroundColor('#F7FAF5');
    }
    .title('购物车');
  }
}
