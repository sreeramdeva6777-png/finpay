import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import InputField from '../components/InputField';
import Button from '../components/Button';
import { loginUser } from '../services/api';

function Login() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previousData) => ({
      ...previousData,
      [name]: value,
    }));

    setError('');
  };

  const validateForm = () => {
    if (!formData.email.trim()) {
      return 'Please enter your email address.';
    }

    if (!formData.password) {
      return 'Please enter your password.';
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

    try {
      setLoading(true);
      setError('');

      const data = await loginUser({
        email: formData.email.trim(),
        password: formData.password,
      });

      if (!data?.token) {
        throw new Error('Login succeeded, but no authentication token was received.');
      }

      localStorage.setItem('token', data.token);

      localStorage.setItem(
        'user',
        JSON.stringify({
          id: data.id,
          name: data.name,
          email: data.email,
        })
      );

      navigate('/dashboard', { replace: true });

    } catch (err) {
      setError(
        err.message || 'Invalid email or password.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">

      <div className="auth-brand">
        <div className="brand-mark">
          F
        </div>

        <div className="brand-text">
          <span>Fin</span>Pay
        </div>
      </div>

      <div className="auth-container">

        <div className="auth-card">

          <div className="auth-header">

            <span className="auth-eyebrow">
              WELCOME BACK
            </span>

            <h1>
              Sign in to FinPay
            </h1>

            <p>
              Access your wallet and manage your money securely.
            </p>

          </div>

          <form onSubmit={handleSubmit}>

            <InputField
              label="Email address"
              name="email"
              type="email"
              placeholder="you@example.com"
              value={formData.email}
              onChange={handleChange}
              disabled={loading}
              required
            />

            <InputField
              label="Password"
              name="password"
              type="password"
              placeholder="Enter your password"
              value={formData.password}
              onChange={handleChange}
              disabled={loading}
              required
            />

            {error && (
              <div className="auth-error">
                {error}
              </div>
            )}

            <Button
              type="submit"
              loading={loading}
            >
              Sign In
            </Button>

          </form>

          <div className="auth-footer">
            <span>
              Don't have an account?
            </span>

            <Link to="/register">
              Create account
            </Link>
          </div>

        </div>

        <div className="auth-note">
          <span>🔒</span>
          Secure authentication powered by FinPay.
        </div>

      </div>

    </div>
  );
}

export default Login;