/**
 * 格式化时间
 */
export function formatDate(dateStr: string | Date, fmt: string = 'YYYY-MM-DD'): string {
  const date = new Date(dateStr);
  const o: Record<string, number> = {
    'M+': date.getMonth() + 1,
    'D+': date.getDate(),
    'h+': date.getHours(),
    'm+': date.getMinutes(),
    's+': date.getSeconds(),
  };
  if (/(Y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
  }
  for (const k in o) {
    if (new RegExp('(' + k + ')').test(fmt)) {
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k] + '') : (('00' + o[k]).substr(('' + o[k]).length)));
    }
  }
  return fmt;
}

/**
 * 格式化数字（例如评分保留一位小数）
 */
export function formatNumber(num: number | string, decimals: number = 1): string {
  return Number(num).toFixed(decimals);
}