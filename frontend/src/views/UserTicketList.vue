<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-10 col-lg-9">
        
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h1 class="h2 fw-bold mb-0">My  Tickets</h1>
          <RouterLink to="/help-center/new" class="btn btn-primary">
            <i class="bi bi-plus-circle-fill me-1"></i>
            Create New Ticket
          </RouterLink>
        </div>

        <div class="card shadow-sm border-0 mb-4">
          <div class="card-body p-3 p-md-4">
            <div class="row g-3 align-items-center">
              
              <div class="col-md-3">
                <label for="filterStatus" class="form-label small">Status</label>
                <select id="filterStatus" v-model="selectedStatus" class="form-select">
                  <option value="ALL">All Status</option>
                  <option value="OPEN">Open</option>
                  <option value="RESPONDED">Responded</option>
                  <option value="CLOSED">Closed</option>
                </select>
              </div>
              
              <div class="col-md-3">
                <label for="filterPriority" class="form-label small">Priority</label>
                <select id="filterPriority" v-model="selectedPriority" class="form-select">
                  <option value="ALL">All Priorities</option>
                  <option value="HIGH">High</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="LOW">Low</option>
                </select>
              </div>
              
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
        <div v-if="store.loading" class="text-center my-5">
          </div>

        <div v-else-if="store.error" class="alert alert-danger">
          </div>

        <div v-else-if="!filteredAndSortedTickets.length" class="card shadow-sm border-0 text-center p-5">
          <i class="bi bi-inbox fs-1 text-muted"></i>
          <h3 class="mt-3">No Tickets Found</h3>
          <p class="text-muted">
            You have not created any support tickets that match the current filters.
          </p>
        </div>

        <div v-else class="list-group shadow-sm">
          <TicketListItem
            v-for="ticket in filteredAndSortedTickets"
            :key="ticket.id"
            :ticket="ticket"
          />
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue' 
import { useTicketStore } from '@/stores/ticketStore'
import { RouterLink } from 'vue-router'
import TicketListItem from '@/components/TicketListItem.vue'

const store = useTicketStore()
const selectedStatus = ref('ALL')
const selectedPriority = ref('ALL')
const startDate = ref('')
const endDate = ref('')

// Fetch tickets when the component loads
onMounted(() => {
  store.fetchUserTickets().catch(err => {
    console.error("Failed to load user tickets:", err)
  })
})
function clearFilters() {
  selectedStatus.value = 'ALL' 
  selectedPriority.value = 'ALL'
  startDate.value = ''
  endDate.value = ''
}
const filteredAndSortedTickets = computed(() => {
  // Start with the full list from the store
  let tickets = [...store.userTickets];

 
  
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


  // Sort the filtered list
  return tickets.sort((a, b) => {
    const dateA = new Date(a.updatedAt || a.createdAt)
    const dateB = new Date(b.updatedAt || b.createdAt)
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