import { defineStore } from 'pinia'
import { ref } from 'vue'
import apiClient from '@/services/api'

export const usePortfolioStore = defineStore('portfolio', () => {
  // --- STATE ---
  const portfolioItems = ref([])
  const loading = ref(false)
  const error = ref(null)
  // --- ACTIONS ---
//  Fetches the logged-in user's portfolio items.
  async function fetchPortfolio() {
    if (portfolioItems.value.length > 0) {
      return portfolioItems.value
    }
    loading.value = true
    error.value = null
    try {
      // 1. Calls  endpoint: GET /api/v1/portfolio
      const response = await apiClient.get('/portfolio')
    
      if (response.data && response.data.success) {
        portfolioItems.value = response.data.data || []
      } else {
        // Handle cases where { "success": false } is returned
        throw new Error(response.data.message || 'Failed to fetch portfolio data.')
      }
      
      console.log(`Loaded ${portfolioItems.value.length} portfolio items`)
      return portfolioItems.value
    } catch (err) {
      error.value = err.response?.data?.message || err.message || 'Failed to fetch portfolio'
      console.error('Error fetching portfolio:', err)
      portfolioItems.value = [] // Ensure we have an empty array on error
      throw err
    } finally {
      loading.value = false
    }
  }
  return {
    // State
    portfolioItems,
    loading,
    error,
    // Actions
    fetchPortfolio,
  }
})