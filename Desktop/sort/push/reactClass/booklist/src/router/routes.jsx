import Login from "../auth/login";
import SignUp from "../auth/SignUp";
import BookListPage from "../components/BookListPage" ;

const routes = [
    {
        path:"/login",
        element:<Login/>
    },
    {
        path:"/signUp",
        element:<SignUp/>
    },
    {
        path:"/",
        element:<SignUp/>
    },
    {
        path:"/bookList",
        element: <BookListPage/>
    
    }

]

export default routes;