"use client"

import { useState } from "react"
import { useParams, Link, useNavigate } from "react-router-dom"
import { useAuth } from "../context/AuthContext"
import { allJobs } from "../data/jobs"

function JobDetailPage() {
  const { jobId } = useParams()
  const { currentUser } = useAuth()
  const navigate = useNavigate()
  const [isApplyModalOpen, setIsApplyModalOpen] = useState(false)

  // Find the job with the matching id
  const job = allJobs.find((j) => j.id === jobId)

  // If job not found
  if (!job) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 text-center">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">Job Not Found</h1>
        <p className="text-gray-600 mb-8">The job you're looking for doesn't exist or has been removed.</p>
        <Link
          to="/jobs"
          className="inline-block bg-teal-600 text-white px-6 py-3 rounded-md font-medium hover:bg-teal-700 transition-colors"
        >
          Browse Jobs
        </Link>
      </div>
    )
  }

  // Handle apply button click
  const handleApply = () => {
    if (!currentUser) {
      navigate("/login")
      return
    }

    setIsApplyModalOpen(true)
  }

  // Handle application submission
  const handleSubmitApplication = (e) => {
    e.preventDefault()
    // In a real app, this would make an API call to submit the application
    alert("Application submitted successfully!")
    setIsApplyModalOpen(false)
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-4">
        <Link to="/jobs" className="text-teal-600 hover:text-teal-800">
          ← Back to Jobs
        </Link>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2">
          {/* Job Header */}
          <div className="bg-white rounded-lg shadow-md p-6 mb-6">
            <div className="flex items-start">
              <img
                src={job.companyLogo || "/placeholder.svg?height=80&width=80"}
                alt={job.companyName}
                className="w-16 h-16 rounded-md mr-4 object-contain"
              />
              <div>
                <h1 className="text-2xl font-bold text-gray-900 mb-1">{job.title}</h1>
                <Link to={`/companies/${job.companyId}`} className="text-teal-600 hover:text-teal-800">
                  {job.companyName}
                </Link>
                <div className="mt-2 flex flex-wrap gap-2">
                  <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-teal-100 text-teal-800">
                    {job.jobType}
                  </span>
                  <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                    {job.locationType}
                  </span>
                  {job.isUrgent && (
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
                      Urgent
                    </span>
                  )}
                </div>
              </div>
            </div>
          </div>

          {/* Job Details */}
          <div className="bg-white rounded-lg shadow-md p-6 mb-6">
            <h2 className="text-xl font-semibold mb-4">Job Description</h2>
            <div className="prose max-w-none mb-6">
              <p>{job.description}</p>
            </div>

            <h2 className="text-xl font-semibold mb-4">Responsibilities</h2>
            <ul className="list-disc pl-5 mb-6">
              {job.responsibilities.map((item, index) => (
                <li key={index} className="mb-2">
                  {item}
                </li>
              ))}
            </ul>

            <h2 className="text-xl font-semibold mb-4">Requirements</h2>
            <ul className="list-disc pl-5 mb-6">
              {job.requirements.map((item, index) => (
                <li key={index} className="mb-2">
                  {item}
                </li>
              ))}
            </ul>

            <h2 className="text-xl font-semibold mb-4">Benefits</h2>
            <ul className="list-disc pl-5">
              {job.benefits.map((item, index) => (
                <li key={index} className="mb-2">
                  {item}
                </li>
              ))}
            </ul>
          </div>

          {/* Company Overview */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-xl font-semibold mb-4">About {job.companyName}</h2>
            <div className="prose max-w-none mb-6">
              <p>{job.companyDescription}</p>
            </div>

            <Link to={`/companies/${job.companyId}`} className="text-teal-600 hover:text-teal-800 font-medium">
              View Company Profile
            </Link>
          </div>
        </div>

        <div className="lg:col-span-1">
          {/* Job Summary */}
          <div className="bg-white rounded-lg shadow-md p-6 mb-6 sticky top-6">
            <h2 className="text-lg font-semibold mb-4">Job Summary</h2>

            <div className="space-y-4">
              <div>
                <div className="text-sm text-gray-500">Location</div>
                <div>{job.location}</div>
              </div>

              <div>
                <div className="text-sm text-gray-500">Job Type</div>
                <div>{job.jobType}</div>
              </div>

              <div>
                <div className="text-sm text-gray-500">Experience Level</div>
                <div>{job.experienceLevel}</div>
              </div>

              <div>
                <div className="text-sm text-gray-500">Salary</div>
                <div>
                  {job.salary
                    ? `$${job.salary.min.toLocaleString()} - $${job.salary.max.toLocaleString()} per year`
                    : "Competitive"}
                </div>
              </div>

              <div>
                <div className="text-sm text-gray-500">Posted On</div>
                <div>{job.postedDate}</div>
              </div>
            </div>

            <div className="mt-6">
              <button
                onClick={handleApply}
                className="w-full bg-teal-600 text-white py-3 rounded-md font-medium hover:bg-teal-700 transition-colors mb-4"
              >
                Apply Now
              </button>

              <button className="w-full bg-white border border-teal-600 text-teal-600 py-3 rounded-md font-medium hover:bg-teal-50 transition-colors">
                Save Job
              </button>
            </div>
          </div>

          {/* Similar Jobs */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-lg font-semibold mb-4">Similar Jobs</h2>

            <div className="space-y-4">
              {allJobs
                .filter((j) => j.category === job.category && j.id !== job.id)
                .slice(0, 3)
                .map((similarJob) => (
                  <div key={similarJob.id} className="border-b border-gray-200 pb-4 last:border-b-0 last:pb-0">
                    <Link to={`/jobs/${similarJob.id}`} className="block">
                      <h3 className="font-medium text-gray-900 hover:text-teal-600">{similarJob.title}</h3>
                    </Link>
                    <div className="text-sm text-gray-500">{similarJob.companyName}</div>
                    <div className="text-sm text-gray-500">{similarJob.location}</div>
                  </div>
                ))}
            </div>
          </div>
        </div>
      </div>

      {/* Application Modal */}
      {isApplyModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-semibold">Apply for {job.title}</h2>
              <button onClick={() => setIsApplyModalOpen(false)} className="text-gray-500 hover:text-gray-700">
                <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <form onSubmit={handleSubmitApplication}>
              <div className="mb-4">
                <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-1">
                  Full Name
                </label>
                <input
                  type="text"
                  id="name"
                  className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500"
                  defaultValue={currentUser?.name || ""}
                  required
                />
              </div>

              <div className="mb-4">
                <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
                  Email
                </label>
                <input
                  type="email"
                  id="email"
                  className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500"
                  defaultValue={currentUser?.email || ""}
                  required
                />
              </div>

              <div className="mb-4">
                <label htmlFor="phone" className="block text-sm font-medium text-gray-700 mb-1">
                  Phone Number
                </label>
                <input
                  type="tel"
                  id="phone"
                  className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500"
                  required
                />
              </div>

              <div className="mb-4">
                <label htmlFor="resume" className="block text-sm font-medium text-gray-700 mb-1">
                  Resume
                </label>
                <input
                  type="file"
                  id="resume"
                  className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500"
                  required
                />
                <p className="text-xs text-gray-500 mt-1">PDF, DOC, or DOCX (Max 5MB)</p>
              </div>

              <div className="mb-4">
                <label htmlFor="coverLetter" className="block text-sm font-medium text-gray-700 mb-1">
                  Cover Letter (Optional)
                </label>
                <textarea
                  id="coverLetter"
                  rows={4}
                  className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500"
                ></textarea>
              </div>

              <div className="flex justify-end gap-4">
                <button
                  type="button"
                  onClick={() => setIsApplyModalOpen(false)}
                  className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
                >
                  Cancel
                </button>
                <button type="submit" className="px-4 py-2 bg-teal-600 text-white rounded-md hover:bg-teal-700">
                  Submit Application
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

export default JobDetailPage
