// Authentication Utilities
// JWT token decoding and authentication helper functions.
// Uses JWT token as the single source of truth for authentication.
import { jwtDecode } from 'jwt-decode'

// Get the authentication token from sessionStorage
// Returns: The JWT token or null if not found
export function getAuthToken() {
  return sessionStorage.getItem('authToken')
}

// Decode JWT token and extract user information
// Returns: Decoded token data or null if invalid/missing
export function decodeToken() {
  const token = getAuthToken()
  if (!token) return null

  try {
    const decoded = jwtDecode(token)
    
    // Check if token is expired
    const currentTime = Math.floor(Date.now() / 1000)
    if (decoded.exp && decoded.exp < currentTime) {
      console.warn('Token expired')
      return null
    }

    return decoded
  } catch (error) {
    console.error('Invalid token:', error)
    return null
  }
}

// Check if user is authenticated (has valid token)
// Returns: True if user has valid token
export function isAuthenticated() {
  return !!decodeToken()
}

// Get current user information from JWT token
// Returns: User object with role and userId, or null if not authenticated
export function getCurrentUserFromToken() {
  const decoded = decodeToken()
  if (!decoded) return null

  return {
    userId: decoded.userId,
    role: decoded.role,
  }
}

// Check if current user has ADMIN role
// Returns: True if user is admin
export function isAdmin() {
  const user = getCurrentUserFromToken()
  return user?.role === 'ADMIN'
}

// Get user role from token
// Returns: User role (ADMIN/USER) or null
export function getUserRole() {
  const user = getCurrentUserFromToken()
  return user?.role || null
}

// Clear authentication token
export function clearAuth() {
  sessionStorage.removeItem('authToken')
}
