/**
 * 日期格式化函数
 * @param {string|number|Date} date - 日期字符串、时间戳或Date对象
 * @param {string} format - 格式化模式，默认为 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的日期字符串
 */
export const formatDate = (date, format = "YYYY-MM-DD HH:mm:ss") => {
  if (!date) return "";

  // 转换为Date对象
  let dateObj;
  if (date instanceof Date) {
    dateObj = date;
  } else if (typeof date === "number" || /^\d+$/.test(date)) {
    // 如果是数字或纯数字字符串，按时间戳处理
    dateObj = new Date(parseInt(date));
  } else {
    // 字符串日期
    dateObj = new Date(date);
  }

  // 检查是否有效日期
  if (isNaN(dateObj.getTime())) {
    return "";
  }

  const year = dateObj.getFullYear();
  const month = String(dateObj.getMonth() + 1).padStart(2, "0");
  const day = String(dateObj.getDate()).padStart(2, "0");
  const hours = String(dateObj.getHours()).padStart(2, "0");
  const minutes = String(dateObj.getMinutes()).padStart(2, "0");
  const seconds = String(dateObj.getSeconds()).padStart(2, "0");

  // 根据format替换对应部分
  return format
    .replace("YYYY", year)
    .replace("MM", month)
    .replace("DD", day)
    .replace("HH", hours)
    .replace("mm", minutes)
    .replace("ss", seconds);
};

/**
 * 文件大小格式化
 * @param {number} bytes - 字节数
 * @param {number} decimals - 小数位数，默认为2
 * @returns {string} 格式化后的文件大小
 */
export const formatFileSize = (bytes, decimals = 2) => {
  if (bytes === 0) return "0 Bytes";

  const k = 1024;
  const sizes = ["Bytes", "KB", "MB", "GB", "TB", "PB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return (
    parseFloat((bytes / Math.pow(k, i)).toFixed(decimals)) + " " + sizes[i]
  );
};

/**
 * 截取字符串并添加省略号
 * @param {string} str - 原始字符串
 * @param {number} length - 截取长度
 * @returns {string} 截取后的字符串
 */
export const truncateString = (str, length = 30) => {
  if (!str) return "";
  return str.length > length ? str.substring(0, length) + "..." : str;
};

/**
 * 格式化金额
 * @param {number} amount - 金额
 * @param {string} currency - 货币符号，默认为 '¥'
 * @returns {string} 格式化后的金额
 */
export const formatCurrency = (amount, currency = "¥") => {
  if (amount === null || amount === undefined) return "";
  return `${currency}${parseFloat(amount).toFixed(2)}`;
};

/**
 * 价格展示格式化（免费或价格）
 * @param {number} price - 价格
 * @returns {string} 格式化后的价格显示
 */
export const formatPrice = (price) => {
  if (!price || price <= 0) return "免费";
  return formatCurrency(price);
};
