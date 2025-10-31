
<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-10 col-lg-8">
        <div class="card shadow-sm border-0">
          <div class="card-body p-5 text-center">
            <i
              class="bi bi-question-circle-fill text-primary"
              style="font-size: 4rem"
            ></i>
            <h1 class="h2 fw-bold mt-3">Help Center</h1>
            <p class="lead text-muted">
              How can we assist you today, {{ userName }}?
            </p>

            <hr class="my-4" />

            <div v-if="userRole === 'USER'">
              <p class="mb-3">
                Need help with an investment or your account?
              </p>
              <div
                class="d-grid gap-3 d-md-flex justify-content-center"
              >
                <RouterLink
                  to="/help-center/new"
                  class="btn btn-primary btn-lg px-4"
                >
                  <i class="bi bi-plus-circle-fill me-2"></i>
                  Create a New Ticket
                </RouterLink>
                <RouterLink
                  to="/help-center/my-tickets"
                  class="btn btn-outline-secondary btn-lg px-4"
                >
                  <i class="bi bi-card-list me-2"></i>
                  View My Tickets
                </RouterLink>
              </div>
            </div>

            <div v-if="userRole === 'ADMIN'">
              <p class="mb-3">
                You are in administrator mode. You can view and respond to all
                user tickets.
              </p>
              <div class="d-grid gap-3 justify-content-center">
                <RouterLink
                  to="/admin/tickets"
                  class="btn btn-danger btn-lg px-4"
                >
                  <i class="bi bi-inbox-fill me-2"></i>
                  Manage All Tickets
                </RouterLink>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { useAuth } from '@/composables/useAuth'

const { currentUser, loadUserProfile } = useAuth()
const userName = ref('User')
const userRole = ref('USER')

onMounted(async () => {
  // Load user profile if not already loaded
  await loadUserProfile()
  
  // Set local refs from currentUser
  if (currentUser.value) {
    userName.value = currentUser.value.fullName || 'User'
    userRole.value = currentUser.value.role || 'USER'
  }
})
</script>

<style scoped>
.card {
  border-radius: 1rem;
}
.btn-lg {
  font-weight: 500;
}
</style>