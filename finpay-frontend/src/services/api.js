const API_BASE_URL = 'http://localhost:8080';

async function apiRequest(endpoint, options = {}) {
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
  });

  const contentType = response.headers.get('content-type');

  let data = null;

  if (contentType?.includes('application/json')) {
    data = await response.json();
  } else {
    data = await response.text();
  }

  if (!response.ok) {
    const message =
      typeof data === 'string'
        ? data
        : data?.message || 'Something went wrong';

    throw new Error(message);
  }

  return data;
}

export async function registerUser(userData) {
  return apiRequest('/users/register', {
    method: 'POST',
    body: JSON.stringify(userData),
  });
}

export async function loginUser(credentials) {
  return apiRequest('/users/login', {
    method: 'POST',
    body: JSON.stringify(credentials),
  });
}

export async function createWallet(token) {
  return apiRequest('/wallets/create', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
}

export async function getBalance(token) {
  return apiRequest('/wallets/balance', {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
}

export async function addMoney(token, amount) {
  return apiRequest('/wallets/add-money', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({
      amount,
    }),
  });
}

export async function deductMoney(token, amount) {
  return apiRequest('/wallets/deduct-money', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({
      amount,
    }),
  });
}

export async function transferMoney(
  token,
  toUserId,
  amount,
  idempotencyKey
) {
  return apiRequest('/wallets/transfer', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({
      toUserId,
      amount,
      idempotencyKey,
    }),
  });
}

export async function getTransactionHistory(token) {
  return apiRequest('/transactions/history', {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
}