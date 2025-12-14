import React from 'react';
import { Loader } from 'lucide-react';

const Loading: React.FC = () => {
  return (
    <div className="loading-container">
      <Loader className="loading-spinner" size={48} />
      <p>Loading...</p>
    </div>
  );
};

export default Loading;
