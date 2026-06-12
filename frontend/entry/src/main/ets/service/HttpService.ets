import axios, { AxiosHeaders, AxiosInstance, AxiosResponse } from '@ohos/axios';
import { AuthTokenStore } from '../utils/AuthTokenStore';

export interface ApiResult<T> {
  code: number;
  message: string;
  data: T;
}

export interface UserInfo {
  id: number;
  username: string;
  phone: string;
  nickname: string;
  avatarUrl: string;
  status: number;
}

export interface UserLoginData {
  token: string;
  userInfo: UserInfo;
}

export interface UserLoginRequest {
  username: string;
  password: string;
}

export class HttpService {
  private static readonly baseURL: string = 'http://127.0.0.1:8080/api';
  private static readonly client: AxiosInstance = axios.create({
    baseURL: HttpService.baseURL,
    timeout: 5000
  });

  static async login(): Promise<ApiResult<UserLoginData>> {
    const requestBody: UserLoginRequest = {
      username: 'tony',
      password: '123456'
    };
    return HttpService.post<UserLoginData>('/user/login', requestBody);
  }

  static async get<T>(url: string, params?: object): Promise<T> {
    const response: AxiosResponse<T> = await HttpService.client.get(url, {
      params,
      headers: HttpService.buildHeaders()
    });
    return response.data;
  }

  static async post<T>(url: string, data?: object): Promise<ApiResult<T>> {
    const response: AxiosResponse<ApiResult<T>> = await HttpService.client.post(url, data, {
      headers: HttpService.buildHeaders()
    });
    return response.data;
  }

  private static buildHeaders(): AxiosHeaders {
    const headers: AxiosHeaders = new AxiosHeaders();
    headers.set('Content-Type', 'application/json');

    const token: string = AuthTokenStore.getToken();
    if (token.length > 0) {
      headers.set('Authorization', `Bearer ${token}`);
    }

    return headers;
  }
}
