<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-10 col-lg-8">
        <div class="card shadow-sm border-0">
          <div class="card-body p-4 p-md-5">
            <div class="text-center mb-4">
              <i
                class="bi bi-plus-circle-fill text-primary"
                style="font-size: 3rem"
              ></i>
              <h1 class="h3 fw-bold mt-2">Create New Support Ticket</h1>
              <p class="text-muted">
                Please provide details about your issue.
              </p>
            </div>

            <div
              v-if="successMessage"
              class="alert alert-success"
              role="alert"
            >
              <i class="bi bi-check-circle-fill me-2"></i>
              {{ successMessage }}
            </div>

            <div v-if="ticketStore.error" class="alert alert-danger">
              <i class="bi bi-exclamation-triangle-fill me-2"></i>
              <strong>Error:</strong> {{ ticketStore.error.message || ticketStore.error }}
            </div>

            <form @submit.prevent="handleSubmit" v-if="!successMessage">
              <div class="mb-3">
                <label for="subject" class="form-label fw-bold"
                  >Subject</label
                >
                <input
                  id="subject"
                  v-model="formData.subject"
                  type="text"
                  class="form-control form-control-lg"
                  placeholder="e.g., 'Problem with my investment'"
                  required
                />
              </div>

              <div class="mb-3">
                <label for="investment" class="form-label fw-bold"
                  >Related Investment (Optional)</label
                >
                <select
                  id="investment"
                  v-model="formData.investmentProductId"
                  class="form-select form-select-lg"
                  @focus="ensurePortfolioLoaded"
                >
                  <option value="">-- General Inquiry --</option>
                  <option
                    v-if="portfolioStore.loading"
                    disabled
                  >
                    Loading your investments...
                  </option>
                  <option
                    v-for="item in portfolioStore.portfolioItems"
                    :key="item.investmentProductId"
                    :value="item.investmentProductId"
                  >
                    {{ item.investmentProductName }} (ID: {{ item.investmentProductId }})
                  </option>
                </select>
                <div class="d-flex align-items-center mt-1">
                  <div v-if="portfolioStore.error" class="text-danger small">
                    Could not load your investments.
                  </div>
                  <button
                    v-if="portfolioStore.error"
                    type="button"
                    class="btn btn-sm btn-outline-secondary ms-2"
                    @click="ensurePortfolioLoaded(true)"
                  >
                    Retry
                  </button>
                </div>
              </div>

              <div class="mb-4">
                <label for="description" class="form-label fw-bold"
                  >Description</label
                >
                <textarea
                  id="description"
                  v-model="formData.description"
                  class="form-control"
                  rows="6"
                  placeholder="Describe your issue in detail..."
                  required
                ></textarea>
              </div>
              <div v-if="formError" class="alert alert-danger small mb-3">
                {{ formError }}
              </div>

              <div class="d-grid">
                <button
                  type="submit"
                  class="btn btn-primary btn-lg fw-bold"
                  :disabled="ticketStore.loading || !isFormValid"
                >
                  <span
                    v-if="ticketStore.loading"
                    class="spinner-border spinner-border-sm me-2"
                  ></span>
                  <i v-else class="bi bi-send-fill me-2"></i>
                  Submit Ticket
                </button>
              </div>
            </form>

            <div class="text-center mt-4">
              <RouterLink to="/help-center" class="text-decoration-none">
                <i class="bi bi-arrow-left-circle me-1"></i>
                Back to Help Center
              </RouterLink>
              <span class="mx-2 text-muted">|</span>
              <RouterLink to="/help-center/my-tickets" class="text-decoration-none">
                View My Tickets
                <i class="bi bi-arrow-right-circle ms-1"></i>
              </RouterLink>
            </div>

          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useTicketStore } from '@/stores/ticketStore'
import { usePortfolioStore } from '@/stores/portfolioStore'

const ticketStore = useTicketStore()
const portfolioStore = usePortfolioStore()
const router = useRouter()

const successMessage = ref('')

const formData = reactive({
  subject: '',
  description: '',
  investmentProductId: '',
})

const formError = ref('')
const isFormValid = computed(() => {
  const subjectOk = !!(formData.subject && formData.subject.trim().length > 0)
  const descOk = !!(formData.description && formData.description.trim().length > 0)
  return subjectOk && descOk
})

// Fetch the user's portfolio when the component loads
onMounted(() => {
  portfolioStore.fetchPortfolio().catch(err => {
    console.error("Failed to load portfolio for dropdown:", err)
  })
})

async function ensurePortfolioLoaded(force = false) {
  if (portfolioStore.loading) return
  if (portfolioStore.portfolioItems && portfolioStore.portfolioItems.length > 0 && !force) return
  try {
    await portfolioStore.fetchPortfolio()
  } catch (err) {
    console.error('Portfolio load retry failed:', err)
  }
}

async function handleSubmit() {
  formError.value = ''
  const trimmedSubject = formData.subject ? formData.subject.trim() : ''
  const trimmedDescription = formData.description ? formData.description.trim() : ''

  if (!trimmedSubject || !trimmedDescription) {
    formError.value = 'Blank spaces are not valid. Please provide a valid subject and description.'
    return
  }

  try {
    const payload = {
      subject: trimmedSubject,
      description: trimmedDescription,
      investmentProductId: formData.investmentProductId === '' ? null : formData.investmentProductId
    }

    await ticketStore.createTicket(payload)
    successMessage.value = 'Your ticket has been submitted successfully! We will get back to you soon.'
    formData.subject = ''
    formData.description = ''
    formData.investmentProductId = ''
    setTimeout(() => {
      router.push('/help-center/my-tickets')
    }, 2000)

  } catch (error) {
    console.error('Submission failed:', error)
    formError.value = ticketStore.error?.message || ticketStore.error || 'Failed to submit ticket.'
  }
}
</script>

<style scoped>
.card {
  border-radius: 1rem;
}
.form-control-lg {
  padding: 0.75rem 1rem;
}
.form-select-lg {
  padding: 0.75rem 1rem;
  font-size: 1rem; 
}
.form-label {
  font-size: 0.9rem;
  color: #495057;
}
</style>