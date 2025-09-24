import { BrowserRouter as Router, Routes, Route } from "react-router-dom"
import Navbar from "./components/Navbar"
import Footer from "./components/Footer"
import HomePage from "./pages/HomePage"
import JobsPage from "./pages/JobsPage"
import JobDetailPage from "./pages/JobDetailPage"
import { AuthProvider } from "./context/AuthContext"
import PrivateRoute from "./components/PrivateRoute"


function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="flex flex-col min-h-screen">
          <Navbar />
          <main className="flex-grow">
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/jobs" element={<JobsPage />} />
              <Route path="/jobs/:jobId" element={<JobDetailPage />} />
              {/* <Route path="/companies/:companyId" element={<CompanyPage />} /> */}
              <Route
                path="/post-job"
                element={
                  <PrivateRoute>
                    {/* <PostJobPage /> */}
                  </PrivateRoute>
                }
              />
              <Route
                path="/dashboard"
                element={
                  <PrivateRoute>
                    {/* <DashboardPage /> */}
                  </PrivateRoute>
                }
              />
              {/* <Route path="/login" element={<LoginPage />} /> */}
              {/* <Route path="/signup" element={<SignupPage />} /> */}
              <Route
                path="/profile"
                element={
                  <PrivateRoute>
                    {/* <ProfilePage /> */}
                  </PrivateRoute>
                }
              />
            </Routes>
          </main>
          <Footer />
        </div>
      </Router>
    </AuthProvider>
  )
}

export default App
