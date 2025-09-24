import { Link } from "react-router-dom"
import JobCard from "../components/JobCard"
import { featuredJobs, topCompanies } from "../data/jobs"

function HomePage() {
  return (
    <div>
      {/* Hero Section */}
      <section className="bg-gradient-to-r from-teal-600 to-teal-700 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 md:py-24">
          <div className="text-center">
            <h1 className="text-4xl md:text-5xl font-extrabold mb-6">Find Your Dream Job Today</h1>
            <p className="text-lg md:text-xl mb-8 max-w-3xl mx-auto">
              Connect with top employers and discover opportunities that match your skills and career goals.
            </p>
            <div className="max-w-3xl mx-auto">
              <div className="flex flex-col md:flex-row bg-white rounded-lg shadow-lg overflow-hidden">
                <input
                  type="text"
                  placeholder="Job title, keywords, or company"
                  className="flex-1 px-4 py-3 text-gray-900 focus:outline-none"
                />
                <input
                  type="text"
                  placeholder="Location"
                  className="flex-1 px-4 py-3 text-gray-900 focus:outline-none border-t md:border-t-0 md:border-l border-gray-200"
                />
                <button className="bg-teal-600 hover:bg-teal-700 text-white px-6 py-3 transition-colors">
                  Search Jobs
                </button>
              </div>
              <div className="mt-4 text-sm">
                Popular:
                <Link to="/jobs?q=developer" className="ml-2 hover:underline">
                  Developer
                </Link>
                ,
                <Link to="/jobs?q=marketing" className="ml-2 hover:underline">
                  Marketing
                </Link>
                ,
                <Link to="/jobs?q=design" className="ml-2 hover:underline">
                  Design
                </Link>
                ,
                <Link to="/jobs?q=remote" className="ml-2 hover:underline">
                  Remote
                </Link>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Featured Jobs */}
      <section className="py-12 md:py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center mb-8">
            <h2 className="text-2xl md:text-3xl font-bold text-gray-900">Featured Jobs</h2>
            <Link to="/jobs" className="text-teal-600 hover:text-teal-800 font-medium">
              View All Jobs
            </Link>
          </div>

          <div className="space-y-6">
            {featuredJobs.map((job) => (
              <JobCard key={job.id} job={job} />
            ))}
          </div>
        </div>
      </section>

      {/* Job Categories */}
      <section className="py-12 md:py-16 bg-gray-100">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h2 className="text-2xl md:text-3xl font-bold text-gray-900 mb-8 text-center">Browse Jobs by Category</h2>

          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {jobCategories.map((category) => (
              <Link
                key={category.name}
                to={`/jobs?category=${category.slug}`}
                className="bg-white rounded-lg shadow-md p-6 text-center hover:shadow-lg transition-shadow"
              >
                <div className="text-teal-600 text-3xl mb-3">{category.icon}</div>
                <h3 className="font-medium text-gray-900">{category.name}</h3>
                <p className="text-sm text-gray-500 mt-1">{category.count} jobs</p>
              </Link>
            ))}
          </div>
        </div>
      </section>

      {/* Top Companies */}
      <section className="py-12 md:py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center mb-8">
            <h2 className="text-2xl md:text-3xl font-bold text-gray-900">Top Companies Hiring</h2>
            <Link to="/companies" className="text-teal-600 hover:text-teal-800 font-medium">
              View All Companies
            </Link>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {topCompanies.map((company) => (
              <Link
                key={company.id}
                to={`/companies/${company.id}`}
                className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow"
              >
                <div className="flex items-center justify-center mb-4">
                  <img
                    src={company.logo || "/placeholder.svg?height=80&width=80"}
                    alt={company.name}
                    className="h-16 w-16 object-contain"
                  />
                </div>
                <h3 className="font-medium text-gray-900 text-center">{company.name}</h3>
                <p className="text-sm text-gray-500 text-center mt-1">{company.openJobs} open positions</p>
              </Link>
            ))}
          </div>
        </div>
      </section>

      {/* How It Works */}
      <section className="py-12 md:py-16 bg-gray-100">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h2 className="text-2xl md:text-3xl font-bold text-gray-900 mb-8 text-center">How JobConnect Works</h2>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center">
              <div className="bg-teal-100 text-teal-600 w-16 h-16 rounded-full flex items-center justify-center text-2xl mx-auto mb-4">
                1
              </div>
              <h3 className="text-xl font-semibold mb-2">Create Your Profile</h3>
              <p className="text-gray-600">
                Sign up and create your professional profile to showcase your skills and experience.
              </p>
            </div>

            <div className="text-center">
              <div className="bg-teal-100 text-teal-600 w-16 h-16 rounded-full flex items-center justify-center text-2xl mx-auto mb-4">
                2
              </div>
              <h3 className="text-xl font-semibold mb-2">Discover Opportunities</h3>
              <p className="text-gray-600">
                Search and apply for jobs that match your skills, experience, and career goals.
              </p>
            </div>

            <div className="text-center">
              <div className="bg-teal-100 text-teal-600 w-16 h-16 rounded-full flex items-center justify-center text-2xl mx-auto mb-4">
                3
              </div>
              <h3 className="text-xl font-semibold mb-2">Land Your Dream Job</h3>
              <p className="text-gray-600">
                Connect with employers, ace your interviews, and start your new career journey.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Testimonials */}
      <section className="py-12 md:py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h2 className="text-2xl md:text-3xl font-bold text-gray-900 mb-8 text-center">Success Stories</h2>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {testimonials.map((testimonial, index) => (
              <div key={index} className="bg-white p-6 rounded-lg shadow-md">
                <div className="flex items-center mb-4">
                  <img
                    src={testimonial.avatar || "/placeholder.svg?height=50&width=50"}
                    alt={testimonial.name}
                    className="h-12 w-12 rounded-full mr-4"
                  />
                  <div>
                    <h3 className="font-semibold text-gray-900">{testimonial.name}</h3>
                    <p className="text-sm text-gray-500">
                      {testimonial.role} at {testimonial.company}
                    </p>
                  </div>
                </div>
                <p className="text-gray-600 italic">"{testimonial.text}"</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-12 md:py-16 bg-teal-600 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-2xl md:text-3xl font-bold mb-4">Ready to Find Your Next Opportunity?</h2>
          <p className="text-lg mb-8 max-w-3xl mx-auto">
            Join thousands of professionals who have found their dream jobs through JobConnect.
          </p>
          <div className="flex flex-col sm:flex-row justify-center gap-4">
            <Link
              to="/signup"
              className="px-6 py-3 rounded-md bg-white text-teal-600 font-medium hover:bg-gray-100 transition-colors"
            >
              Sign Up as Job Seeker
            </Link>
            <Link
              to="/signup?role=employer"
              className="px-6 py-3 rounded-md bg-transparent border border-white text-white font-medium hover:bg-white/10 transition-colors"
            >
              Sign Up as Employer
            </Link>
          </div>
        </div>
      </section>
    </div>
  )
}

