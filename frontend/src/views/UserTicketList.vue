<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-10 col-lg-9">
        
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h1 class="h2 fw-bold mb-0">My Support Tickets</h1>
          <RouterLink to="/help-center/new" class="btn btn-primary">
            <i class="bi bi-plus-circle-fill me-1"></i>
            Create New Ticket
          </RouterLink>
        </div>

        <div v-if="store.loading" class="text-center my-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading tickets...</span>
          </div>
          <p class="mt-2 text-muted">Loading your tickets...</p>
        </div>

        <div v-else-if="store.error" class="alert alert-danger">
          <strong>Error:</strong> {{ store.error.message || store.error }}
        </div>

        <div v-else-if="!store.userTickets.length" class="card shadow-sm border-0 text-center p-5">
          <i class="bi bi-inbox fs-1 text-muted"></i>
          <h3 class="mt-3">No Tickets Found</h3>
          <p class="text-muted">
            You have not created any support tickets yet.
          </p>
        </div>

        <div v-else class="list-group shadow-sm">
          <TicketListItem
            v-for="ticket in sortedTickets"
            :key="ticket.id"
            :ticket="ticket"
          />
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue'
import { useTicketStore } from '@/stores/ticketStore'
import { RouterLink } from 'vue-router'
import TicketListItem from '@/components/TicketListItem.vue'

const store = useTicketStore()

// Fetch tickets when the component loads
onMounted(() => {
  store.fetchUserTickets().catch(err => {
    console.error("Failed to load user tickets:", err)
  })
})

// Sort tickets to show the newest (or most recently updated) first
const sortedTickets = computed(() => {
  return [...store.userTickets].sort((a, b) => {
    const dateA = new Date(a.updatedAt || a.createdAt)
    const dateB = new Date(b.updatedAt || b.createdAt)
    return dateB - dateA // Descending order
  })
})
</script>

<style scoped>
.list-group {
  border-radius: 0.5rem;
  overflow: hidden;
}
</style>