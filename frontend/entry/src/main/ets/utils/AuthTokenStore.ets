export class AuthTokenStore {
  private static readonly TOKEN_KEY: string = 'freshlife_token';

  static saveToken(token: string): void {
    AppStorage.setOrCreate<string>(AuthTokenStore.TOKEN_KEY, token);
  }

  static getToken(): string {
    return AppStorage.get<string>(AuthTokenStore.TOKEN_KEY) ?? '';
  }

  static clearToken(): void {
    AppStorage.setOrCreate<string>(AuthTokenStore.TOKEN_KEY, '');
  }
}
