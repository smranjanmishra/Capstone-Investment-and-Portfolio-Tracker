<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-lg-10">

        <div class="mb-3">
          <RouterLink :to="backLink" class="text-decoration-none">
            <i class="bi bi-arrow-left-circle me-1"></i>
            Back to Ticket List
          </RouterLink>
        </div>

        <div v-if="store.loading && !store.currentTicket" class="text-center my-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading ticket...</span>
          </div>
          <p class="mt-2 text-muted">Loading ticket details...</p>
        </div>

        <div v-else-if="store.error" class="alert alert-danger">
          <strong>Error:</strong> {{ store.error.message || store.error }}
        </div>

        <div v-else-if="ticket" class="card shadow-sm border-0">
          <div class="card-header bg-light p-4">
            <h2 class="h4 mb-0 fw-bold">Ticket #{{ ticket.id }}: {{ ticket.subject }}</h2>
            <div class="d-flex align-items-center mt-2">
              <span class="badge me-2" :class="statusClass(ticket.ticketStatus)">
                <i :class="statusIcon(ticket.ticketStatus)" class="me-1"></i>
                {{ ticket.ticketStatus }}
              </span>
              <span class="badge" :class="priorityClass(ticket.ticketPriority)">
                {{ ticket.ticketPriority }} PRIORITY
              </span>
              <span class="text-muted ms-auto small">
                Created: {{ formattedDate(ticket.createdAt) }}
              </span>
            </div>
          </div>

          <div class="card-body p-4 p-md-5">
            <h5 class="fw-bold">User's Request</h5>
            <div class="p-3 bg-light rounded mb-4">
              <p style="white-space: pre-wrap;">{{ ticket.description }}</p>
              <hr v-if="ticket.investmentProductId">
              <p v-if="ticket.investmentProductId" class="mb-0 small text-muted">
                <strong>Related Investment ID:</strong> {{ ticket.investmentProductId }}
              </p>
              <p v-if="isAdmin" class="mb-0 small text-muted">
                <strong>User:</strong> {{ ticket.userResponse.name }} ({{ ticket.userResponse.email }})
              </p>
            </div>

            <h5 class="fw-bold">Response</h5>
            <div v-if="ticket.response" class="p-3 bg-primary bg-opacity-10 rounded-3 border border-primary">
              <p style="white-space: pre-wrap;">{{ ticket.response }}</p>
              <hr>
              <p class="mb-0 small text-muted">
                <strong>Responded at:</strong> {{ formattedDate(ticket.updatedAt) }}
              </p>
            </div>
            <div v-else class="p-3 bg-light rounded text-muted">
              An administrator has not responded to this ticket yet.
            </div>

            <div v-if="isAdmin && ticket.ticketStatus === 'OPEN'" class="mt-5">
              <h4 class="fw-bold">Respond to this Ticket</h4>
              <form @submit.prevent="handleResponse">
                <div v-if="responseError" class="alert alert-danger small">
                  {{ responseError }}
                </div>
                <div class="mb-3">
                  <textarea
                    v-model="responseText"
                    class="form-control"
                    rows="5"
                    placeholder="Type your response here..."
                    required
                  ></textarea>
                </div>
                <button type="submit" class="btn btn-primary" :disabled="store.loading">
                   <span
                    v-if="store.loading"
                    class="spinner-border spinner-border-sm me-2"
                  ></span>
                  <i v-else class="bi bi-send-fill me-1"></i>
                  Submit Response
                </button>
              </form>
            </div>

          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { useTicketStore } from '@/stores/ticketStore'

// Get props (ticket ID) and stores
const props = defineProps({
  id: {
    type: [String, Number],
    required: true,
  },
})
const route = useRoute()
const router = useRouter()
const store = useTicketStore()

// --- State ---
const ticket = computed(() => store.currentTicket)
const isAdmin = ref(false)
const responseText = ref('')
const responseError = ref('')
const backLink = computed(() => {
  return isAdmin.value ? '/admin/tickets' : '/help-center/my-tickets'
})

// ---  Fetch Data ---
onMounted(() => {
  // Check user role from localStorage
  const user = localStorage.getItem('currentUser')
  if (user) {
    isAdmin.value = JSON.parse(user).role === 'ADMIN'
  }

  // Fetch the specific ticket details
  // The store logic will find it in its list or re-fetch
  store.fetchTicketById(Number(props.id)).catch(err => {
    console.error("Failed to load ticket details:", err)
  })
})

// --- Methods ---
async function handleResponse() {
  if (!responseText.value.trim()) {
    responseError.value = 'Response cannot be empty.'
    return
  }
  responseError.value = ''

  try {
    await store.respondToTicket(ticket.value.id, responseText.value)
    // The  Ticket store automatically updates currentTicket, so no extra steps needed.
    responseText.value = '' // Clear form
  } catch (error) {
    responseError.value = error.message || 'Failed to submit response.'
  }
}

// --- Helper Functions ---
function formattedDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('en-US', {
    dateStyle: 'medium',
    timeStyle: 'short',
  })
}

function statusClass(status) {
  switch (status) {
    case 'OPEN': return 'bg-success';
    case 'RESPONDED': return 'bg-info';
    case 'CLOSED': return 'bg-secondary';
    default: return 'bg-light text-dark';
  }
}

function statusIcon(status) {
  switch (status) {
    case 'OPEN': return 'bi bi-envelope-open-fill';
    case 'RESPONDED': return 'bi bi-chat-left-dots-fill';
    case 'CLOSED': return 'bi bi-lock-fill';
    default: return 'bi bi-question-circle-fill';
  }
}

function priorityClass(priority) {
  switch (priority) {
    case 'HIGH': return 'bg-danger';
    case 'MEDIUM': return 'bg-warning text-dark';
    case 'LOW': return 'bg-secondary';
    default: return 'bg-light text-dark';
  }
}
</script>

<style scoped>
.card-header {
  border-bottom: 0;
}
.card {
  border-radius: 1rem;
  overflow: hidden;
}
.badge {
  font-size: 0.8rem;
  padding: 0.4em 0.7em;
  font-weight: 600;
  text-transform: uppercase;
}

.pre-wrap {
  white-space: pre-wrap;
  word-break: break-word;
}
</style>