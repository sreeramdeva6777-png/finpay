import { useNavigate } from 'react-router-dom';

function Navbar({ userName, onMenuClick }) {
  const navigate = useNavigate();

  const getInitial = () => {
    if (!userName) {
      return 'U';
    }

    return userName.charAt(0).toUpperCase();
  };

  const handleProfileClick = () => {
    navigate('/dashboard');
  };

  return (
    <header className="navbar">

      <div className="navbar-left">

        <button
          type="button"
          className="mobile-menu-button"
          onClick={onMenuClick}
          aria-label="Open navigation menu"
        >
          ☰
        </button>

        <button
          type="button"
          className="navbar-mobile-brand"
          onClick={() => navigate('/dashboard')}
        >
          <div className="brand-mark">F</div>

          <div className="brand-text">
            <span>Fin</span>Pay
          </div>
        </button>

      </div>

      <div className="navbar-right">

        <button
          type="button"
          className="user-profile"
          onClick={handleProfileClick}
          title="Go to dashboard"
        >
          <div className="user-avatar">
            {getInitial()}
          </div>

          <div className="user-info">
            <span className="user-name">
              {userName || 'User'}
            </span>

            <span className="user-role">
              Personal Account
            </span>
          </div>
        </button>

      </div>

    </header>
  );
}

export default Navbar;