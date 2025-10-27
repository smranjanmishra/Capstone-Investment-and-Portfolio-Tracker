<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-11 col-lg-10">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h1 class="h2 fw-bold mb-0">Manage All Tickets</h1>
        </div>
        <div class="card shadow-sm border-0 mb-4">
          <div class="card-body p-3 p-md-4">
           
            <div class="row g-3 align-items-end"> 

              <!-- Filter by Status -->
              <div class="col-md-3">
                <label for="filterStatus" class="form-label small">Status</label>
                <select id="filterStatus" v-model="selectedStatus" class="form-select">
                  <option value="ALL">All Statuses</option>
                  <option value="OPEN">Open</option>
                  <option value="RESPONDED">Responded</option>
                  <option value="CLOSED">Closed</option>
                </select>
              </div>

              <!-- Filter by Priority -->
              <div class="col-md-3">
                <label for="filterPriority" class="form-label small">Priority</label>
                <select id="filterPriority" v-model="selectedPriority" class="form-select">
                  <option value="ALL">All Priorities</option>
                  <option value="HIGH">High</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="LOW">Low</option>
                </select>
              </div>

              <!-- Date Range -->
              <div class="col-md-2">
                <label for="filterDateFrom" class="form-label small">From Date</label>
                <input id="filterDateFrom" v-model="startDate" type="date" class="form-control">
              </div>
              <div class="col-md-2">
                <label for="filterDateTo" class="form-label small">To Date</label>
                <input id="filterDateTo" v-model="endDate" type="date" class="form-control">
              </div>

              <div class="col-md-2 d-grid gap-2 d-md-block text-end">
                <button class="btn btn-outline-secondary btn-sm" @click="clearFilters"> 
                  Clear
                </button>
              </div>

            </div>
          </div>
        </div>

        
        <div v-if="store.loading && !store.allTickets.length" class="text-center my-5">
           <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading tickets...</span>
          </div>
          <p class="mt-2 text-muted">Fetching all user tickets...</p>
        </div>

        <!-- Error Message -->
        <div v-else-if="store.error" class="alert alert-danger">
           <strong>Error:</strong> {{ store.error.message || store.error }}
        </div>

        <!-- Empty State -->
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
            No tickets match the current filters.
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
const selectedStatus = ref('OPEN') 
const selectedPriority = ref('ALL')
const startDate = ref('')
const endDate = ref('')

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
  store.fetchAllTickets()
}

function clearFilters() {
  selectedStatus.value = 'ALL' 
  selectedPriority.value = 'ALL'
  startDate.value = ''
  endDate.value = ''
}


// Filter and Sort tickets
const filteredTickets = computed(() => {
  let tickets = [...store.allTickets]
  
  // 1. Filter by Status
  if (selectedStatus.value !== 'ALL') {
    tickets = tickets.filter(t => t.ticketStatus === selectedStatus.value);
  }
  
  // 2. Filter by Priority
  if (selectedPriority.value !== 'ALL') {
    tickets = tickets.filter(t => t.ticketPriority === selectedPriority.value);
  }
  
  // 3. Filter by Start Date
  if (startDate.value) {
    const fromDate = new Date(startDate.value);
    fromDate.setHours(0, 0, 0, 0); // Set to start of the day
    tickets = tickets.filter(t => new Date(t.createdAt) >= fromDate);
  }
  
  // 4. Filter by End Date
  if (endDate.value) {
    const toDate = new Date(endDate.value);
    toDate.setHours(23, 59, 59, 999); // Set to end of the day
    tickets = tickets.filter(t => new Date(t.createdAt) <= toDate);
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