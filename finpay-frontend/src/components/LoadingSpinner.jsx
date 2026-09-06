function LoadingSpinner({ size = 'medium', text = '' }) {
  return (
    <div className={`loading-wrapper ${size}`}>
      <div
        className="loading-spinner"
        role="status"
        aria-label="Loading"
      >
        <span></span>
        <span></span>
        <span></span>
      </div>

      {text && (
        <p className="loading-text">
          {text}
        </p>
      )}
    </div>
  );
}

export default LoadingSpinner;