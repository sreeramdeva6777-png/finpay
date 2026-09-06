import { NavLink, useNavigate } from 'react-router-dom';

function Sidebar({ isOpen, onClose, onLogout }) {
  const navigate = useNavigate();

  const menuItems = [
    {
      label: 'Dashboard',
      path: '/dashboard',
      icon: '⌂',
    },
    {
      label: 'Wallet',
      path: '/wallet',
      icon: '▣',
    },
    {
      label: 'Transfer Money',
      path: '/transfer',
      icon: '↗',
    },
    {
      label: 'Transaction History',
      path: '/history',
      icon: '◷',
    },
  ];

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');

    onClose?.();
    onLogout?.();

    navigate('/login', { replace: true });
  };

  return (
    <>
      {isOpen && (
        <div
          className="sidebar-overlay"
          onClick={onClose}
        ></div>
      )}

      <aside className={`sidebar ${isOpen ? 'open' : ''}`}>

        <div className="sidebar-header">

          <div className="sidebar-brand">
            <div className="brand-mark">F</div>

            <div className="brand-text">
              <span>Fin</span>Pay
            </div>
          </div>

          <button
            type="button"
            className="sidebar-close-button"
            onClick={onClose}
            aria-label="Close navigation"
          >
            ×
          </button>

        </div>

        <div className="sidebar-section">

          <span className="sidebar-section-title">
            MAIN MENU
          </span>

          <nav className="sidebar-menu">

            {menuItems.map((item) => (
              <NavLink
                key={item.path}
                to={item.path}
                end={item.path === '/dashboard'}
                onClick={onClose}
                className={({ isActive }) =>
                  `sidebar-menu-item ${
                    isActive ? 'active' : ''
                  }`
                }
              >
                <span className="sidebar-menu-icon">
                  {item.icon}
                </span>

                <span>{item.label}</span>
              </NavLink>
            ))}

          </nav>

        </div>

        <div className="sidebar-bottom">

          <button
            type="button"
            className="sidebar-menu-item logout-item"
            onClick={handleLogout}
          >
            <span className="sidebar-menu-icon">
              ↪
            </span>

            <span>Logout</span>
          </button>

        </div>

      </aside>
    </>
  );
}

export default Sidebar;