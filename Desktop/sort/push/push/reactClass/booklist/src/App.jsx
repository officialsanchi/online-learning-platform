import { useState } from 'react'
import {createBrowserRouter, RouterProvider } from "react-router-dom";
import routes from './router/routes';

const route = createBrowserRouter([...routes]);

function App() {
  return (
    <>
    < RouterProvider router={route}/>
    </>
  )
}
export default App
