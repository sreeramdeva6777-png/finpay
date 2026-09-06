import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';
import InputField from '../components/InputField';
import Button from '../components/Button';

import { transferMoney } from '../services/api';

function Transfer() {
  const navigate = useNavigate();

  const [user, setUser] = useState(() => {
    const storedUser = localStorage.getItem('user');

    return storedUser ? JSON.parse(storedUser) : null;
  });

  const [formData, setFormData] = useState({
    toUserId: '',
    amount: '',
  });

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');

    navigate('/login', { replace: true });
  };

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previousData) => ({
      ...previousData,
      [name]: value,
    }));

    setError('');
    setSuccess('');
  };

  const validateForm = () => {
    const receiverId = Number(formData.toUserId);
    const amount = Number(formData.amount);

    if (!formData.toUserId) {
      return 'Please enter the receiver user ID.';
    }

    if (!Number.isInteger(receiverId) || receiverId <= 0) {
      return 'Please enter a valid receiver user ID.';
    }

    if (user && receiverId === Number(user.id)) {
      return 'You cannot transfer money to yourself.';
    }

    if (!formData.amount || Number.isNaN(amount)) {
      return 'Please enter a transfer amount.';
    }

    if (amount <= 0) {
      return 'Transfer amount must be greater than zero.';
    }

    return '';
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const validationError = validateForm();

    if (validationError) {
      setError(validationError);
      return;
    }

    const token = localStorage.getItem('token');

    if (!token) {
      navigate('/login', { replace: true });
      return;
    }

    try {
      setLoading(true);
      setError('');
      setSuccess('');

      /*
       * One unique idempotency key is generated
       * for this payment attempt.
       *
       * If the same request needs to be retried,
       * the same key should be reused.
       */
      const idempotencyKey = crypto.randomUUID();

      await transferMoney(
        token,
        Number(formData.toUserId),
        Number(formData.amount),
        idempotencyKey
      );

      setSuccess(
        'Money transferred successfully.'
      );

      setFormData({
        toUserId: '',
        amount: '',
      });

    } catch (err) {
      setError(
        err.message || 'Unable to complete the transfer.'
      );
    } finally {
      setLoading(false);
    }
  };

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
                TRANSFER
              </span>

              <h1>
                Transfer Money
              </h1>

              <p>
                Send money securely to another FinPay user.
              </p>
            </div>
          </div>

          <div className="transfer-layout">

            <section className="dashboard-card transfer-card">

              <div className="transfer-card-header">
                <div className="transfer-icon">
                  ↗
                </div>

                <div>
                  <h2>
                    Send money
                  </h2>

                  <p>
                    Enter the recipient and amount below.
                  </p>
                </div>
              </div>

              <form onSubmit={handleSubmit}>

                <InputField
                  label="Receiver User ID"
                  name="toUserId"
                  type="number"
                  placeholder="Enter receiver user ID"
                  value={formData.toUserId}
                  onChange={handleChange}
                  disabled={loading}
                  required
                />

                <InputField
                  label="Amount"
                  name="amount"
                  type="number"
                  placeholder="Enter amount"
                  value={formData.amount}
                  onChange={handleChange}
                  disabled={loading}
                  required
                />

                {error && (
                  <div className="auth-error">
                    {error}
                  </div>
                )}

                {success && (
                  <div className="auth-success">
                    {success}
                  </div>
                )}

                <Button
                  type="submit"
                  loading={loading}
                >
                  Send Money
                </Button>

              </form>

            </section>

            <aside className="dashboard-card transfer-info-card">

              <div className="section-header">
                <div>
                  <h2>
                    Transfer details
                  </h2>

                  <p>
                    Your transaction is protected by FinPay.
                  </p>
                </div>
              </div>

              <div className="transfer-info-list">

                <div className="transfer-info-item">
                  <span>From</span>

                  <strong>
                    {user?.name || 'Current user'}
                  </strong>
                </div>

                <div className="transfer-info-item">
                  <span>Authentication</span>

                  <strong className="secure-label">
                    Secured
                  </strong>
                </div>

                <div className="transfer-info-item">
                  <span>Transaction protection</span>

                  <strong>
                    Idempotent
                  </strong>
                </div>

                <div className="transfer-info-item">
                  <span>Concurrency</span>

                  <strong>
                    Optimistic locking
                  </strong>
                </div>

              </div>

              <div className="transfer-security-note">
                <span>🔒</span>

                <p>
                  Never share your authentication token
                  with anyone.
                </p>
              </div>

            </aside>

          </div>

        </main>

      </div>

    </div>
  );
}

export default Transfer;