import { CartPage } from './CartPage';
import { OrderPage } from './OrderPage';
import { ProductDetailPage } from './ProductDetailPage';
import { ProductListPage } from './ProductListPage';
import { ApiResult, HttpService, UserLoginData } from '../service/HttpService';
import { AuthTokenStore } from '../utils/AuthTokenStore';
import { RouteConstants } from '../utils/RouteConstants';

@Entry
@Component
struct LoginPage {
  private navPathStack: NavPathStack = new NavPathStack();
  @State private username: string = 'tony';
  @State private password: string = '123456';
  @State private errorMessage: string = '';
  @State private loading: boolean = false;

  @Builder
  NavigationDestination(name: string) {
    if (name === RouteConstants.PRODUCT_LIST) {
      ProductListPage({ navPathStack: this.navPathStack })
    } else if (name === RouteConstants.PRODUCT_DETAIL) {
      ProductDetailPage({ navPathStack: this.navPathStack })
    } else if (name === RouteConstants.CART) {
      CartPage({ navPathStack: this.navPathStack })
    } else if (name === RouteConstants.ORDER) {
      OrderPage({ navPathStack: this.navPathStack })
    }
  }

  private async login(): Promise<void> {
    if (this.loading) {
      return;
    }

    this.errorMessage = '';
    this.loading = true;
    try {
      const result: ApiResult<UserLoginData> = await HttpService.login();
      if (result.code !== 0 || !result.data || result.data.token.length === 0) {
        this.errorMessage = result.message || '登录失败';
        return;
      }

      AuthTokenStore.saveToken(result.data.token);
      this.navPathStack.pushPathByName(RouteConstants.PRODUCT_LIST, null);
    } catch (error) {
      this.errorMessage = '登录失败，请确认后端服务已启动，账号 tony / 123456 可用';
    } finally {
      this.loading = false;
    }
  }

  build() {
    Navigation(this.navPathStack) {
      Column({ space: 24 }) {
        Column({ space: 8 }) {
          Text('FreshLife')
            .fontSize(36)
            .fontWeight(FontWeight.Bold)
            .fontColor('#1F6B3A');
          Text('社区生鲜，到家更快')
            .fontSize(16)
            .fontColor('#5F6F64');
        }
        .width('100%')
        .alignItems(HorizontalAlign.Start);

        Column({ space: 14 }) {
          TextInput({ text: this.username, placeholder: '请输入用户名' })
            .height(48)
            .borderRadius(10)
            .backgroundColor('#FFFFFF')
            .onChange((value: string) => {
              this.username = value;
            });
          TextInput({ text: this.password, placeholder: '请输入密码' })
            .height(48)
            .type(InputType.Password)
            .borderRadius(10)
            .backgroundColor('#FFFFFF')
            .onChange((value: string) => {
              this.password = value;
            });

          if (this.errorMessage.length > 0) {
            Text(this.errorMessage)
              .width('100%')
              .fontSize(13)
              .fontColor('#C92A2A');
          }

          Button(this.loading ? '登录中...' : '登录并进入商品列表')
            .width('100%')
            .height(48)
            .backgroundColor('#2F7D4A')
            .onClick(() => {
              this.login();
            });
        }
        .width('100%');
      }
      .width('100%')
      .height('100%')
      .padding(24)
      .justifyContent(FlexAlign.Center)
      .backgroundColor('#F7FAF5');
    }
    .title('登录')
    .hideBackButton(true)
    .navDestination(this.NavigationDestination);
  }
}
