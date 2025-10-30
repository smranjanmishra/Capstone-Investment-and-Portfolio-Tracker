/**
 * useAuth Composable
 * 
 * Provides reactive authentication state and user profile management.
 * Fetches user profile data from API and stores in memory (not localStorage).
 */
import { ref, computed } from 'vue'
import { isAuthenticated, isAdmin as checkIsAdmin, getUserRole, clearAuth } from '@/utils/auth'
import apiClient from '@/services/api'

// Global reactive state - shared across all components
const userProfile = ref(null)
const isLoadingProfile = ref(false)

export function useAuth() {
  // Computed properties based on token
  const authenticated = computed(() => isAuthenticated())
  const isAdmin = computed(() => checkIsAdmin())
  const role = computed(() => getUserRole())

  // Computed property for user info
  const currentUser = computed(() => userProfile.value)

  /**
   * Fetch user profile from API
   * Only fetches if not already loaded and user is authenticated
   */
  async function loadUserProfile() {
    if (!isAuthenticated()) {
      userProfile.value = null
      return
    }

    // Don't fetch if already loaded
    if (userProfile.value) {
      return
    }

    try {
      isLoadingProfile.value = true
      const response = await apiClient.get('/user/profile')
      
      userProfile.value = {
        ...response.data,
        fullName: response.data.name || response.data.fullName,
      }
      
      console.log('✅ User profile loaded from API')
    } catch (error) {
      console.error('❌ Failed to load user profile:', error)
      
      // If unauthorized, clear auth
      if (error.response?.status === 401) {
        logout()
      }
    } finally {
      isLoadingProfile.value = false
    }
  }

  /**
   * Logout user - clear token and profile
   */
  function logout() {
    clearAuth()
    userProfile.value = null
    console.log('🔓 User logged out')
  }

  /**
   * Clear profile cache (useful after login)
   */
  function clearProfileCache() {
    userProfile.value = null
  }

  return {
    // State
    currentUser,
    authenticated,
    isAdmin,
    role,
    isLoadingProfile,
    
    // Methods
    loadUserProfile,
    logout,
    clearProfileCache,
  }
}
