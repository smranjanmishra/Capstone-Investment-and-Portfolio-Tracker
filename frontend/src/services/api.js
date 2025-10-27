/**
 * API Configuration and Axios Instance
 *
 * Centralized HTTP client configuration for all API calls.
 * Uses environment variables for base URL configuration.
 */

import axios from 'axios'

// Create axios instance with base configuration
// Base URL points to Spring Boot backend @RequestMapping("/api/v1")
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 seconds timeout
  withCredentials: false, // Set to true if using cookies for authentication
})

// Request interceptor for adding authentication token if needed
apiClient.interceptors.request.use(
  (config) => {
    // Add auth token if available in localStorage
    const token = localStorage.getItem('authToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => {
    // Successfully received response
    return response
  },
  (error) => {
    // Handle common errors
    if (error.response) {
      // Server responded with error status
      const status = error.response.status
      const errorData = error.response.data

      console.error(`API Error [${status}]:`, errorData)

      // Handle 401 Unauthorized - Session expired or invalid token
      if (status === 401) {
        console.warn('⚠️ Unauthorized access - clearing auth token')
        localStorage.removeItem('authToken')
        localStorage.removeItem('currentUser')

        // Dispatch event for components to react
        window.dispatchEvent(new CustomEvent('auth-token-expired'))

        // Only redirect if not already on login/register page
        const currentPath = window.location.pathname
        if (currentPath !== '/login' && currentPath !== '/register') {
          console.log('🔄 Redirecting to login...')
          window.location.href = '/login?session=expired'
        }
      }

      // Handle 403 Forbidden - Insufficient privileges
      if (status === 403) {
        console.warn('⚠️ Access forbidden - insufficient privileges')
      }

      // Handle 404 Not Found
      if (status === 404) {
        console.warn('⚠️ Resource not found')
      }

      // Handle 500 Internal Server Error
      if (status === 500) {
        console.error('Server error - please try again later')
      }
    } else if (error.request) {
      // Request was made but no response received
      console.error('Network Error: No response from server')
      console.error('Please check:')
      console.error('1. Backend server is running on http://localhost:8080')
      console.error('2. CORS is properly configured')
      console.error('3. Network connection is stable')
    } else {
      // Something else happened
      console.error('Error:', error.message)
    }

    return Promise.reject(error)
  }
)

export default apiClient
export const getPortfolio = () => apiClient.get("/portfolio");
export const buyInvestment = (payload) => apiClient.post("/portfolio/buy", payload);
export const sellInvestment = (payload) => apiClient.post("/portfolio/sell", payload);
export const getTransactions = () => apiClient.get("/portfolio/transactions");
export const getInvestments = () => apiClient.get("/Investments");


export const getPortfolioSummary = () => apiClient.get('/portfolio/summary');
export const getPortfolioAllocation = () => apiClient.get('/portfolio/allocation');
export const getPortfolioGains = () => apiClient.get('/portfolio/gains');
