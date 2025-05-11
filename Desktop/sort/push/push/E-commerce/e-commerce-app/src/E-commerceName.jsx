import React from 'react';
import  Name from './name.css'; 

const Name = ({ title, content }) => {
    return (
        <div className="name">
            <h2>{title}</h2>
            <p>{content}</p>
        </div>
    );
};

export default Name;
