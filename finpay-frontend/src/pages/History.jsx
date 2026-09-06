import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';
import LoadingSpinner from '../components/LoadingSpinner';
import TransactionItem from '../components/TransactionItem';

import { getTransactionHistory } from '../services/api';

function History() {
  const navigate = useNavigate();

  const [user, setUser] = useState(null);
  const [transactions, setTransactions] = useState([]);

  const [filter, setFilter] = useState('ALL');

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

    const parsedUser = JSON.parse(storedUser);
    setUser(parsedUser);

    const loadHistory = async () => {
      try {
        setLoading(true);
        setError('');

        const data = await getTransactionHistory(token);

        setTransactions(
          Array.isArray(data) ? data : []
        );
      } catch (err) {
        setError(
          err.message || 'Unable to load transaction history.'
        );
      } finally {
        setLoading(false);
      }
    };

    loadHistory();
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');

    navigate('/login', { replace: true });
  };

  const filteredTransactions = useMemo(() => {
    if (filter === 'ALL') {
      return transactions;
    }

    return transactions.filter((transaction) => {
      const isSent =
        Number(transaction.senderId) === Number(user?.id);

      if (filter === 'SENT') {
        return isSent;
      }

      if (filter === 'RECEIVED') {
        return !isSent;
      }

      return true;
    });
  }, [transactions, filter, user]);

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

          <div className="dashboard-heading history-heading">
            <div>
              <span className="dashboard-eyebrow">
                ACTIVITY
              </span>

              <h1>
                Transaction History
              </h1>

              <p>
                Review all your recent wallet transactions.
              </p>
            </div>

            <button
              type="button"
              className="history-transfer-button"
              onClick={() => navigate('/transfer')}
            >
              <span>↗</span>
              New Transfer
            </button>
          </div>

          {error && (
            <div className="dashboard-error">
              {error}
            </div>
          )}

          <section className="dashboard-card history-card">

            <div className="history-toolbar">

              <div>
                <h2>All Transactions</h2>

                <p>
                  {transactions.length}{' '}
                  {transactions.length === 1
                    ? 'transaction'
                    : 'transactions'}
                </p>
              </div>

              <div className="history-filters">

                <button
                  type="button"
                  className={
                    filter === 'ALL'
                      ? 'history-filter active'
                      : 'history-filter'
                  }
                  onClick={() => setFilter('ALL')}
                >
                  All
                </button>

                <button
                  type="button"
                  className={
                    filter === 'SENT'
                      ? 'history-filter active'
                      : 'history-filter'
                  }
                  onClick={() => setFilter('SENT')}
                >
                  Sent
                </button>

                <button
                  type="button"
                  className={
                    filter === 'RECEIVED'
                      ? 'history-filter active'
                      : 'history-filter'
                  }
                  onClick={() => setFilter('RECEIVED')}
                >
                  Received
                </button>

              </div>

            </div>

            {loading ? (

              <div className="history-loading">
                <LoadingSpinner text="Loading transactions..." />
              </div>

            ) : filteredTransactions.length === 0 ? (

              <div className="empty-state history-empty">

                <div className="empty-state-icon">
                  ◷
                </div>

                <h3>
                  {filter === 'ALL'
                    ? 'No transactions yet'
                    : `No ${filter.toLowerCase()} transactions`}
                </h3>

                <p>
                  Your wallet activity will appear here.
                </p>

                {filter === 'ALL' && (
                  <button
                    type="button"
                    className="empty-action-button"
                    onClick={() => navigate('/transfer')}
                  >
                    Make your first transfer
                  </button>
                )}

              </div>

            ) : (

              <div className="history-transaction-list">

                {filteredTransactions.map((transaction) => (
                  <TransactionItem
                    key={transaction.transactionId}
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

export default History;