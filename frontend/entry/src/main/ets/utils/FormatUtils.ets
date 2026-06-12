export class FormatUtils {
  static money(value: number): string {
    return `¥${value.toFixed(2)}`;
  }
}
