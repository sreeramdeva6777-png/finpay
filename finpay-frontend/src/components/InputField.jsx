function InputField({
  label,
  name,
  type = 'text',
  placeholder = '',
  value = '',
  onChange,
  error = '',
  disabled = false,
  required = false,
}) {
  return (
    <div className="input-field">
      <label htmlFor={name}>
        {label}

        {required && (
          <span className="required-mark">*</span>
        )}
      </label>

      <input
        id={name}
        name={name}
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        disabled={disabled}
        aria-invalid={Boolean(error)}
        autoComplete={
          type === 'password'
            ? 'current-password'
            : type === 'email'
              ? 'email'
              : 'off'
        }
        className={error ? 'input-error' : ''}
      />

      {error && (
        <span className="input-error-message">
          {error}
        </span>
      )}
    </div>
  );
}

export default InputField;