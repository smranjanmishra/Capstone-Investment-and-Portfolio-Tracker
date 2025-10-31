import { defineStore } from 'pinia'
import { ref } from 'vue'
import apiClient from '@/services/api'

export const usePortfolioStore = defineStore('portfolio', () => {
  // --- STATE ---
  const portfolioItems = ref([])
  const loading = ref(false)
  const error = ref(null)
  
  async function fetchPortfolio() {
    
    if (portfolioItems.value.length > 0) {
      return portfolioItems.value
    }
    loading.value = true
    error.value = null

    try {
      // 3. Calls endpoint: GET /api/v1/portfolio
      const response = await apiClient.get('/portfolio')
    
      if (response.data && response.data.data) {
        
        portfolioItems.value = response.data.data || []
        console.log(`Loaded ${portfolioItems.value.length} portfolio items`)
        return portfolioItems.value
      
      } else {
        
        if (response.data && 
            response.data.message === 'Portfolio fetched successfully' && 
            response.data.data) {
          
          console.warn('API returned success:false but had success message and data. Treating as success.');
          portfolioItems.value = response.data.data || []
          console.log(`Loaded ${portfolioItems.value.length} portfolio items (recovered from API error)`)
          return portfolioItems.value
        }
      
        throw new Error(response.data.message || 'Failed to fetch portfolio data.')
      }
      
    } catch (err) {
     
      
      error.value = err.message || 'Failed to fetch portfolio'
      
      console.error('Error fetching portfolio:', err.message)
      portfolioItems.value = [] 

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