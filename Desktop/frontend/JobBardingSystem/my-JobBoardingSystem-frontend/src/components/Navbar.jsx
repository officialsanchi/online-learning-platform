"use client"

import { useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import { useAuth } from "../context/AuthContext"

function Navbar() {
  const { currentUser, logout } = useAuth()
  const navigate = useNavigate()
  const [isMenuOpen, setIsMenuOpen] = useState(false)

  const handleLogout = () => {
    logout()
    navigate("/")
  }

  return (
    <nav className="bg-teal-600 text-white shadow-md">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <Link to="/" className="flex-shrink-0 flex items-center">
              <span className="text-xl font-bold">JobConnect</span>
            </Link>
            <div className="hidden md:ml-6 md:flex md:space-x-4">
              <Link to="/" className="px-3 py-2 rounded-md hover:bg-teal-700">
                Home
              </Link>
              <Link to="/jobs" className="px-3 py-2 rounded-md hover:bg-teal-700">
                Jobs
              </Link>
              <Link to="/companies" className="px-3 py-2 rounded-md hover:bg-teal-700">
                Companies
              </Link>
            </div>
          </div>
          <div className="hidden md:flex items-center">
            {currentUser ? (
              <>
                {currentUser.role === "employer" && (
                  <Link to="/post-job" className="px-3 py-2 rounded-md hover:bg-teal-700">
                    Post a Job
                  </Link>
                )}
                <Link to="/dashboard" className="px-3 py-2 rounded-md hover:bg-teal-700">
                  Dashboard
                </Link>
                <div className="relative ml-3">
                  <div>
                    <button
                      onClick={() => setIsMenuOpen(!isMenuOpen)}
                      className="flex text-sm rounded-full focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-teal-600 focus:ring-white"
                    >
                      <span className="sr-only">Open user menu</span>
                      <div className="h-8 w-8 rounded-full bg-teal-800 flex items-center justify-center">
                        {currentUser.name.charAt(0)}
                      </div>
                    </button>
                  </div>
                  {isMenuOpen && (
                    <div className="origin-top-right absolute right-0 mt-2 w-48 rounded-md shadow-lg py-1 bg-white ring-1 ring-black ring-opacity-5 focus:outline-none z-10">
                      <Link to="/profile" className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                        Profile
                      </Link>
                      <button
                        onClick={handleLogout}
                        className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                      >
                        Sign out
                      </button>
                    </div>
                  )}
                </div>
              </>
            ) : (
              <>
                <Link to="/login" className="px-3 py-2 rounded-md hover:bg-teal-700">
                  Login
                </Link>
                <Link
                  to="/signup"
                  className="ml-3 px-3 py-2 border border-transparent rounded-md bg-white text-teal-600 hover:bg-gray-100"
                >
                  Sign Up
                </Link>
              </>
            )}
          </div>
          <div className="flex md:hidden items-center">
            <button
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              className="inline-flex items-center justify-center p-2 rounded-md text-white hover:bg-teal-700 focus:outline-none"
            >
              <svg
                className="h-6 w-6"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                {isMenuOpen ? (
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                ) : (
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                )}
              </svg>
            </button>
          </div>
        </div>
      </div>

      {/* Mobile menu */}
      {isMenuOpen && (
        <div className="md:hidden">
          <div className="px-2 pt-2 pb-3 space-y-1 sm:px-3">
            <Link to="/" className="block px-3 py-2 rounded-md hover:bg-teal-700">
              Home
            </Link>
            <Link to="/jobs" className="block px-3 py-2 rounded-md hover:bg-teal-700">
              Jobs
            </Link>
            <Link to="/companies" className="block px-3 py-2 rounded-md hover:bg-teal-700">
              Companies
            </Link>
            {currentUser && currentUser.role === "employer" && (
              <Link to="/post-job" className="block px-3 py-2 rounded-md hover:bg-teal-700">
                Post a Job
              </Link>
            )}
            {currentUser && (
              <Link to="/dashboard" className="block px-3 py-2 rounded-md hover:bg-teal-700">
                Dashboard
              </Link>
            )}
          </div>
          <div className="pt-4 pb-3 border-t border-teal-700">
            {currentUser ? (
              <div className="px-2 space-y-1">
                <div className="flex items-center px-3">
                  <div className="h-8 w-8 rounded-full bg-teal-800 flex items-center justify-center mr-3">
                    {currentUser.name.charAt(0)}
                  </div>
                  <div>
                    <div className="text-base font-medium">{currentUser.name}</div>
                    <div className="text-sm font-medium text-teal-300">{currentUser.email}</div>
                  </div>
                </div>
                <Link to="/profile" className="block px-3 py-2 rounded-md hover:bg-teal-700">
                  Profile
                </Link>
                <button
                  onClick={handleLogout}
                  className="block w-full text-left px-3 py-2 rounded-md hover:bg-teal-700"
                >
                  Sign out
                </button>
              </div>
            ) : (
              <div className="px-2 space-y-1">
                <Link to="/login" className="block px-3 py-2 rounded-md hover:bg-teal-700">
                  Login
                </Link>
                <Link to="/signup" className="block px-3 py-2 rounded-md hover:bg-teal-700">
                  Sign Up
                </Link>
              </div>
            )}
          </div>
        </div>
      )}
    </nav>
  )
}

export default Navbar
