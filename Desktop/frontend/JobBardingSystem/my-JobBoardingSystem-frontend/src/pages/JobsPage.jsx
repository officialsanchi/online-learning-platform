"use client"

import { useState, useEffect } from "react"
import { useSearchParams } from "react-router-dom"
import JobCard from "../components/JobCard"
import { allJobs } from "../data/jobs"

function JobsPage() {
  const [searchParams] = useSearchParams()
  const queryParam = searchParams.get("q")
  const categoryParam = searchParams.get("category")

  const [searchTerm, setSearchTerm] = useState(queryParam || "")
  const [selectedCategory, setSelectedCategory] = useState(categoryParam || "")
  const [selectedJobType, setSelectedJobType] = useState("")
  const [selectedLocation, setSelectedLocation] = useState("")
  const [salaryRange, setSalaryRange] = useState([0, 200000])
  const [filteredJobs, setFilteredJobs] = useState(allJobs)

  // Extract unique categories and job types
  const categories = ["All", ...new Set(allJobs.map((job) => job.category))]
  const jobTypes = ["All", ...new Set(allJobs.map((job) => job.jobType))]
  const locations = ["All", ...new Set(allJobs.map((job) => job.location))]

  // Filter jobs based on search, category, job type, location, and salary
  useEffect(() => {
    const filtered = allJobs.filter((job) => {
      const matchesSearch =
        job.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        job.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
        job.companyName.toLowerCase().includes(searchTerm.toLowerCase())

      const matchesCategory = selectedCategory === "" || selectedCategory === "All" || job.category === selectedCategory

      const matchesJobType = selectedJobType === "" || selectedJobType === "All" || job.jobType === selectedJobType

      const matchesLocation = selectedLocation === "" || selectedLocation === "All" || job.location === selectedLocation

      const matchesSalary = !job.salary || (job.salary.min >= salaryRange[0] && job.salary.max <= salaryRange[1])

      return matchesSearch && matchesCategory && matchesJobType && matchesLocation && matchesSalary
    })

    setFilteredJobs(filtered)
  }, [searchTerm, selectedCategory, selectedJobType, selectedLocation, salaryRange])

  // Set search term and category from URL parameters on initial load
  useEffect(() => {
    if (queryParam) {
      setSearchTerm(queryParam)
    }
    if (categoryParam) {
      setSelectedCategory(categoryParam)
    }
  }, [queryParam, categoryParam])

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Find Jobs</h1>

      <div className="mb-8">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="flex-1">
            <input
              type="text"
              placeholder="Job title, keywords, or company"
              className="w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          <div className="flex-1">
            <select
              className="w-full px-4 py-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500"
              value={selectedLocation}
              onChange={(e) => setSelectedLocation(e.target.value)}
            >
              <option value="">All Locations</option>
              {locations.map(
                (location, index) =>
                  location !== "All" && (
                    <option key={index} value={location}>
                      {location}
                    </option>
                  ),
              )}
            </select>
          </div>
          <button className="bg-teal-600 hover:bg-teal-700 text-white px-6 py-3 rounded-md transition-colors">
            Search Jobs
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
        {/* Filters */}
        <div className="lg:col-span-1">
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-lg font-semibold mb-4">Filters</h2>

            <div className="mb-6">
              <label htmlFor="category" className="block text-sm font-medium text-gray-700 mb-1">
                Category
              </label>
              <select
                id="category"
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500"
                value={selectedCategory}
                onChange={(e) => setSelectedCategory(e.target.value)}
              >
                <option value="">All Categories</option>
                {categories.map(
                  (category, index) =>
                    category !== "All" && (
                      <option key={index} value={category}>
                        {category}
                      </option>
                    ),
                )}
              </select>
            </div>

            <div className="mb-6">
              <label htmlFor="jobType" className="block text-sm font-medium text-gray-700 mb-1">
                Job Type
              </label>
              <select
                id="jobType"
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500"
                value={selectedJobType}
                onChange={(e) => setSelectedJobType(e.target.value)}
              >
                <option value="">All Job Types</option>
                {jobTypes.map(
                  (type, index) =>
                    type !== "All" && (
                      <option key={index} value={type}>
                        {type}
                      </option>
                    ),
                )}
              </select>
            </div>

            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Salary Range: ${salaryRange[0].toLocaleString()} - ${salaryRange[1].toLocaleString()}
              </label>
              <div className="flex items-center space-x-4">
                <input
                  type="range"
                  min="0"
                  max="200000"
                  step="10000"
                  value={salaryRange[0]}
                  onChange={(e) => setSalaryRange([Number.parseInt(e.target.value), salaryRange[1]])}
                  className="w-full"
                />
                <input
                  type="range"
                  min="0"
                  max="200000"
                  step="10000"
                  value={salaryRange[1]}
                  onChange={(e) => setSalaryRange([salaryRange[0], Number.parseInt(e.target.value)])}
                  className="w-full"
                />
              </div>
            </div>

            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-1">Experience Level</label>
              <div className="space-y-2">
                <div className="flex items-center">
                  <input type="checkbox" id="entry" className="mr-2" />
                  <label htmlFor="entry">Entry Level</label>
                </div>
                <div className="flex items-center">
                  <input type="checkbox" id="mid" className="mr-2" />
                  <label htmlFor="mid">Mid Level</label>
                </div>
                <div className="flex items-center">
                  <input type="checkbox" id="senior" className="mr-2" />
                  <label htmlFor="senior">Senior Level</label>
                </div>
                <div className="flex items-center">
                  <input type="checkbox" id="executive" className="mr-2" />
                  <label htmlFor="executive">Executive</label>
                </div>
              </div>
            </div>

            <button
              onClick={() => {
                setSearchTerm("")
                setSelectedCategory("")
                setSelectedJobType("")
                setSelectedLocation("")
                setSalaryRange([0, 200000])
              }}
              className="w-full bg-gray-200 hover:bg-gray-300 text-gray-800 py-2 rounded-md transition-colors"
            >
              Reset Filters
            </button>
          </div>
        </div>

        {/* Job List */}
        <div className="lg:col-span-3">
          <div className="mb-4 flex justify-between items-center">
            <div className="text-gray-600">{filteredJobs.length} jobs found</div>
            <div>
              <select className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500">
                <option>Most Relevant</option>
                <option>Newest</option>
                <option>Highest Salary</option>
              </select>
            </div>
          </div>

          {filteredJobs.length === 0 ? (
            <div className="text-center py-12 bg-white rounded-lg shadow-md">
              <h3 className="text-xl font-semibold mb-2">No jobs found</h3>
              <p className="text-gray-600">Try adjusting your filters or search term.</p>
            </div>
          ) : (
            <div className="space-y-6">
              {filteredJobs.map((job) => (
                <JobCard key={job.id} job={job} />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default JobsPage
