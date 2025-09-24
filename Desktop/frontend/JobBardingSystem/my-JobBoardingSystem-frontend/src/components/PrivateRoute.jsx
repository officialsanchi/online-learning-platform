"use client"
import { Navigate } from "react-router-dom"
import { useAuth } from "../context/AuthContext"

function PrivateRoute({ children }) {
  const { currentUser, loading } = useAuth()

  if (loading) {
    return <div className="flex justify-center items-center h-screen">Loading...</div>
  }

  if (!currentUser) {
    return <Navigate to="/login" />
  }

  return children
}

export default PrivateRoute
