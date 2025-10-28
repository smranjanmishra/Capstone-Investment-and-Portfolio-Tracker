// Portfolio Analytics Store
// Manages portfolio performance, asset allocation, and ROI metrics

import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getPortfolioSummary,
  getPortfolioAllocation,
  getPortfolioGains,
} from '@/services/api'

export const usePortfolioAnalyticsStore = defineStore('portfolioAnalytics', () => {
  // --- STATE ---
  const summary = ref(null)
  const allocation = ref([])
  const gains = ref([])
  const loading = ref(false)
  const error = ref(null)

  // --- ACTIONS ---
  async function fetchPortfolioSummary() {
    try {
      loading.value = true
      const res = await getPortfolioSummary()
      summary.value = res.data.data || res.data
      console.log('✅ Portfolio summary loaded:', summary.value)
    } catch (err) {
      error.value = 'Failed to load portfolio summary'
      console.error(err)
    } finally {
      loading.value = false
    }
  }

  async function fetchPortfolioAllocation() {
    try {
      loading.value = true
      const res = await getPortfolioAllocation()
      allocation.value = res.data.data || res.data
      console.log('✅ Asset allocation loaded:', allocation.value)
    } catch (err) {
      error.value = 'Failed to load allocation data'
      console.error(err)
    } finally {
      loading.value = false
    }
  }

  async function fetchPortfolioGains() {
    try {
      loading.value = true
      const res = await getPortfolioGains()
      gains.value = res.data.data || res.data
      console.log('✅ Gains data loaded:', gains.value)
    } catch (err) {
      error.value = 'Failed to load gains data'
      console.error(err)
    } finally {
      loading.value = false
    }
  }

  // Fetch all analytics in one go
  async function fetchAllAnalytics() {
    error.value = null
    loading.value = true
    try {
      await Promise.all([
        fetchPortfolioSummary(),
        fetchPortfolioAllocation(),
        fetchPortfolioGains()
      ])
    } catch (err) {
      error.value = 'Failed to load portfolio analytics'
    } finally {
      loading.value = false
    }
  }

  // --- EXPORT ---
  return {
    summary,
    allocation,
    gains,
    loading,
    error,
    fetchPortfolioSummary,
    fetchPortfolioAllocation,
    fetchPortfolioGains,
    fetchAllAnalytics
  }
})
