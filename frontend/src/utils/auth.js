/**
 * Authentication Utilities
 * 
 * JWT token decoding and authentication helper functions.
 * Uses JWT token as the single source of truth for authentication.
 */
import { jwtDecode } from 'jwt-decode'

/**
 * Get the authentication token from localStorage
 * @returns {string|null} The JWT token or null if not found
 */
export function getAuthToken() {
  return localStorage.getItem('authToken')
}

/**
 * Decode JWT token and extract user information
 * @returns {Object|null} Decoded token data or null if invalid/missing
 */
export function decodeToken() {
  const token = getAuthToken()
  if (!token) return null

  try {
    const decoded = jwtDecode(token)
    
    // Check if token is expired
    const currentTime = Math.floor(Date.now() / 1000)
    if (decoded.exp && decoded.exp < currentTime) {
      console.warn('⚠️ Token expired')
      return null
    }

    return decoded
  } catch (error) {
    console.error('❌ Invalid token:', error)
    return null
  }
}

/**
 * Check if user is authenticated (has valid token)
 * @returns {boolean} True if user has valid token
 */
export function isAuthenticated() {
  return !!decodeToken()
}

/**
 * Get current user information from JWT token
 * @returns {Object|null} User object with role and userId, or null if not authenticated
 */
export function getCurrentUserFromToken() {
  const decoded = decodeToken()
  if (!decoded) return null

  return {
    userId: decoded.userId,
    role: decoded.role,
  }
}

/**
 * Check if current user has ADMIN role
 * @returns {boolean} True if user is admin
 */
export function isAdmin() {
  const user = getCurrentUserFromToken()
  return user?.role === 'ADMIN'
}

/**
 * Check if current user has USER role
 * @returns {boolean} True if user is regular user
 */
export function isUser() {
  const user = getCurrentUserFromToken()
  return user?.role === 'USER'
}

/**
 * Get user ID from token
 * @returns {number|null} User ID or null
 */
export function getUserId() {
  const user = getCurrentUserFromToken()
  return user?.userId || null
}

/**
 * Get user role from token
 * @returns {string|null} User role (ADMIN/USER) or null
 */
export function getUserRole() {
  const user = getCurrentUserFromToken()
  return user?.role || null
}

/**
 * Clear authentication token
 */
export function clearAuth() {
  localStorage.removeItem('authToken')
}

/**
 * Set authentication token
 * @param {string} token - JWT token to store
 */
export function setAuthToken(token) {
  localStorage.setItem('authToken', token)
}
