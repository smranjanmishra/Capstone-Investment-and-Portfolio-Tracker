// Investment Products Pinia Store
// Centralized state management for investment products
// Uses direct axios calls to backend API

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import apiClient from '@/services/api'

export const useInvestmentStore = defineStore('investment', () => {

  // Array of all investment products from backend
  const investments = ref([])
  // Loading state for API calls
  const loading = ref(false)
  // Error message from failed API calls
  const error = ref(null)
  // Currently selected investment product
  const currentInvestment = ref(null)
  
  // Filter to return only active investment products (isActive = true)
  const activeInvestments = computed(() => {
    return investments.value.filter((inv) => inv.isActive === true)
  })

  // Group investments by type (STOCK, MUTUAL_FUND, BOND, ETF)
  const investmentsByType = computed(() => {
    const grouped = {}
    investments.value.forEach((inv) => {
      if (!grouped[inv.type]) {
        grouped[inv.type] = []
      }
      grouped[inv.type].push(inv)
    })
    return grouped
  })

  // Group investments by risk level (LOW, MEDIUM, HIGH)
  const investmentsByRisk = computed(() => {
    const grouped = {
      LOW: [],
      MEDIUM: [],
      HIGH: [],
    }
    investments.value.forEach((inv) => {
      if (grouped[inv.riskLevel]) {
        grouped[inv.riskLevel].push(inv)
      }
    })
    return grouped
  })

  // Fetch all investment products from backend API
  async function fetchInvestments() {
    loading.value = true
    error.value = null
    try {
      // GET request to /api/v1/investments
      const response = await apiClient.get('/investments')
      
      // Extract data array from backend response
      // Backend returns: { success: true, message: string, count: number, data: InvestmentProductResponseDTO[] }
      investments.value = response.data.data || []
      
      console.log(` Loaded ${investments.value.length} investments from backend`)
      return investments.value
    } catch (err) {
      error.value = 'Failed to fetch investments'
      console.error('Error fetching investments:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // Create a new investment product via backend API
  // @param {Object} investmentData - Investment product data
  async function createInvestment(investmentData) {
    loading.value = true
    error.value = null
    try {
      // POST request to /api/v1/admin/investments
      const response = await apiClient.post('/admin/investments', investmentData)
      
      // Extract created investment from response
      // Backend returns: { success: true, message: string, data: InvestmentProductResponseDTO }
      const newInvestment = response.data.data
      
      // Add new investment to local state
      investments.value.push(newInvestment)
      console.log(` Created investment: ${newInvestment.name}`)
      return newInvestment
    } catch (err) {
      error.value = 'Failed to create investment'
      console.error('Error creating investment:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // Update an existing investment product via backend API
  // @param {string|number} id - Investment product ID
  // @param {Object} investmentData - Updated investment data
  async function updateInvestment(id, investmentData) {
    loading.value = true
    error.value = null
    try {
      // PUT request to /api/v1/admin/investments/{id}
      const response = await apiClient.put(`/admin/investments/${id}`, investmentData)
      
      // Extract updated investment from response
      // Backend returns: { success: true, message: string, data: InvestmentProductResponseDTO }
      const updatedInvestment = response.data.data
      
      // Update local state with new data
      const index = investments.value.findIndex((inv) => inv.id === id)
      if (index !== -1) {
        investments.value[index] = updatedInvestment
      }
      console.log(` Updated investment: ${updatedInvestment.name}`)
      return updatedInvestment
    } catch (err) {
      error.value = 'Failed to update investment'
      console.error('Error updating investment:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // Toggle active status of an investment product via backend API
  // @param {string|number} id - Investment product ID
  // @param {boolean} isActive - New active status
  async function toggleActiveStatus(id, isActive) {
    loading.value = true
    error.value = null
    try {
      // Find current investment in local state
      const currentInvestment = investments.value.find(inv => inv.id === id)
      if (!currentInvestment) {
        throw new Error('Investment not found')
      }

      // Send PUT request with updated isActive status
      // Backend requires all fields, so we send complete object
      const response = await apiClient.put(`/admin/investments/${id}`, {
        name: currentInvestment.name,
        type: currentInvestment.type,
        riskLevel: currentInvestment.riskLevel,
        minInvestment: currentInvestment.minInvestment,
        expectedReturnRate: currentInvestment.expectedReturnRate,
        currentNAV: currentInvestment.currentNAV,
        description: currentInvestment.description,
        isActive: isActive
      })
      
      // Extract updated investment from response
      // Backend returns: { success: true, message: string, data: InvestmentProductResponseDTO }
      const updatedInvestment = response.data.data
      
      // Update local state with new data
      const index = investments.value.findIndex((inv) => inv.id === id)
      if (index !== -1) {
        investments.value[index] = updatedInvestment
        console.log(` ${isActive ? 'Activated' : 'Deactivated'} investment: ${investments.value[index].name}`)
        return investments.value[index]
      }
      throw new Error('Investment not found in local state')
    } catch (err) {
      error.value = 'Failed to update investment status'
      console.error('Error toggling investment status:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // Return public API of the store
  return {
    // State
    investments,
    loading,
    error,
    currentInvestment,
    // Getters
    activeInvestments,
    investmentsByType,
    investmentsByRisk,
    // Actions
    fetchInvestments,
    createInvestment,
    updateInvestment,
    toggleActiveStatus,
  }
})
