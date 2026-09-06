import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';
import Button from '../components/Button';
import InputField from '../components/InputField';
import LoadingSpinner from '../components/LoadingSpinner';

import {
  getBalance,
  createWallet,
  addMoney,
  deductMoney,
} from '../services/api';

function Wallet() {
  const navigate = useNavigate();

  const [user, setUser] = useState(null);

  const [balance, setBalance] = useState(null);
  const [walletExists, setWalletExists] = useState(false);

  const [addAmount, setAddAmount] = useState('');
  const [deductAmount, setDeductAmount] = useState('');

  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [sidebarOpen, setSidebarOpen] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');

    if (!token || !storedUser) {
      navigate('/login', { replace: true });
      return;
    }

    try {
      setUser(JSON.parse(storedUser));
    } catch {
      localStorage.removeItem('token');
      localStorage.removeItem('user');

      navigate('/login', { replace: true });
      return;
    }

    const loadWallet = async () => {
      try {
        setLoading(true);
        setError('');

        const data = await getBalance(token);

        setBalance(data.balance);
        setWalletExists(true);
      } catch (err) {
        if (
          err.message?.toLowerCase().includes('wallet not found')
        ) {
          setWalletExists(false);
          setBalance(null);
        } else {
          setError(
            err.message || 'Unable to load your wallet.'
          );
        }
      } finally {
        setLoading(false);
      }
    };

    loadWallet();
  }, [navigate]);

  const clearMessages = () => {
    setError('');
    setSuccess('');
  };

  const handleCreateWallet = async () => {
    const token = localStorage.getItem('token');

    if (!token) {
      navigate('/login', { replace: true });
      return;
    }

    try {
      setActionLoading(true);
      clearMessages();

      const data = await createWallet(token);

      setBalance(data.balance);
      setWalletExists(true);

      setSuccess('Your wallet has been created successfully.');
    } catch (err) {
      setError(
        err.message || 'Unable to create your wallet.'
      );
    } finally {
      setActionLoading(false);
    }
  };

  const validateAmount = (value) => {
    const numericAmount = Number(value);

    if (!value.trim()) {
      return 'Please enter an amount.';
    }

    if (!Number.isFinite(numericAmount)) {
      return 'Please enter a valid amount.';
    }

    if (numericAmount <= 0) {
      return 'Amount must be greater than zero.';
    }

    return '';
  };

  const handleAddMoney = async () => {
    const validationError = validateAmount(addAmount);

    if (validationError) {
      setError(validationError);
      setSuccess('');
      return;
    }

    const token = localStorage.getItem('token');

    if (!token) {
      navigate('/login', { replace: true });
      return;
    }

    try {
      setActionLoading(true);
      clearMessages();

      const data = await addMoney(
        token,
        Number(addAmount)
      );

      setBalance(data.balance);
      setAddAmount('');

      setSuccess(
        `₹${Number(addAmount).toLocaleString('en-IN')} added successfully.`
      );
    } catch (err) {
      setError(
        err.message || 'Unable to add money.'
      );
    } finally {
      setActionLoading(false);
    }
  };

  const handleDeductMoney = async () => {
    const validationError = validateAmount(deductAmount);

    if (validationError) {
      setError(validationError);
      setSuccess('');
      return;
    }

    const token = localStorage.getItem('token');

    if (!token) {
      navigate('/login', { replace: true });
      return;
    }

    try {
      setActionLoading(true);
      clearMessages();

      const data = await deductMoney(
        token,
        Number(deductAmount)
      );

      setBalance(data.balance);
      setDeductAmount('');

      setSuccess(
        `₹${Number(deductAmount).toLocaleString('en-IN')} deducted successfully.`
      );
    } catch (err) {
      setError(
        err.message || 'Unable to deduct money.'
      );
    } finally {
      setActionLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');

    navigate('/login', { replace: true });
  };

  const formattedBalance = Number(
    balance || 0
  ).toLocaleString('en-IN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });

  if (loading) {
    return (
      <div className="dashboard-loading">
        <LoadingSpinner text="Loading your wallet..." />
      </div>
    );
  }

  return (
    <div className="dashboard-layout">

      <Sidebar
        isOpen={sidebarOpen}
        onClose={() => setSidebarOpen(false)}
        onLogout={handleLogout}
      />

      <div className="dashboard-main">

        <Navbar
          userName={user?.name}
          onMenuClick={() => setSidebarOpen(true)}
        />

        <main className="dashboard-content">

          <div className="dashboard-heading">
            <div>
              <span className="dashboard-eyebrow">
                WALLET
              </span>

              <h1>
                Your Wallet
              </h1>

              <p>
                Manage your balance and wallet activity.
              </p>
            </div>
          </div>

          {error && (
            <div className="dashboard-error">
              {error}
            </div>
          )}

          {success && (
            <div className="auth-success">
              {success}
            </div>
          )}

          {!walletExists ? (
            <section className="dashboard-card wallet-create-card">

              <div className="wallet-create-icon">
                ₹
              </div>

              <div className="wallet-create-content">
                <h2>
                  Set up your FinPay wallet
                </h2>

                <p>
                  Create your wallet to start managing
                  your money and making transactions.
                </p>

                <Button
                  onClick={handleCreateWallet}
                  loading={actionLoading}
                >
                  Create Wallet
                </Button>
              </div>

            </section>
          ) : (
            <>
              <section className="dashboard-card balance-card wallet-balance-card">

                <div className="card-top">
                  <span className="card-label">
                    Available Balance
                  </span>

                  <div className="card-icon">
                    ₹
                  </div>
                </div>

                <div className="balance-value">
                  ₹{formattedBalance}
                </div>

                <span className="balance-caption">
                  Current wallet balance
                </span>

              </section>

              <section className="wallet-actions-grid">

                <div className="dashboard-card">

                  <div className="section-header">
                    <div>
                      <h2>
                        Add Money
                      </h2>

                      <p>
                        Add funds to your wallet.
                      </p>
                    </div>
                  </div>

                  <InputField
                    label="Amount"
                    name="addAmount"
                    type="number"
                    placeholder="Enter amount"
                    value={addAmount}
                    onChange={(event) => {
                      setAddAmount(event.target.value);
                      clearMessages();
                    }}
                    disabled={actionLoading}
                    required
                  />

                  <Button
                    onClick={handleAddMoney}
                    loading={actionLoading}
                  >
                    Add Money
                  </Button>

                </div>

                <div className="dashboard-card">

                  <div className="section-header">
                    <div>
                      <h2>
                        Deduct Money
                      </h2>

                      <p>
                        Remove funds from your wallet.
                      </p>
                    </div>
                  </div>

                  <InputField
                    label="Amount"
                    name="deductAmount"
                    type="number"
                    placeholder="Enter amount"
                    value={deductAmount}
                    onChange={(event) => {
                      setDeductAmount(event.target.value);
                      clearMessages();
                    }}
                    disabled={actionLoading}
                    required
                  />

                  <Button
                    variant="secondary"
                    onClick={handleDeductMoney}
                    loading={actionLoading}
                  >
                    Deduct Money
                  </Button>

                </div>

              </section>

              <section className="dashboard-card wallet-info-card">

                <div className="section-header">
                  <div>
                    <h2>
                      Wallet Information
                    </h2>

                    <p>
                      Your current wallet details.
                    </p>
                  </div>
                </div>

                <div className="wallet-info-row">
                  <span>
                    Wallet Status
                  </span>

                  <strong className="wallet-active">
                    Active
                  </strong>
                </div>

                <div className="wallet-info-row">
                  <span>
                    Account Holder
                  </span>

                  <strong>
                    {user?.name}
                  </strong>
                </div>

                <div className="wallet-info-row">
                  <span>
                    Email
                  </span>

                  <strong>
                    {user?.email}
                  </strong>
                </div>

              </section>

              <div className="wallet-navigation-actions">

                <button
                  type="button"
                  className="wallet-secondary-action"
                  onClick={() => navigate('/transfer')}
                >
                  <span>↗</span>
                  Transfer Money
                </button>

                <button
                  type="button"
                  className="wallet-secondary-action"
                  onClick={() => navigate('/history')}
                >
                  <span>◷</span>
                  View Transactions
                </button>

              </div>
            </>
          )}

        </main>

      </div>

    </div>
  );
}

export default Wallet;