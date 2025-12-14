import React from 'react';
import { AlertCircle, X } from 'lucide-react';

interface ErrorMessageProps {
  message: string;
  onClose?: () => void;
}

const ErrorMessage: React.FC<ErrorMessageProps> = ({ message, onClose }) => {
  return (
    <div className="error-message">
      <div className="error-content">
        <AlertCircle className="error-icon" size={20} />
        <span>{message}</span>
      </div>
      {onClose && (
        <button onClick={onClose} className="error-close">
          <X size={18} />
        </button>
      )}
    </div>
  );
};

export default ErrorMessage;
