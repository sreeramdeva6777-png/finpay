function Button({
  children,
  type = 'button',
  onClick,
  disabled = false,
  loading = false,
  variant = 'primary',
  fullWidth = true,
}) {
  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled || loading}
      className={`app-button ${variant} ${
        fullWidth ? 'full-width' : ''
      }`}
    >
      {loading ? (
        <span className="button-loading">
          <span className="button-spinner"></span>
          Please wait...
        </span>
      ) : (
        children
      )}
    </button>
  );
}

export default Button;