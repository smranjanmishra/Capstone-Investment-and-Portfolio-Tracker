<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-11 col-lg-10">
        
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h1 class="h2 fw-bold mb-0">Manage All Tickets</h1>
          <div class="d-flex gap-2">
            <button class="btn btn-outline-secondary" @click="toggleFilter">
              <i class="bi" :class="showOnlyOpen ? 'bi-funnel-fill' : 'bi-funnel'"></i>
              {{ showOnlyOpen ? 'Showing Open' : 'Show All' }}
            </button>
            <button class="btn btn-primary" @click="refreshTickets">
              <i class="bi bi-arrow-clockwise me-1"></i>
              Refresh
            </button>
          </div>
        </div>

        <div v-if="store.loading && !store.allTickets.length" class="text-center my-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading tickets...</span>
          </div>
          <p class="mt-2 text-muted">Fetching all user tickets...</p>
        </div>

        <div v-else-if="store.error" class="alert alert-danger">
          <strong>Error:</strong> {{ store.error.message || store.error }}
        </div>

        <div v-else-if="!store.allTickets.length" class="card shadow-sm border-0 text-center p-5">
          <i class="bi bi-inbox-fill fs-1 text-muted"></i>
          <h3 class="mt-3">Ticket Inbox is Empty</h3>
          <p class="text-muted">
            There are currently no support tickets in the system.
          </p>
        </div>

        <div v-else class="list-group shadow-sm">
          <TicketListItem
            v-for="ticket in filteredTickets"
            :key="ticket.id"
            :ticket="ticket"
            :showUser="true" 
          />
          <div v-if="!filteredTickets.length" class="list-group-item text-center p-4 text-muted">
            No tickets match the current filter.
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, computed, ref } from 'vue'
import { useTicketStore } from '@/stores/ticketStore'
import TicketListItem from '@/components/TicketListItem.vue'

const store = useTicketStore()
const showOnlyOpen = ref(true) // Admin default: show only OPEN tickets

// Fetch tickets when the component loads
onMounted(() => {
  fetchTickets()
})

function fetchTickets() {
  store.fetchAllTickets().catch(err => {
    console.error("Failed to load all tickets:", err)
  })
}

function refreshTickets() {
  // Force a refresh from the server
  store.fetchAllTickets()
}

function toggleFilter() {
  showOnlyOpen.value = !showOnlyOpen.value
}

// Filter and Sort tickets
const filteredTickets = computed(() => {
  let tickets = [...store.allTickets]

  // Filter
  if (showOnlyOpen.value) {
    tickets = tickets.filter(t => t.ticketStatus === 'OPEN')
  }

  // Sort: Show OPEN tickets first, then by priority (HIGH > MEDIUM > LOW)
  return tickets.sort((a, b) => {
    // 1. Status sort (OPEN comes before RESPONDED)
    if (a.ticketStatus === 'OPEN' && b.ticketStatus !== 'OPEN') return -1
    if (a.ticketStatus !== 'OPEN' && b.ticketStatus === 'OPEN') return 1

    // 2. Priority sort (HIGH comes before MEDIUM/LOW)
    const priorityOrder = { 'HIGH': 1, 'MEDIUM': 2, 'LOW': 3 }
    const priorityA = priorityOrder[a.ticketPriority] || 4
    const priorityB = priorityOrder[b.ticketPriority] || 4
    if (priorityA !== priorityB) {
      return priorityA - priorityB
    }

    // 3. Date sort (Newest first)
    const dateA = new Date(a.createdAt)
    const dateB = new Date(b.createdAt)
    return dateB - dateA
  })
})
</script>

<style scoped>
.list-group {
  border-radius: 0.5rem;
  overflow: hidden;
}
</style>