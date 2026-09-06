import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import InputField from '../components/InputField';
import Button from '../components/Button';
import { registerUser } from '../services/api';

function Register() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
  });

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

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
    const name = formData.name.trim();
    const email = formData.email.trim();
    const password = formData.password;

    if (!name) {
      return 'Please enter your full name.';
    }

    if (name.length < 2) {
      return 'Name must contain at least 2 characters.';
    }

    if (!email) {
      return 'Please enter your email address.';
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      return 'Please enter a valid email address.';
    }

    if (!password) {
      return 'Please create a password.';
    }

    if (password.length < 6) {
      return 'Password must contain at least 6 characters.';
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
      setSuccess('');

      await registerUser({
        name: formData.name.trim(),
        email: formData.email.trim(),
        password: formData.password,
      });

      setSuccess(
        'Account created successfully. Redirecting to login...'
      );

      setTimeout(() => {
        navigate('/login', { replace: true });
      }, 1000);

    } catch (err) {
      setError(
        err.message || 'Unable to create your account. Please try again.'
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
              GET STARTED
            </span>

            <h1>
              Create your account
            </h1>

            <p>
              Join FinPay and manage your money with confidence.
            </p>

          </div>

          <form onSubmit={handleSubmit}>

            <InputField
              label="Full name"
              name="name"
              type="text"
              placeholder="Enter your full name"
              value={formData.name}
              onChange={handleChange}
              disabled={loading}
              required
            />

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
              placeholder="Create a secure password"
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

            {success && (
              <div className="auth-success">
                {success}
              </div>
            )}

            <Button
              type="submit"
              loading={loading}
            >
              Create Account
            </Button>

          </form>

          <div className="auth-footer">
            <span>
              Already have an account?
            </span>

            <Link to="/login">
              Sign in
            </Link>
          </div>

        </div>

        <div className="auth-note">
          <span>🔒</span>
          Your information is securely handled by FinPay.
        </div>

      </div>

    </div>
  );
}

export default Register;