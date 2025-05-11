import Register from "./bases/register"
import Login from "./bases/login"


export const Routes =[
    {
        path: "/",
        element:<Layout/>,
       children:[ {
            path: "/login",
            element: <Login/>,
        },
        {
            path: "/",
            element: <Login/>,
        },
        {
            path: "/register",
            element:<Register/>,
        },
        {
            path: "/events",
            element:<Events/>,
        },
        {
            path: "/completedTask",
            element:<Events/>,
        },
    
        {
            path: "/pendingTask",
            element:<Events/>,
        },
    
        {
            path: "/allTask",
            element:<Events/>,
        },
        {
            path: "/cancelledTask",
            element:<Events/>,
        },
        {
            path:"/createTask",
            element:<CreateTask/>,
        }
       ]
      
    },
    
]
