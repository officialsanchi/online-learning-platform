import { Link } from "react-router-dom"

function JobCard({ job }) {
  return (
    <div className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
      <div className="flex items-start">
        <img
          src={job.companyLogo || "/placeholder.svg?height=60&width=60"}
          alt={job.companyName}
          className="w-12 h-12 rounded-md mr-4 object-contain"
        />
        <div className="flex-1">
          <Link to={`/jobs/${job.id}`} className="block">
            <h3 className="text-lg font-semibold text-gray-900 hover:text-teal-600">{job.title}</h3>
          </Link>
          <Link to={`/companies/${job.companyId}`} className="text-gray-600 hover:text-teal-600">
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
        <div className="text-right">
          <div className="text-gray-900 font-medium">
            {job.salary ? `$${job.salary.min.toLocaleString()} - $${job.salary.max.toLocaleString()}` : "Competitive"}
          </div>
          <div className="text-sm text-gray-500 mt-1">{job.location}</div>
          <div className="text-xs text-gray-400 mt-2">Posted {job.postedDate}</div>
        </div>
      </div>
      <div className="mt-4">
        <p className="text-gray-600 line-clamp-2">{job.description}</p>
      </div>
      <div className="mt-4 flex flex-wrap gap-2">
        {job.skills.slice(0, 4).map((skill, index) => (
          <span
            key={index}
            className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800"
          >
            {skill}
          </span>
        ))}
        {job.skills.length > 4 && (
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
            +{job.skills.length - 4} more
          </span>
        )}
      </div>
    </div>
  )
}

export default JobCard
