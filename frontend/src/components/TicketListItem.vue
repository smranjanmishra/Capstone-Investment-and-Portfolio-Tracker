<template>
  <RouterLink
    :to="`/ticket/${ticket.id}`"
    class="list-group-item list-group-item-action p-3"
  >
    <div class="d-flex w-100 justify-content-between">
      <h5 class="mb-1 text-primary">
        {{ ticket.subject }}
      </h5>
      <small class="text-muted">{{ formattedDate(ticket.createdAt) }}</small>
    </div>

    <div class="d-flex align-items-center justify-content-between mt-2">
      <div>
        <span class="badge me-2" :class="statusClass(ticket.ticketStatus)">
          <i :class="statusIcon(ticket.ticketStatus)" class="me-1"></i>
          {{ ticket.ticketStatus }}
        </span>
        <span class="badge bg-light text-dark">
          <i class="bi bi-tag-fill me-1"></i>
          ID: {{ ticket.id }}
        </span>
      </div>

      <div class="text-end">
        <span v-if="showUser && ticket.userResponse" class="text-muted small d-block">
          User: {{ ticket.userResponse.name }} ({{ ticket.userResponse.email }})
        </span>
        <span class="badge" :class="priorityClass(ticket.ticketPriority)">
          {{ ticket.ticketPriority }}
        </span>
        
      </div>
    </div>
  </RouterLink>
</template>

<script setup>
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
const props = defineProps({
  ticket: {
    type: Object,
    required: true,
  },
  // We add this prop to show user info on the admin list
  showUser: {
    type: Boolean,
    default: false,
  }
})

// ---  Properties for Styling ---

function statusClass(status) {
  switch (status) {
    case 'OPEN':
      return 'bg-success'
    case 'RESPONDED':
      return 'bg-info'
    case 'CLOSED':
      return 'bg-secondary'
    default:
      return 'bg-light text-dark'
  }
}

function statusIcon(status) {
  switch (status) {
    case 'OPEN':
      return 'bi bi-envelope-open-fill'
    case 'RESPONDED':
      return 'bi bi-chat-left-dots-fill'
    case 'CLOSED':
      return 'bi bi-lock-fill'
    default:
      return 'bi bi-question-circle-fill'
  }
}

function priorityClass(priority) {
  switch (priority) {
    case 'HIGH':
      return 'bg-danger'
    case 'MEDIUM':
      return 'bg-warning'
    case 'LOW':
      return 'bg-secondary'
    default:
      return 'bg-light text-dark'
  }
}

function formattedDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('en-US', {
    dateStyle: 'medium',
    timeStyle: 'short',
  })
}
</script>

<style scoped>
.list-group-item-action {
  border-radius: 0.5rem;
  margin-bottom: 0.75rem;
  border-width: 1px;
  border-color: #e9ecef;
  transition: all 0.2s ease;
}

.list-group-item-action:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-left: 4px solid var(--bs-primary);
}

.badge {
  font-size: 0.75rem;
  padding: 0.4em 0.7em;
  font-weight: 600;
  text-transform: uppercase;
}
</style>