"use client"

import { createContext, useState, useContext, useEffect } from "react"

const AuthContext = createContext()

export function useAuth() {
  return useContext(AuthContext)
}

export function AuthProvider({ children }) {
  const [currentUser, setCurrentUser] = useState(null)
  const [loading, setLoading] = useState(true)

  // Check if user is logged in from localStorage on initial load
  useEffect(() => {
    const user = localStorage.getItem("user")
    if (user) {
      setCurrentUser(JSON.parse(user))
    }
    setLoading(false)
  }, [])

  // Login function
  const login = (email, password) => {
    // In a real app, this would make an API call
    // For demo purposes, we'll simulate a successful login
    const user = {
      id: "user123",
      name: "John Doe",
      email: email,
      role: "jobseeker", // or "employer"
      applications: [],
      savedJobs: [],
    }

    localStorage.setItem("user", JSON.stringify(user))
    setCurrentUser(user)
    return Promise.resolve(user)
  }

  // Signup function
  const signup = (name, email, password, role) => {
    // In a real app, this would make an API call
    const user = {
      id: "user123",
      name: name,
      email: email,
      role: role,
      applications: [],
      savedJobs: [],
    }

    localStorage.setItem("user", JSON.stringify(user))
    setCurrentUser(user)
    return Promise.resolve(user)
  }

  // Logout function
  const logout = () => {
    localStorage.removeItem("user")
    setCurrentUser(null)
  }

  const value = {
    currentUser,
    login,
    signup,
    logout,
    loading,
  }

  return <AuthContext.Provider value={value}>{!loading && children}</AuthContext.Provider>
}
