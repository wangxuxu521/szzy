/**
 * 日期格式化函数
 * @param {Date|string} date - 日期对象或日期字符串
 * @param {string} format - 格式化模板，默认为 "YYYY-MM-DD HH:mm:ss"
 * @returns {string} 格式化后的日期字符串
 */
export const formatDate = (date, format = "YYYY-MM-DD HH:mm:ss") => {
  if (!date) return "";

  // 如果是字符串，尝试转换为Date对象
  if (typeof date === "string") {
    date = new Date(date);
  }

  // 如果不是有效的日期对象，返回原始值或空字符串
  if (!(date instanceof Date) || isNaN(date.getTime())) {
    return typeof date === "string" ? date : "";
  }

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const seconds = String(date.getSeconds()).padStart(2, "0");

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

/**
 * 格式化数字（添加千分位分隔符）
 * @param {number} num - 数字
 * @returns {string} 格式化后的数字字符串
 */
export const formatNumber = (num) => {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};