// Sample data for the homepage
const jobCategories = [
  { name: "Technology", slug: "technology", icon: "💻", count: 1243 },
  { name: "Marketing", slug: "marketing", icon: "📱", count: 873 },
  { name: "Design", slug: "design", icon: "🎨", count: 654 },
  { name: "Finance", slug: "finance", icon: "💰", count: 512 },
  { name: "Healthcare", slug: "healthcare", icon: "🏥", count: 435 },
  { name: "Education", slug: "education", icon: "🎓", count: 327 },
  { name: "Sales", slug: "sales", icon: "📊", count: 289 },
  { name: "Customer Service", slug: "customer-service", icon: "🤝", count: 267 },
]

const testimonials = [
  {
    name: "David Chen",
    role: "Software Engineer",
    company: "TechCorp",
    avatar: "/placeholder.svg?height=50&width=50",
    text: "I found my dream job at a tech startup through JobConnect. The platform made it easy to search for positions that matched my skills and experience.",
  },
  {
    name: "Sarah Johnson",
    role: "Marketing Manager",
    company: "BrandWorks",
    avatar: "/placeholder.svg?height=50&width=50",
    text: "After being laid off, I was worried about finding a new position. JobConnect connected me with multiple opportunities, and I landed a great job within weeks.",
  },
  {
    name: "Michael Rodriguez",
    role: "HR Director",
    company: "GlobalCorp",
    avatar: "/placeholder.svg?height=50&width=50",
    text: "As an employer, JobConnect has helped us find qualified candidates quickly. The platform's filtering tools make it easy to identify the right talent for our open positions.",
  },
]

export default HomePage
