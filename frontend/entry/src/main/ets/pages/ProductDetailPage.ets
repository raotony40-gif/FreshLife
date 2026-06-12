import { Product } from '../model/AppModels';
import { MockDataService } from '../service/MockDataService';
import { FormatUtils } from '../utils/FormatUtils';
import { RouteConstants } from '../utils/RouteConstants';

@Component
export struct ProductDetailPage {
  navPathStack: NavPathStack = new NavPathStack();
  private product: Product = MockDataService.getProductDetail();

  build() {
    NavDestination() {
      Column({ space: 18 }) {
        Column() {
          Text(this.product.name.substring(0, 1))
            .fontSize(56)
            .fontWeight(FontWeight.Bold)
            .fontColor('#2F7D4A');
        }
        .width('100%')
        .height(180)
        .borderRadius(16)
        .justifyContent(FlexAlign.Center)
        .alignItems(HorizontalAlign.Center)
        .backgroundColor('#E8F5E9');

        Column({ space: 10 }) {
          Text(this.product.name)
            .fontSize(26)
            .fontWeight(FontWeight.Bold)
            .fontColor('#17251C');
          Text(this.product.category)
            .fontSize(14)
            .fontColor('#5F6F64');
          Text(FormatUtils.money(this.product.price))
            .fontSize(28)
            .fontWeight(FontWeight.Bold)
            .fontColor('#D9480F');
          Text(this.product.description)
            .fontSize(15)
            .fontColor('#3D4A42')
            .lineHeight(22);
          Text(`库存 ${this.product.stock}`)
            .fontSize(14)
            .fontColor('#6B766F');
        }
        .width('100%')
        .alignItems(HorizontalAlign.Start);

        Blank();

        Button('加入购物车')
          .width('100%')
          .height(48)
          .backgroundColor('#2F7D4A')
          .onClick(() => {
            this.navPathStack.pushPathByName(RouteConstants.CART, null);
          });
      }
      .width('100%')
      .height('100%')
      .padding(16)
      .backgroundColor('#F7FAF5');
    }
    .title('商品详情');
  }
}
