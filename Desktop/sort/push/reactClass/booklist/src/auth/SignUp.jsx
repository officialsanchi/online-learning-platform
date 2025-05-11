import React, { useState } from "react";
import style from "../style/SignUp.module.css";
import CustomButton from "../reuseables/customButton";
import { Link,useNavigate } from 'react-router-dom';

const SignUp = () => {
  const navigate = useNavigate();
    const userDetail = {
        username: "",
        email:  "",
        password: "",
    };

        const [data,setData] = useState( userDetail)

        function handleChange(event){
            const {name,value} = event.target;
            setData((prevData) => {
              return {... prevData,[name]: value}
            });
            
        };

        const handleSubmit = () => {
          console.log("submitted.....");
          // setTimeout(()=>{
            navigate('/login')

          // },2000)
}
        console.log(data)
  

  return (
    <div>
      <form  onsubmit={handleSubmit} action="">
        <div>
          <input
            type="text"
            name="username"
            placeholder="Enter user name"
            className={style.input}
            onChange={handleChange}
            required 
          />
        </div>

        <div>
          <input
            type="email"
            name="email"
            placeholder="Enter email "
            className={style.input}
            onChange={handleChange}
            required
            
          />
        </div>

        <div>
          <input
            type="password"
            name="password"
            placeholder="Enter password"
            className="input"
            onChange={handleChange}
            required
          />
        </div>
        <CustomButton style= {style.btn} type= "submit" textcontent="submit"/>
      </form>

      <div>
        <span>Already have an account?</span>
        <span><Link to={'/login'}>SignUp</Link></span>
      </div>
    </div>
  );
};


export default SignUp;
