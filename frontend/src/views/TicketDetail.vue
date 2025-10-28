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
        <div v-else-if="store.error && !store.currentTicket" class="alert alert-danger">
          <strong>Error loading ticket:</strong> {{ store.error.message || store.error }}
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
            <div class="p-3 bg-light rounded mb-4 pre-wrap"> 
              {{ ticket.description }}
              <hr v-if="ticket.investmentProductId || isAdmin">
              <p v-if="ticket.investmentProductId" class="mb-0 small text-muted">
                <strong>Related Investment ID:</strong> {{ ticket.investmentProductId }}
              </p>
              <p v-if="isAdmin && ticket.userResponse" class="mb-0 small text-muted">
                <strong>User:</strong> {{ ticket.userResponse.name }} ({{ ticket.userResponse.email }})
              </p>
            </div>
            <h5 class="fw-bold">Response</h5>
            <div v-if="ticket.response && ticket.ticketStatus !== 'OPEN'"
                 class="p-3 rounded-3 mb-4"
                 :class="ticket.ticketStatus === 'CLOSED' ? 'bg-secondary bg-opacity-10 border border-secondary' : 'bg-primary bg-opacity-10 border border-primary'">
              <p class="pre-wrap mb-0">{{ ticket.response }}</p>
              <hr>
              <p class="mb-0 small text-muted">
                <strong>Last Updated:</strong> {{ formattedDate(ticket.updatedAt) }}
              </p>
            </div>
            <div v-else-if="ticket.ticketStatus === 'OPEN'" class="p-3 bg-light rounded text-muted mb-4">
              An administrator has not responded to this ticket yet.
            </div>
            
             <div v-else-if="!ticket.response && ticket.ticketStatus === 'RESPONDED'" class="p-3 bg-light rounded text-warning mb-4">
               Ticket marked as responded, but no response text found.
             </div>
            <div v-if="isAdmin && ticket && ticket.ticketStatus !== 'CLOSED'" class="mt-4">
             <h4 class="fw-bold">{{ ticket.ticketStatus === 'OPEN' ? 'Respond to this Ticket' : 'Add Final Comment & Close' }}</h4>
              <form @submit.prevent="handleResponse">
                <div v-if="responseError" class="alert alert-danger small p-2"> <!-- Added p-2 -->
                  {{ responseError }}
                </div>
                <div class="mb-3" v-if="ticket.ticketStatus === 'OPEN'">
                  <label for="adminPriority" class="form-label fw-bold">Set Priority</label>
                  <select id="adminPriority" v-model="selectedPriority" class="form-select">
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                  </select>
                </div>
                <div class="mb-3">
                   <label for="adminResponseText" class="form-label fw-bold">Response / Comment</label>
                  <textarea
                    id="adminResponseText"
                    v-model="responseText"
                    class="form-control"
                    rows="5"
                   :placeholder="ticket.ticketStatus === 'OPEN' ? 'Type your response here...' : 'Type final closing comment (e.g., Ticket closed - resolved)...'"
                    required
                  ></textarea>
                </div>
                <button type="submit" class="btn"
                        :class="ticket.ticketStatus === 'OPEN' ? 'btn-primary' : 'btn-danger'"
                        :disabled="store.loading">
                   <span v-if="store.loading" class="spinner-border spinner-border-sm me-2"></span>
                   <i v-else :class="ticket.ticketStatus === 'OPEN' ? 'bi bi-send-fill' : 'bi bi-lock-fill'" class="me-1"></i>
                   {{ ticket.ticketStatus === 'OPEN' ? 'Submit Response' : 'Submit & Close Ticket' }}
                </button>
              </form>
            </div>
            <div v-if="ticket && ticket.ticketStatus === 'CLOSED'" class="alert alert-secondary mt-4" role="alert">
              <i class="bi bi-lock-fill me-2"></i>
              This ticket has been closed. 
            </div>
          </div>
        </div>
         <div v-else-if="!store.loading" class="text-center text-muted mt-5">
           Ticket data could not be loaded or ticket not found.
         </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed ,watch} from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { useTicketStore } from '@/stores/ticketStore'

const props = defineProps({
  id: {
    type: [String, Number],
    required: true,
  },
})
const route = useRoute()
const router = useRouter()
const store = useTicketStore()

const ticket = computed(() => store.currentTicket)
const isAdmin = ref(false)
const responseText = ref('')
const selectedPriority = ref('MEDIUM')
const responseError = ref('')
const backLink = computed(() => {
  return isAdmin.value ? '/admin/tickets' : '/help-center/my-tickets'
})

onMounted(() => {
  const user = localStorage.getItem('currentUser')
  if (user) {
    isAdmin.value = JSON.parse(user).role === 'ADMIN'
  }
  store.error = null;
  responseError.value = '';
  store.fetchTicketById(Number(props.id)).catch(err => {
    console.error("Failed to load ticket details:", err);
    responseError.value = `Error loading ticket: ${err.message}`; 
  })
})

watch(ticket, (newTicket) => {
  if (newTicket) {
    selectedPriority.value = newTicket.ticketPriority || 'MEDIUM'; 

    if (isAdmin.value) { 
      if (newTicket.ticketStatus === 'RESPONDED') {
        responseText.value = ''; 
      } else if (newTicket.ticketStatus === 'OPEN') {
        responseText.value = '';
      }
    }
  } else {
     responseText.value = '';
     selectedPriority.value = 'LOW'; 
  }
}, { immediate: true });
async function handleResponse() {
  if (!responseText.value.trim()) {
    responseError.value = 'Response/Comment cannot be empty.'
    return
  }
   if (ticket.value.ticketStatus === 'CLOSED') {
     responseError.value = 'Cannot modify a closed ticket.';
     return;
   }
  responseError.value = ''

  try {
    await store.respondToTicket(
        ticket.value.id,
        responseText.value,
        ticket.value.ticketStatus === 'OPEN' ? selectedPriority.value : null 
    );
    if (store.currentTicket?.ticketStatus === 'RESPONDED') {
       responseText.value = '';
    }
     

  } catch (error) {
    responseError.value = error.message || 'Failed to submit update.'
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

.btn-sm {
   font-size: 0.875rem;
   padding: 0.25rem 0.5rem;
}
.alert.small { 
   font-size: 0.875rem;
   padding: 0.5rem 0.75rem;
}

.form-label {
  font-size: 0.9rem;
  color: #495057;
}
</style>

