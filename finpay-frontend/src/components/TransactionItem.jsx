function TransactionItem({ transaction, currentUserId }) {
  const isSent =
    Number(transaction.senderId) === Number(currentUserId);

  const formattedAmount = Number(
    transaction.amount
  ).toLocaleString('en-IN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });

  const formattedDate = transaction.createdAt
    ? new Date(transaction.createdAt).toLocaleString('en-IN', {
        dateStyle: 'medium',
        timeStyle: 'short',
      })
    : 'Date unavailable';

  const isSuccess = transaction.status === 'SUCCESS';

  return (
    <div className="transaction-item">
      <div
        className={`transaction-icon ${
          isSent ? 'sent-icon' : 'received-icon'
        }`}
      >
        {isSent ? '↗' : '↙'}
      </div>

      <div className="transaction-details">
        <h4>
          {isSent ? 'Money Sent' : 'Money Received'}
        </h4>

        <p>
          {isSent
            ? `To User #${transaction.receiverId}`
            : `From User #${transaction.senderId}`}
        </p>

        <span>{formattedDate}</span>
      </div>

      <div className="transaction-amount">
        <strong className={isSent ? 'sent' : 'received'}>
          {isSent ? '-' : '+'} ₹{formattedAmount}
        </strong>

        <span
          className={
            isSuccess ? 'transaction-success' : 'transaction-failed'
          }
        >
          {transaction.status}
        </span>
      </div>
    </div>
  );
}

export default TransactionItem;