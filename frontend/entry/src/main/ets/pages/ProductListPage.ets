import { Product } from '../model/AppModels';
import { MockDataService } from '../service/MockDataService';
import { FormatUtils } from '../utils/FormatUtils';
import { RouteConstants } from '../utils/RouteConstants';

@Component
export struct ProductListPage {
  navPathStack: NavPathStack = new NavPathStack();
  private products: Product[] = MockDataService.getProducts();

  build() {
    NavDestination() {
      Column({ space: 12 }) {
        Row() {
          Text('今日生鲜')
            .fontSize(24)
            .fontWeight(FontWeight.Bold)
            .fontColor('#17251C');
          Blank();
          Button('购物车')
            .height(36)
            .backgroundColor('#2F7D4A')
            .onClick(() => {
              this.navPathStack.pushPathByName(RouteConstants.CART, null);
            });
        }
        .width('100%');

        List({ space: 12 }) {
          ForEach(this.products, (product: Product) => {
            ListItem() {
              Row({ space: 12 }) {
                Column() {
                  Text(product.name.substring(0, 1))
                    .fontSize(26)
                    .fontWeight(FontWeight.Bold)
                    .fontColor('#2F7D4A');
                }
                .width(64)
                .height(64)
                .borderRadius(12)
                .justifyContent(FlexAlign.Center)
                .alignItems(HorizontalAlign.Center)
                .backgroundColor('#E8F5E9');

                Column({ space: 6 }) {
                  Text(product.name)
                    .fontSize(18)
                    .fontWeight(FontWeight.Medium)
                    .fontColor('#17251C');
                  Text(`${product.category} · 库存 ${product.stock}`)
                    .fontSize(13)
                    .fontColor('#6B766F');
                  Text(FormatUtils.money(product.price))
                    .fontSize(18)
                    .fontWeight(FontWeight.Bold)
                    .fontColor('#D9480F');
                }
                .layoutWeight(1)
                .alignItems(HorizontalAlign.Start);
              }
              .width('100%')
              .padding(14)
              .borderRadius(12)
              .backgroundColor('#FFFFFF')
              .onClick(() => {
                this.navPathStack.pushPathByName(RouteConstants.PRODUCT_DETAIL, product.id);
              });
            }
          }, (product: Product) => product.id.toString());
        }
        .layoutWeight(1)
        .width('100%');
      }
      .width('100%')
      .height('100%')
      .padding(16)
      .backgroundColor('#F7FAF5');
    }
    .title('商品列表');
  }
}
