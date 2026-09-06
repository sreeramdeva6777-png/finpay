import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';
import LoadingSpinner from '../components/LoadingSpinner';
import TransactionItem from '../components/TransactionItem';

import {
  getBalance,
  getTransactionHistory,
} from '../services/api';

function Dashboard() {
  const navigate = useNavigate();

  const [user, setUser] = useState(null);
  const [balance, setBalance] = useState(null);
  const [transactions, setTransactions] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [sidebarOpen, setSidebarOpen] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');

    if (!token || !storedUser) {
      navigate('/login', { replace: true });
      return;
    }

    let parsedUser;

    try {
      parsedUser = JSON.parse(storedUser);
    } catch {
      localStorage.removeItem('token');
      localStorage.removeItem('user');

      navigate('/login', { replace: true });
      return;
    }

    setUser(parsedUser);

    const loadDashboard = async () => {
      try {
        setLoading(true);
        setError('');

        const [balanceData, transactionData] =
          await Promise.all([
            getBalance(token),
            getTransactionHistory(token),
          ]);

        setBalance(balanceData);
        setTransactions(
          Array.isArray(transactionData)
            ? transactionData
            : []
        );
      } catch (err) {
        setError(
          err.message ||
            'Unable to load your dashboard.'
        );
      } finally {
        setLoading(false);
      }
    };

    loadDashboard();
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');

    navigate('/login', { replace: true });
  };

  const formattedBalance = Number(
    balance?.balance || 0
  ).toLocaleString('en-IN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });

  if (loading) {
    return (
      <div className="dashboard-loading">
        <LoadingSpinner
          text="Loading your FinPay dashboard..."
        />
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
          onMenuClick={() =>
            setSidebarOpen(true)
          }
        />

        <main className="dashboard-content">

          <div className="dashboard-heading">

            <div>
              <span className="dashboard-eyebrow">
                OVERVIEW
              </span>

              <h1>
                Welcome back,{' '}
                {user?.name?.split(' ')[0] || 'User'}
              </h1>

              <p>
                Here's what's happening with your
                FinPay account.
              </p>
            </div>

          </div>

          {error && (
            <div className="dashboard-error">
              {error}
            </div>
          )}

          <section className="dashboard-stats">

            <div className="dashboard-card balance-card">

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

            </div>

            <div className="dashboard-card">

              <div className="card-top">

                <span className="card-label">
                  Total Transactions
                </span>

                <div className="card-icon">
                  ↗
                </div>

              </div>

              <div className="stat-value">
                {transactions.length}
              </div>

              <span className="stat-caption">
                Recorded transactions
              </span>

            </div>

          </section>

          <section className="dashboard-grid">

            <div className="dashboard-card quick-actions">

              <div className="section-header">

                <div>
                  <h2>
                    Quick Actions
                  </h2>

                  <p>
                    Manage your wallet
                  </p>
                </div>

              </div>

              <div className="action-buttons">

                <button
                  type="button"
                  className="quick-action primary-action"
                  onClick={() =>
                    navigate('/wallet')
                  }
                >
                  <span className="quick-action-icon">
                    +
                  </span>

                  <div>
                    <strong>
                      Add Money
                    </strong>

                    <span>
                      Fund your wallet
                    </span>
                  </div>
                </button>

                <button
                  type="button"
                  className="quick-action"
                  onClick={() =>
                    navigate('/transfer')
                  }
                >
                  <span className="quick-action-icon">
                    ↗
                  </span>

                  <div>
                    <strong>
                      Transfer Money
                    </strong>

                    <span>
                      Send money securely
                    </span>
                  </div>
                </button>

              </div>

            </div>

            <div className="dashboard-card account-card">

              <div className="section-header">

                <div>
                  <h2>
                    Account
                  </h2>

                  <p>
                    Your FinPay profile
                  </p>
                </div>

              </div>

              <div className="account-details">

                <div className="account-avatar">
                  {user?.name
                    ?.charAt(0)
                    .toUpperCase() || 'U'}
                </div>

                <div>
                  <strong>
                    {user?.name}
                  </strong>

                  <span>
                    {user?.email}
                  </span>
                </div>

              </div>

              <div className="account-status">

                <span className="status-dot"></span>

                Account active

              </div>

            </div>

          </section>

          <section className="dashboard-card transactions-card">

            <div className="section-header">

              <div>
                <h2>
                  Recent Transactions
                </h2>

                <p>
                  Your latest wallet activity
                </p>
              </div>

              {transactions.length > 0 && (
                <button
                  type="button"
                  className="view-all-button"
                  onClick={() =>
                    navigate('/history')
                  }
                >
                  View all
                </button>
              )}

            </div>

            {transactions.length === 0 ? (

              <div className="empty-state">

                <div className="empty-state-icon">
                  ◷
                </div>

                <h3>
                  No transactions yet
                </h3>

                <p>
                  Your recent transactions will
                  appear here.
                </p>

                <button
                  type="button"
                  className="empty-action-button"
                  onClick={() =>
                    navigate('/transfer')
                  }
                >
                  Make a transfer
                </button>

              </div>

            ) : (

              <div className="transaction-list">

                {transactions
                  .slice(0, 5)
                  .map((transaction) => (
                    <TransactionItem
                      key={
                        transaction.transactionId
                      }
                      transaction={transaction}
                      currentUserId={user?.id}
                    />
                  ))}

              </div>

            )}

          </section>

        </main>

      </div>

    </div>
  );
}

export default Dashboard;