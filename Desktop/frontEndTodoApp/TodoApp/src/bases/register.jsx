import React, { useState } from 'react'
import style from "../style/register.module.css"
import {Link} from "react-router-dom"
import { useAddUserMutation } from '../service/AuthApi'


const Register = () => {
    const [name, setName] = useState('');
    const [userName, setUserName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [addUser, {isLoading}] = useAddUserMutation();

    
  
    
    const handleSubmit = async (event) => {
      event.preventDefault(); 

      const userData = {
        userName,
        email,
        password,
        confirmPassword 
      }
  
      try{
        await addUser(userData);


      }catch(error){
        alert(error)

      };

      
      // if (!name || !userName || !email || !password || !confirmPassword) {
      //   setError('All fields are required!');
      //   setSuccess('');
      //   return;
      // }
  
      // if (password !== confirmPassword) {
      //   setError('Passwords do not match!');
      //   setSuccess('');
      //   return;
      // }
  
      
      // setError('');
      // setSuccess('Registration successful!');
  
      
      // console.log('User Registered:', { name, userName, email, password });
    };
  
    return (
      <div className={style.motherDiv}>
          <div className={style.register}>
        <h2>Register</h2>
        <form onSubmit={handleSubmit} className={style.form}>
          <div className={style.inputStyle}>
            <label className={style.label}>Name:</label>
            <input
            placeholder='Enter your name'
              type="text"
              value={name}
              className={style.input}
              onChange={(event) => setName(event.target.value)}
              required
            />
          </div>
          <div className={style.inputStyle}>
            <label className={style.label}>UserName:</label>
            <input type="text"
             placeholder='Enter your userName'
            className={style.input}
            value={userName} 
            onChange={(event) => setUserName (event.target.value)}
            />

          </div>
          <div className={style.inputStyle}>
          <label className={style.label}>Email:</label>
            <input
              type="email"
               placeholder='Enter your email'
              className={style.input}
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
            />
          </div>
          <div className={style.inputStyle}>
            <label className={style.label}>Password:</label>
            <input
              type="password"
               placeholder='Enter your password'
              className={style.input}
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
            />
          </div>
          <div className={style.inputStyle}>
            <label className={style.label}>Confirm Password:</label>
            <input
              type="password"
               placeholder='Enter your password'
              className={style.input}
              value={confirmPassword}
              onChange={(event) => setConfirmPassword(event.target.value)}
              required
            />
          </div>
          {error && <p className="error">{error}</p>}
          {success && <p className="success">{success}</p>}
          <Link to='/register'><button className={style.button} type="register">Register</button></Link>
          
        </form>
      </div>
      </div>
      
    );
  
}

export default Register