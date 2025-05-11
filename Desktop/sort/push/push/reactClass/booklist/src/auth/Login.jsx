import  React, {useState} from "react";
import style from "../style/Login.module.css";
import CustomButton from "../reuseables/customButton";
import { Link } from 'react-router-dom';

const Login = ()=>{

    const userLoginDetails = {
        email: "",
        password: "",
    };

    const [loginData,setLoginData] = useState( userLoginDetails)
        function handleChange(event){
            const {name,value} = event.target;
            setLoginData((prevData) => {
              return {... prevData,[name]: value}
            });
            

        }
    

        console.log(loginData);      

    return (
     
         <div>
            <form  action="">
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
        <CustomButton style= {style.btn} type= "submit" textcontent="SignUp"/>
       </form>


       <div>
        <span>I don't have an account?</span>
        <span><Link to={'/Login'}>Login</Link></span>
       </div>
         </div>

         
    )
}
export default Login;