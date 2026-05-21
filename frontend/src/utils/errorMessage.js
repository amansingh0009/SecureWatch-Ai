export function errorMessage(error, fallback) {
  const data = error?.response?.data;
  if (data?.message) return data.message;
  if (data?.fields) return Object.entries(data.fields).map(([field, message]) => `${field}: ${message}`).join(', ');
  if (error?.message === 'Network Error') return 'Cannot reach backend at http://localhost:8080';
  return fallback;
}
